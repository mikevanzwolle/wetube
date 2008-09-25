import java.io.*;



public class WeTube {
	
	private static int _maxThreads = 30; 
	private static String _policy = "Wetenschap en technologie";
	
	public static void main( String args[] ) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("WeTube - a YouTube Video's Crawler");
		
		// initialize the visited, todo and network objects
		Visited visited = new Visited();
		Todo todo = new Todo();
		Network network = new Network();
		
		todo.add("ewp-dyr4OV4");

		System.out.println("Press [enter] to continue...");
		br.readLine();
		YTCrawler crawlers[] = new YTCrawler[_maxThreads];
		for (int i = 0; i < _maxThreads; i++)
		{
			crawlers[i] = new YTCrawler(visited, todo, network, i, _policy);
		    crawlers[i].start();
		}
	}

}
