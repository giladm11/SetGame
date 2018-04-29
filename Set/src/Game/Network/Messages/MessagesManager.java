package Game.Network.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import Game.BoardChange;
import Game.Card;
import Game.Player;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sun.glass.ui.CommonDialogs.Type;
import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.sun.media.jfxmediaimpl.MediaDisposer.Disposable;

public class MessagesManager implements Disposable, Runnable {
	private static Gson gson = new Gson();
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private IGameMessages actions;
	private boolean isAlive = true;
	private Thread listener;
	private Hashtable<MessageType, ReceivedFunction> hshFunctions;

	private Thread sender;
	private Vector<Message> waitingList;
	
	// Hashtable for messages
	private Hashtable<Integer, Message> notAcked;
	private Hashtable<Integer, Message> skipped;
	private int sendedIndex;
	private int recievedIndex;
	private Thread fixBrokenMessages;
	
	// Locks
	private Object waitingListLock;
	private Object increaseSendedLock;

	public MessagesManager(ObjectInputStream inputStream,
			final ObjectOutputStream outputStream, final IGameMessages actions) {
		super();
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.actions = actions;
		this.notAcked = new Hashtable<Integer, Message>();
		this.skipped = new Hashtable<Integer, Message>();
		this.sendedIndex = 0;
		this.recievedIndex = 0;
		
		// Locks
		this.waitingListLock = new Object();
		this.increaseSendedLock = new Object();

		// Initialize the functions manager
		this.hshFunctions = new Hashtable<MessageType, ReceivedFunction>();

		this.hshFunctions.put(MessageType.GET_BOARD_CHANGE,
				new ReceivedFunction() {
					@Override
					public void ReceiveData(Message msg) {
						actions.boardChangedReceived((ArrayList<BoardChange>) msg
								.getData());
					}
				});
		this.hshFunctions.put(MessageType.CALL_SET, new ReceivedFunction() {
			@Override
			public void ReceiveData(Message msg) {
				actions.setCalled((NetworkSetCalled) msg.getData());
			}
		});
		this.hshFunctions.put(MessageType.GET_PLAYERS, new ReceivedFunction() {
			@Override
			public void ReceiveData(Message msg) {
				ArrayList<Player> players = 
						ConvertArrayFromGson(
								(ArrayList<LinkedTreeMap<String, String>>)msg.getData(), Player.class);
				
				actions.playersReceived(players);
			}
		});
		this.hshFunctions.put(MessageType.QUIT_GAME, new ReceivedFunction() {
			@Override
			public void ReceiveData(Message msg) {
				actions.playerQuited((String) msg.getData());
			}
		});

		this.hshFunctions.put(MessageType.GET_BOARD, new ReceivedFunction() {
			@Override
			public void ReceiveData(Message msg) {
				actions.boardReceived((Card[][]) msg.getData());
			}
		});

		this.hshFunctions.put(MessageType.CARD_ON_DECK, new ReceivedFunction() {
			@Override
			public void ReceiveData(Message msg) {
				actions.cardsOnDeckReceived((Integer)msg.getData());
			}
		});

		this.hshFunctions.put(MessageType.SUCCESSFULY_MSSAGE,
				new ReceivedFunction() {
					@Override
					public void ReceiveData(Message msg) {
						notAcked.remove(msg.getKey());
					}
				});
		this.hshFunctions.put(MessageType.NOT_SUCCESSFULY_MESSAGE,
				new ReceivedFunction() {
					@Override
					public void ReceiveData(Message msg) {
						SendMessage(notAcked.get(msg.getKey()));
					}
				});

		fixBrokenMessages = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isAlive) {
					try {
						synchronized (notAcked) {
							notAcked.wait();
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					int n = recievedIndex;
					while (n < recievedIndex) {
						SendMessage(MessageType.NOT_SUCCESSFULY_MESSAGE, n);
						try {
							Thread.sleep(0);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		fixBrokenMessages.start();

		// Listen for messages
		listener = new Thread(this);
		listener.start();

		waitingList = new Vector<Message>();
		// Sender thread
		sender = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isAlive) {
					while (waitingList.size() > 0) {
						Message msg = waitingList.remove(0);
						try {
							outputStream.writeObject(gson.toJson(msg));
							outputStream.flush();
						} catch (SocketException e) {
							dispose();
						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					try {
						synchronized (waitingListLock) {
							waitingListLock.wait();
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					synchronized (notAcked) {
						notAcked.notify();
					}
				}
					
					try {
						synchronized (waitingListLock) {
							waitingListLock.wait();
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					synchronized (notAcked) {
						notAcked.notify();
					}
				}
		});

		sender.start();
	}

	@Override
	public void run() {
		// wait for messages
		while (this.isAlive) {
			try {
				// Get message
				//Message recievedMessage = (Message) inputStream.readObject();
				String strMessage = (String)inputStream.readObject();
				// Convert message
				Message recievedMessage = gson.fromJson(
						strMessage, Message.class);
				
				// Handle it
				this.handleMessage(recievedMessage);
			} catch (Exception ex) {
				this.SendMessage(MessageType.NOT_SUCCESSFULY_MESSAGE,
						recievedIndex);
			}
		}
	}

	public void SendMessage(MessageType type, Object data) {
		int messageKey = this.increaseSended();
		Message msg = new Message(type, data, messageKey, new Date());
		this.SendMessage(msg);
	}

	public void SendMessage(Message msg) {
		
		notAcked.put(msg.getKey(), msg);
		
		this.waitingList.add(msg);
		
		Thread notifyWatingList = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					synchronized (waitingListLock) {
						waitingListLock.notify();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		notifyWatingList.start();
	}

	@Override
	public void dispose() {
		this.isAlive = false;
		listener.stop();
		fixBrokenMessages.stop();
		sender.stop();
	}

	private int increaseSended() {
		synchronized (this.increaseSendedLock) {
			return (sendedIndex++);
		}
	}

	private void handleMessage(Message msg) {
			if (msg.getType() != MessageType.SUCCESSFULY_MSSAGE) {
				// Check if the message is good
				this.SendMessage(MessageType.SUCCESSFULY_MSSAGE, msg.getKey());
			}
			
			synchronized (this) {
			// Act on it
			if (msg.getKey() == this.recievedIndex) {
				recievedIndex++;
				hshFunctions.get(msg.getType()).ReceiveData(msg);

				while (this.skipped.containsKey(recievedIndex)) {
					Message execute = this.skipped.get(recievedIndex);
					this.skipped.remove(recievedIndex);
					this.hshFunctions.get(execute.getType()).ReceiveData(execute);
					recievedIndex++;
				}
			} else if (msg.getKey() > this.recievedIndex) {
				this.skipped.put(msg.getKey(), msg);
				this.SendMessage(MessageType.NOT_SUCCESSFULY_MESSAGE,
						this.recievedIndex);
			}

		}
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public <T> ArrayList<T> ConvertArrayFromGson(
			ArrayList<LinkedTreeMap<String, String>> map, Class<T> c)
	{
		ArrayList<T> array = new ArrayList<T>();
		
		for (LinkedTreeMap<String, String> node : map) {
			array.add((T)gson.fromJson(node.toString(), c.getClass()));
		}
		
		return array;
	}
}