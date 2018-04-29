package Game.Interfaces;

import Game.BoardChange;

public interface NotifyChangesListener {
	void notifyChanges(BoardChange[] changes);
}
