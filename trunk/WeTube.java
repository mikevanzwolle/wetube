import java.io.*;


public class WeTube {
	
	private static int _maxThreads = 30; 
//	private static String _POLICY = "Auto's & voertuigen"; // extended crawler type of policy (text based)
	private static String _POLICY = "28"; // related crawler type of policy (28 == wetenschap en techniek)
	private static int _RETRYCOUNT = 3;
	
	public static void main( String args[] ) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String line = "WeTube - a YouTube Video Crawler - Category: " +  _POLICY + " - Super Fast Version, Crawl Category Only!!"; 
		System.out.println(line);
		
		// initialize the visited, todo and network objects
		PageidList visited = new PageidList(line,"visited.txt");
		PageidList todo = new PageidList(line,"todo.txt");
		
//		todo.add("fPJzhTi47LE");
		todo.readFromFile("intodo.txt");
		visited.readFromFile("invisited.txt");
		Network network = new Network(line, todo.getSize() > 100);
		
		System.out.println("Press [enter] to continue...");
		br.readLine();
		YTCrawler_Related crawlers[] = new YTCrawler_Related[_maxThreads];
		Manager manager = new Manager(visited, todo, _maxThreads);

		for (int i = 0; i < _maxThreads; i++)
		{
			crawlers[i] = new YTCrawler_Related(visited, todo, network, i, _POLICY, _RETRYCOUNT, manager);
		    crawlers[i].start();
		}

		// every once in a while write the todo and visited lists to files (it's kinda difficult, because all threads must first be paused...)
		// the manager takes care of this!
		manager.start();  
	}

}

