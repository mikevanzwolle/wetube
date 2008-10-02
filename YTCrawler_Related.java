import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class YTCrawler_Related extends Thread {
	static final Object NO_MORE_WORK = new Object();
	static final int _RELATED_ERROR = -3;
	static final int _POLICY_MISMATCH = -2;
	static final int _GENERAL_ERROR = -1;
	
	private int _crawler_id;
	private int _parse_error;
	
	private PageidList _visited;
	private PageidList _todo;
	private Network _network;
	private String _pageid;
	
	private String _policy;
	
	private StringBuilder _contents;

	Vector<String> _related; // 2000
	
	private int _tempsearch;
	private int _retry;
	
	private Manager _manager;

	public YTCrawler_Related(PageidList v, PageidList t, Network n, int cid, String policy, int retry, Manager m)
	{
		 _manager = m;
		 _visited = v;
		 _todo = t;
		 _network = n;
		 
		 _contents = new StringBuilder();
		 _related = new Vector<String>();
		 
		 _crawler_id = cid;
		 _policy = policy;
		 
		 _retry = retry;
	}
	
	public void run()
	{
		boolean wait = false;
        try {
            while (true) {

            	if (_manager.getTimeToWait())
            	{
            		if (!wait)
            		{
            			_manager.addHolding();
            			System.out.println("Crawler is holding!");
            			wait = true;
            		}
            	} else
            	{
            		wait = false;
            	}
            		
            	if (!wait)
            	{
            		int errorCounter = 0;
            		// Retrieve some work; block if the queue is empty
            		String work = _todo.pop();
            		// 	Terminate if the end-of-stream marker was retrieved
            		if (work == NO_MORE_WORK) 
            			break;
               
            		System.out.println(_crawler_id + " retrieved from todo " + work + " (available: " + _todo.getSize() + " done: " + _visited.getSize() + ")");
            		_pageid = work;
            		
            		int r = crawlPage();
            		while ( (r < 0) 
            				&& (errorCounter < _retry-1)) 
            		{
            			System.out.println("RETRY");
            			errorCounter++;
            			crawlPage(); // try again!!!
            		}
            	}
            }
        } catch (Exception e) {
			System.out.println(" inturupted "+e);
        }
		
		
	}
	
	public int crawlPage(String p)
	{
		_pageid = p;
		return crawlPage();
	}
	
	/**
	 * Find the text between two search strings. The first search string has to be the identifying one (unique)!
	 * @param s1
	 * @param s2
	 * @return
	 */
	private String getTextBetween(String s1, String s2)
	{
		int i, j;
		i = _contents.indexOf(s1,_tempsearch); 
		
		if (i >= 0) {
			j = _contents.indexOf(s2, i + s1.length());
			if (j >= 0)
			{
				_tempsearch = j;
			   return _contents.substring(i+s1.length(),j);
			}
		}

		_parse_error = -1; // if we get to this point, something must have gone wrong!
		return "";
	}
	
	/**
	 * Handle all the post-crawl stuff 
	 * - Determine if the crawled page needs to be added to the network
	 * (perhaps we only want to crawl a specific part of the network, filters can be added here!)
	 * - Add related movies to todo list
	 * - Add this page to visited list (even if we don't add it to the network, we can add it to the 
	 *   visited list to make sure we don't crawl it again! 
	 * @return
	 */
	private int pageToNetwork()
	{
		String s;
		for (int k =0; k < _related.size(); k++){
			s = _related.elementAt(k); 
			if (!_visited.inList(s) && !_todo.inList(s)) 
			{
//				System.out.println("    " + _crawler_id + " adding to todo " + s);
				_todo.add(s);// if we haven't visited it before AND it's not already in the todo list THEN add it to the todo list
			}
		}
			
		_network.writeDataSimple(_pageid, _related);
		return 0;
	}
	
	/**
	 * Parse the page
	 * the order in which we find the different keywords is important!!
	 */
	private int parsePage(){
		_parse_error = 0;
		_tempsearch = 0;
		_related.clear();

		String s = getTextBetween("translated_short_prefix_", "\" class=");
		while ( !s.equals("") )
		{
			String cat = getTextBetween("<a href=\"/results?search_category=","\"");
			
			if (cat.equals(_policy))
				_related.add(s);
			
			s = getTextBetween("translated_short_prefix_", "\" class=");
		}
		
		if (_related.size() <= 0) 
			_parse_error = _GENERAL_ERROR;
		else
			_parse_error = 0;

		return _parse_error;
	}
	
	private int crawlPage()
	{
		try
		{
			System.out.println("  " + _crawler_id + " crawling " + _pageid + " added to visited");
			_visited.add(_pageid); // zorg ervoor dat wat er ook gebeurd de pagina niet meer bekeken gaat worden.. 
			_contents.delete(0, _contents.length());


			URL url = new URL("http://www.youtube.com/results?search=related&v=" + _pageid + "&page=1");
			URLConnection uc = url.openConnection();
			uc.setReadTimeout(10000);
		
			BufferedReader in = new BufferedReader(   new InputStreamReader(  uc.getInputStream()  )   );

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				_contents.append(inputLine + "\n"); // regel voor regel de pagina inlezen
				
			
			in.close();
		
			int r = parsePage();
			if (r == 0) 
				return pageToNetwork();
			else 
				return r;
		} catch (Exception ex)
		{
			System.out.println("WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA WAT IS DAT" + ex);
			return -1;
		}
	}

}
