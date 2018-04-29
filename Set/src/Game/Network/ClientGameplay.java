package Game.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import Game.BoardChange;
import Game.BoardLocation;
import Game.Card;
import Game.Gameplay;
import Game.Player;
import Game.Interfaces.NotifyChangesListener;
import Game.Network.Messages.IGameMessages;
import Game.Network.Messages.MessageType;
import Game.Network.Messages.MessagesManager;
import Game.Network.Messages.NetworkSetCalled;

import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;

public class ClientGameplay extends Gameplay implements NotifyChangesListener,
		Disposable, IGameMessages {
	private static final int SET_WAIT = 1000;
	
	private Socket server;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private MessagesManager msgMgr;
	private Player currentPlayer;
	private Hashtable<String, Player> playerByName;
	private int cardsOnDeck = 0;
	private boolean setCalled;
	private Vector<BoardChange> setChanges;
	private Timer timer;

	public ClientGameplay(ArrayList<Player> players, Player currentPlayer, Socket server)
	{
		super(players);
		this.maxHintCount = 0;
		this.currentPlayer = currentPlayer;
		this.playerByName = new Hashtable<String, Player>();
		this.setPlayers(new Player[]{currentPlayer});
		this.server = server;
		this.setCalled = false;
		this.timer = new Timer();
		this.setChanges = new Vector<BoardChange>();
		
		// Set streams
		try {
			output = new ObjectOutputStream(server.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Listen to notifications
		msgMgr = new MessagesManager(input, output, this);
		msgMgr.SendMessage(MessageType.GET_PLAYERS, new Player[]{new Player(this.getCurrentPlayer())});
	}

	@Override
	public void notifyChanges(BoardChange[] changes) {
		for (BoardChange boardChange : changes) {
			if (boardChange.getNewCard() == null) {
				this.RemoveCards(new BoardLocation[]{boardChange.getLocation()});
			} else {
				this.board.addCard(new Card(boardChange.getNewCard()));
			}
		}

		this.calculateAndUpdateBoardSets();
		this.updateAllGameGuis();
		
		super.NotifyPlayerForChanges(changes);
	}
	
	@Override
	public boolean CallSet(Player p, BoardLocation[] locations) {
		if (this.msgMgr.isAlive())
		{
			this.msgMgr.SendMessage(MessageType.CALL_SET,
					new NetworkSetCalled(p.getName(), locations, null));
			return super.IsSet(locations);
		}
		else
		{
			this.updateAllGameOver(null);
			return false;
		}
	}

	@Override
	public void dispose() {
		this.msgMgr.SendMessage(MessageType.QUIT_GAME, null);
		this.msgMgr.dispose();
		try {
			this.input.close();
		} catch (IOException e1) {
		}
		try {
			this.output.close();
		} catch (IOException e) {
		}
		try {
			this.server.close();
		} catch (IOException e) {
		}
	}

	@Override
	protected void prepareGame() {
		isGameActive = true;
	}
	
	@Override
	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}


	@Override
	public void playerQuited(String playerName) {
		for (Player currentPlayer : this.players) {
			if (currentPlayer.getName() == playerName)
			{
				this.players.remove(currentPlayer);
				this.updateAllGameGuis();
				break;
			}
		}
	}
	
	//  ***** IGame messages ****
	
	@Override
	public void boardChangedReceived(ArrayList<BoardChange> changes) {
		synchronized(this)
		{
			if (this.setCalled)
			{
				for (BoardChange boardChange : changes) {
					this.setChanges.add(boardChange);
				}
			}
			else
			{
				this.notifyChanges((BoardChange[])changes.toArray());
			}
		}
	}

	@Override
	public void setCalled(NetworkSetCalled set) {
		synchronized (this) {
		this.setCalled = true;
		
		Calendar c = Calendar.getInstance();
		c.setTime(set.getDate());
		c.add(Calendar.MILLISECOND, SET_WAIT);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				doSetChanges();
			}
		}, c.getTime());
		}
		
		this.playerByName.get(set.getPlayerName()).addScore();
		this.updateSetAllGameGuis(set.getLocations(), this.playerByName.get(set.getPlayerName()));
	}

	@Override
	public void playersReceived(ArrayList<Player> players) {
		this.setPlayers((Player[])players.toArray());
		this.updateAllGameGuis();
	}
	
	@Override
	public void boardReceived(Card[][] board) {
		
	}
	
	private void setPlayers(Player[] players)
	{
		this.playerByName.clear();
		this.players.clear();
		
		for (Player player : players) {
			this.players.add(player);
			this.playerByName.put(player.getName(), player);
		}
		
		this.currentPlayer = players[0];
	}

	@Override
	public void cardsOnDeckReceived(int number) {
		this.cardsOnDeck = number;
	}

	@Override
	public int getCardsOnDeck() {
		return this.cardsOnDeck;
	}
	
	@Override
	public Vector<BoardChange> popCards(int numberOfCards) {
		return null;
	}
	
	private void doSetChanges()
	{
		synchronized (this)
		{
			this.setCalled = false;
			this.notifyChanges((BoardChange[]) this.setChanges.toArray(new BoardChange[this.setChanges.size()]));
			this.setChanges.clear();
		}
	}
	
	@Override
	protected void updateAllGameGuis() {
		if (!setCalled)
		{
			super.updateAllGameGuis();
		}
	}
	
	@Override
	public void RemoveThisPlayer() {
		this.dispose();
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
