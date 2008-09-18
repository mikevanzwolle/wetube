public class WeTube {
	
	private static int _maxThreads = 10; 
	
	public static void main( String args[] ) throws Exception
	{
		System.out.println("WeTube - a YouTube Video's Crawler");
		
		// initialize the visited, todo and network objects
		Visited visited = new Visited();
		Todo todo = new Todo();
		Network network = new Network();
		
		todo.add("q_D_Aeqcidc");

		YTCrawler crawlers[] = new YTCrawler[_maxThreads];
		for (int i = 0; i < _maxThreads; i++)
			crawlers[i] = new YTCrawler(visited, todo, network, i);
		
		for (int i = 0; i < _maxThreads; i++)
			crawlers[i].start();
		
		System.out.println("What is going on man!!!");
	}

}
