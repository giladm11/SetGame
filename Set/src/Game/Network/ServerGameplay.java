package Game.Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;

import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;

import Game.BoardChange;
import Game.BoardLocation;
import Game.Gameplay;
import Game.Player;
import Game.Network.Messages.MessageType;
import Game.Network.Messages.NetworkSetCalled;

public class ServerGameplay extends Gameplay implements Disposable {
	public static final int PORT = 34545;
	
	private ServerSocket serverSocket;

	public ServerGameplay(ArrayList<Player> players, ServerSocket serverSocket)
	{
		super(players);
		this.serverSocket = serverSocket;
	}
	
	@Override
	public void dispose() {
		for (int i = 0; i < this.players.size(); i++) {
			((NetworkPlayer)this.players.get(i)).dispose();
		}
		
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void prepareGame() {
		if (this.isGameActive)
		{				
			super.prepareGame();
		}
	}
	
	public void StartGame(int playersCount)
	{
		while (this.players.size() < playersCount)
		{
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			this.isGameActive = true;
			this.prepareGame();
			this.SendPlayersUpdate();
			this.updateAllGameGuis();
	}
	
	@Override
	public boolean CallSet(Player player, BoardLocation[] locations) {
		synchronized (this) {
			if (this.IsSet(locations))
			{
				this.SendPlayersMessage(MessageType.CALL_SET, new NetworkSetCalled(player.getName(), locations, new Date()));
				
				boolean result = super.CallSet(player, locations);
				
				this.SendPlayersUpdate();
				
				return result;
			}
			else
			{
				return false;
			}
		}
	}
	
	private void SendPlayersMessage(MessageType message, Object data)
	{
		this.UpdateAvialablePlayers();
		
		for (Player currentPlayer : this.players)
		{
			((NetworkPlayer)currentPlayer).SendMessage(message, data);
		}
	}
	
	public void RemovePlayer(NetworkPlayer player)
	{
		this.players.remove(player);
		player.dispose();
	}
	
	public void AddPlayer(NetworkPlayer p)
	{
		p.setGame(this);
		
		this.players.add(p);
	}
	
	public void SendPlayersUpdate()
	{
		for (Player currentPlayer : this.players) {
			((NetworkPlayer)currentPlayer)
			.SendMessage(MessageType.GET_PLAYERS,
						 this.getNormalPlayers(currentPlayer));
		}
		
		this.updateAllGameGuis();
	}
	
	public Player[] getNormalPlayers(Player player)
	{
		Player[] arrPlayers = new Player[this.players.size()];
		
		// Start from the second one
		int nPos = 1;
		
		for (int i = 0; i < arrPlayers.length; i++) {
			arrPlayers[((this.players.get(i) == player) ? 0 : (nPos++))] = new Player(this.players.get(i));
		}
		
		return arrPlayers;
	}
	
	@Override
	protected void NotifyPlayerForChanges(BoardChange[] changes) {
		this.SendPlayersMessage(MessageType.CARD_ON_DECK, this.getCardsOnDeck());
		super.NotifyPlayerForChanges(changes);
	}
	
	public boolean UpdateAvialablePlayers()
	{
		boolean isPlayerQuited = false;
		
		for (Player player : this.getPlayers()) {
			if(!((NetworkPlayer)player).isAlive())
			{
				isPlayerQuited = true;
				this.RemovePlayer((NetworkPlayer)player);
			}
		}
		
		if (isPlayerQuited)
		{
			this.SendPlayersUpdate();
		}
		
		return isPlayerQuited;
	}
	
	@Override
	public boolean canDrawCards() {
		return false;
	}
	
	@Override
	public boolean canPlayAgain() {
		return false;
	}
}
