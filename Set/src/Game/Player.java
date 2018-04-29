package Game;

import java.io.Serializable;
import java.util.ArrayList;

import Game.Interfaces.NotifyChangesListener;

public class Player implements Serializable {
	private String name;
	private int score;
	private int color;
	private transient ArrayList<NotifyChangesListener> InvokeBoardChange;
	
	public Player(NotifyChangesListener boardChange, String name, int color) {
		super();
		this.InvokeBoardChange = new ArrayList<NotifyChangesListener>();
		this.AddChangesListener(boardChange);
		this.name = name;
		this.score = 0;
		this.color = color;
	}
	
	public Player(Player oldPlayer)
	{
		this(null, oldPlayer.getName(), oldPlayer.getColor());
		this.score = oldPlayer.getScore();
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public int getColor() {
		return color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addScore() {
		this.score++;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	public void AddChangesListener(NotifyChangesListener listener)
	{
		if (listener != null)
		{
			if (this.InvokeBoardChange == null)
				this.InvokeBoardChange = new ArrayList<NotifyChangesListener>();
			this.InvokeBoardChange.add(listener);
		}
	}
	
	public void InvokeChangesNotification(BoardChange[] changes)
	{
		if (this.InvokeBoardChange != null)
		{
			for (NotifyChangesListener notifyChangesListener : this.InvokeBoardChange) {
				notifyChangesListener.notifyChanges(changes);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Player name: " + this.getName() + "\nPlayer score: " + this.getScore();
	}
}
