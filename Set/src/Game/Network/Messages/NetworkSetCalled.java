package Game.Network.Messages;

import java.io.Serializable;
import java.util.Date;

import Game.BoardLocation;

public class NetworkSetCalled implements Serializable
{
	private String playerName;
	private BoardLocation[] locations;
	private Date date;
	
	public NetworkSetCalled(String playerName,
							BoardLocation[] locations,
							Date date) {
		super();
		this.playerName = playerName;
		this.locations = locations;
		this.date = date;
	}
	public String getPlayerName() {
		return playerName;
	}
	public BoardLocation[] getLocations() {
		return locations;
	}
	
	public Date getDate() {
		return date;
	}
}
