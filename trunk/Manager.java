
public class Manager extends Thread{
	private PageidList _visited;
	private PageidList _todo;
	private int _numcrawlers;
	
	private boolean _timeToWait;
	private int _holding;
	private int _lastWrite;
	
	public Manager(PageidList v, PageidList t, int num)
	{
		_visited = v;
		_todo = t;
		_numcrawlers = num;
		
		_lastWrite = _visited.getSize();
	}
	
	public synchronized boolean getTimeToWait() {
		notify();
		return _timeToWait; 
	} 
	public synchronized void addHolding() {
		_holding++;
		notify();
	}
	
	public void run()
	{
		int _timeToWrite = 0;
		_holding = 0;
		
		while (true)
		{		
			if (_timeToWrite == 0) 
			{
				int i = _visited.getSize() - _lastWrite;
				if (i > 10000)
				{
					_timeToWait = true;
					_timeToWrite = i;
					System.out.println("GOING ON HOLD!!!" + i);
				}
			}	
			
			if (_timeToWrite > 0) 
			{
				if (_numcrawlers == _holding) // if every thread is waiting ..
				{
					System.out.println("Every crawler is on hold!!!\n  Writing todo list...");
					_todo.writeListToFile("todo");
					System.out.println("  Writing visited list");
					_visited.writeListToFile("visited");

					_lastWrite = _visited.getSize();
					_holding = 0;
					_timeToWrite = 0;
					_timeToWait = false;
					
					System.out.println("We can continue now!");
				}
			}
			
		}
	}
}
