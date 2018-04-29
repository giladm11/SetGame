package Game.Network.Messages;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	
	private MessageType type;
	private Object data;
	private int key;
	private Date date;
	
	public Message(MessageType type, Object data, int key, Date date) {
		super();
		this.type = type;
		this.data = data;
		this.key = key;
		this.date = date;		
	}
	public MessageType getType() {
		return type;
	}
	public Object getData() {
		return data;
	}	
	
	public int getKey() {
		return key;
	}
	
	public Date getDate() {
		return date;
	}
}
