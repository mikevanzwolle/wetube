import java.io.*;


public class WeTube {
	
	private static int _maxThreads = 25; 
	private static int _RETRYCOUNT = 3;
	
	public static void main( String args[] ) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String line = "--- WeTube - a YouTube Video Crawler - Super Fast Version, Crawl Category Only!! --- \n"; 
		System.out.println(line);
		
		// initialize the visited, todo and network objects
		PageidList visited = new PageidList(line,"visited.txt");
		PageidList todo = new PageidList(line,"todo.txt");
		Network network = new Network(line);
		
		System.out.println("We will now try to read the following files, make sure they exist!!");
		System.out.println("  in_todo.txt     (must contain at least one YouTube movie id, followed by a newline character)");
		System.out.println("  in_visited.txt  (may be empty)");
		System.out.println("  in_network.txt  (may be empty)");
		System.out.println("\n If you want to proceed press [ENTER] otherwise press [CTRL+C]");
		br.readLine();
		System.out.println("Reading these files may take some time, hang in there!");
		System.out.print(" - reading in_todo.txt");
		todo.readFromFile("in_todo.txt"); 
		System.out.print("\n - reading in_visited.txt");
		visited.readFromFile("in_visited.txt");
		System.out.println("\n - copying in_network.txt to network.txt");
		network.readFromFile("in_network.txt");

		System.out.println("Starting the crawling process...");
		
		YTCrawler_Related crawlers[] = new YTCrawler_Related[_maxThreads];
//		YTCrawler_Extended crawlers[] = new YTCrawler_Extended[_maxThreads];
		Manager manager = new Manager(visited, todo, network, _maxThreads);

		for (int i = 0; i < _maxThreads; i++)
		{
			crawlers[i] = new YTCrawler_Related(visited, todo, network, i, "28", _RETRYCOUNT, manager);
//			crawlers[i] = new YTCrawler_Extended(visited, todo, network, i, "Wetenschap en technologie", _RETRYCOUNT, manager);
		    crawlers[i].setPriority(2);
		    crawlers[i].start();
		}

		// every once in a while write the todo and visited lists to files (it's kinda difficult, because all threads must first be paused...)
		// the manager should take care of this!
	    manager.setPriority(2);
		manager.start();  
	}

}

