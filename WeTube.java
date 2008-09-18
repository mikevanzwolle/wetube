import java.io.*;



public class WeTube {
	
	private static int _maxThreads = 10; 
	
	public static void main( String args[] ) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("WeTube - a YouTube Video's Crawler");
		
		// initialize the visited, todo and network objects
		Visited visited = new Visited();
		Todo todo = new Todo();
		Network network = new Network();
		
		todo.add("q_D_Aeqcidc");

		System.out.println("Press [enter] to continue...");
		br.readLine();
		YTCrawler crawlers[] = new YTCrawler[_maxThreads];
		for (int i = 0; i < _maxThreads; i++)
		{
			crawlers[i] = new YTCrawler(visited, todo, network, i);
		    crawlers[i].start();
		}
		
		YTCrawler t = new YTCrawler(visited, todo, network, 1);
			t.crawlPage("q_D_Aeqcidc");
		
	}

}
