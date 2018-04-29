package Game;

public enum Filling
{
	EMPTY(1),
	HALF(2),
	FULL(3);
	
	private int value;
	
	private Filling(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
}
