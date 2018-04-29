package Game.Interfaces;

import Game.BoardLocation;
import Game.Player;

public interface IGuiUpdater {
	void updateGameGui();
	void callSetUpdateGameGui(BoardLocation[] location, Player player);
	void gameOver(Player[] winners);
}
