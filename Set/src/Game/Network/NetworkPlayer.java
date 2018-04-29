package Game.Network;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Game.BoardChange;
import Game.Card;
import Game.Player;
import Game.Interfaces.NotifyChangesListener;
import Game.Network.Messages.IGameMessages;
import Game.Network.Messages.MessageType;
import Game.Network.Messages.MessagesManager;
import Game.Network.Messages.NetworkSetCalled;

import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;

public class NetworkPlayer extends Player implements Disposable, IGameMessages, NotifyChangesListener {
	
	private final char EXTRA_CHAR = 'I';
	
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private MessagesManager msgMgr;
	private ServerGameplay game;
	
	public NetworkPlayer(Socket socket, String name,
			int color, ServerGameplay game) throws IOException {
		super(null, name, color);
		this.socket = socket;
		this.setGame(game);
		try {
			this.inputStream = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.AddChangesListener(this);
		
		msgMgr = new MessagesManager(inputStream, outputStream, this);
	}
	
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void dispose() {
		this.msgMgr.dispose();
		try {
			this.outputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.inputStream.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ObjectInputStream getInputStream() {
		return inputStream;
	}
	
	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}
	
	public void setGame(ServerGameplay game) {
		this.game = game;
	}

	// ****** Game notifier ******
	
	@Override
	public void boardChangedReceived(ArrayList<BoardChange> changes) {
		// Players don't change boards
	}

	@Override
	public void playersReceived(ArrayList<Player> players) {
		if (players != null)
		{
			if (players.size() == 1)
			{
				this.setName(players.get(0).getName());
				this.setColor(players.get(0).getColor());
				this.game.AddPlayer(this);
				this.game.SendPlayersUpdate();
			}
		}
		else
		{
			this.SendMessage(MessageType.GET_PLAYERS, this.game.getNormalPlayers(this));
		}
	}

	@Override
	public void setCalled(NetworkSetCalled set) {
		this.game.CallSet(this, set.getLocations());
	}
	
	@Override
	public void playerQuited(String p) {
		this.game.RemovePlayer(this);
	}
	
	// ****** End game notifier *****

	@Override
	public void notifyChanges(BoardChange[] changes) {
		this.SendMessage(MessageType.GET_BOARD_CHANGE, changes);
	}
	
	public boolean SendMessage(MessageType message, Object data)
	{
		if (this.msgMgr.isAlive())
		{
			this.msgMgr.SendMessage(message, data);
			return true;
		}
		
		return false;
	}

	@Override
	public void boardReceived(Card[][] board) {
		this.SendMessage(MessageType.GET_BOARD, this.game.getBoard());
	}

	private String getValidName(String name)
	{
		String newName = name;
		
		for (int i = 0; i < this.game.getPlayers().size(); i++)
		{
			if (this.game.getPlayers().get(i).getName().compareTo(newName) == 0)
			{
				newName += EXTRA_CHAR;
				i = -1;
			}
		}
		
		return newName;
	}
	
	@Override
	public void setName(String name) {
		if (this.game != null)
		{
			super.setName(this.getValidName(name));
		}
	}

	@Override
	public void cardsOnDeckReceived(int number) {
		this.SendMessage(MessageType.CARD_ON_DECK, this.game.getDeck().size());
	}
	
	public boolean isAlive()
	{
		return this.msgMgr.isAlive();
	}
}
