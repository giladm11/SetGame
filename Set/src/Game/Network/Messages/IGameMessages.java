package Game.Network.Messages;

import java.util.ArrayList;

import Game.BoardChange;
import Game.Card;
import Game.Player;

public interface IGameMessages
{
	void boardChangedReceived(ArrayList<BoardChange> changes);
	void setCalled(NetworkSetCalled set);
	void playersReceived(ArrayList<Player> players);
	void playerQuited(String playerName);
	void boardReceived(Card[][] board);
	void cardsOnDeckReceived(int number);
}
