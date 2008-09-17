public class WeTube {
		
	public static void main( String args[] ) throws Exception
	{
		System.out.println("WeTube - a YouTube Video's Crawler");
		
		// initialize the visited, todo and network objects
		Visited visited = new Visited();
		Todo todo = new Todo();
		Network network = new Network();
		
		YTCrawler c = new YTCrawler(visited, todo, network);
		System.out.println("crawl: " + c.crawlPage("q_D_Aeqcidc"));
		c.printData();
	}

}
