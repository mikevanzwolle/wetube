
public class Manager extends Thread{
	private PageidList _visited;
	private PageidList _todo;
	private Network _network;
	private int _numcrawlers;
	
	private boolean _timeToWait;
	private int _holding;
	private int _lastWrite;
	private int _networkwritesize;
	
	public Manager(PageidList v, PageidList t, Network n, int num, int w)
	{
		_visited = v;
		_todo = t;
		_network = n;
		_numcrawlers = num;
		
		_lastWrite = (_visited.getSize() - _todo.getSize());
		
		_networkwritesize = w;
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
				int i = (_visited.getSize()-_todo.getSize()) - _lastWrite;
				if (i > _networkwritesize)
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
					int i = (_visited.getSize() - _todo.getSize());
					
					System.out.println("Every crawler is on hold!!! (network size: " + i + ")");
					System.out.print(" - Writing todo list...");
					_todo.writeListToFile("todo_" + i);
					System.out.print("done\n - Writing visited list...");
					_visited.writeListToFile("visited_" + i);
					System.out.print("done\n - Writing complete network...");
					_network.writeNetworkToFile("network_" + i);
					System.out.println("done\n Let's continue crawling!");

					_lastWrite = i;
					_holding = 0;
					_timeToWrite = 0;
					_timeToWait = false;
					
				}
			}
			
		}
	}
}
