import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class YTCrawler_Extended extends Thread {
	static final Object NO_MORE_WORK = new Object();
	static final int _RELATED_ERROR = -3;
	static final int _POLICY_MISMATCH = -2;
	static final int _GENERAL_ERROR = -1;
	
	private int _crawler_id;
	private int _parse_error;
	
	private PageidList _visited;
	private PageidList _todo;
	private Network _network;
	private Manager _manager;
	private String _pageid;
	private String _policy;
	
	private StringBuilder _contents;

	private String _description; //21
	private String _labels; //22
	private String _movielength; // .. can be found on line 91!!
	private String _title; //369 (and 20...)
	private String _rating; //410
	private String _numratings;//410
	private String _views; //418
	private String _videomessages;//663
	private String _textmessages; //664
	private String _user; //1399
	private String _date; //1402
	private String _category; //1454
//	private String _code; //1494 // we already have this one! it is called _pageid, and we already know it when we start crawling the page! 
	Vector<String> _related; // 2000
	
	private int _tempsearch;
	private int _retry;

	public YTCrawler_Extended(PageidList v, PageidList t, Network n, int cid, String policy, int retry, Manager m)
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
            				&& (r != _POLICY_MISMATCH) 
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
	
	public void printData() {
		System.out.println("Title: " + _title);
		System.out.println("Rating: " + _rating);
		System.out.println("Num ratings: " + _numratings);
		System.out.println("Views: " + _views);
		System.out.println("Video responses: " + _videomessages);
		System.out.println("Text messages: " + _textmessages);
		System.out.println("User: " + _user);
		System.out.println("Date: " + _date);
		System.out.println("Category: " + _category);
		System.out.println("Description: " + _description);
		System.out.println("Labels: " + _labels);
		System.out.println("Length (s): " + _movielength);
		for (int k =0; k < _related.size(); k++)
   		   System.out.println("Related: " + _related.elementAt(k));
		
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
//		System.out.println("JEMMMMMMMMMMMMMMMMMIG: " + s1 + "|" + s2 + " i:" + i);
		return "";
	}
	
	private String getNextRelated()
	{
		int i, j;
		String s1 = "href=\"/watch?v=";
		String s2 = "&amp;feature=related\">";
		j = _contents.indexOf(s2,_tempsearch); 
		
		if (j >= 0) {
			_tempsearch = j+1;
			
			i = _contents.indexOf(s1, j - 35);
			if ((i >= 0) && (i < j))
			{
			   return _contents.substring(i+s1.length(),j);
			}
		}
		
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
				_todo.add(s);// if we haven't visited it before AND it's not already in the todo list THEN add it to the todo list
		}
			
		_network.writeData(_pageid, _title, _user, _date, _description, _labels, _category, _movielength, _rating, _numratings, _views, _textmessages, _videomessages, _related);
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

//		_description = getTextBetween("<meta name=\"description\" content=\"","\">");
		_labels = getTextBetween("<meta name=\"keywords\" content=\"","\">");
		_movielength = getTextBetween("\", \"l\":",","); 
		_title = getTextBetween("<h1 >","</h1>"); // perhaps we can also move this up, because at the top of the page we can find: ... <meta name="title" content="
		_rating = getTextBetween("<img class=\"ratingL ratingL-","\"");
		_numratings = getTextBetween("<div id=\"defaultRatingMessage\"><span class=\"smallText\">", " ");
		_views = getTextBetween("<span id=\"watch-view-count\">","</span>");
		_videomessages = getTextBetween("<span id=\"watch-comments-numresponses\">","</span>");
		_textmessages = getTextBetween("<span class=\"number-of-comments\">","</span>");
		_user = getTextBetween("hLink fn n contributor\">","</a>");
		_date = getTextBetween("watch-video-added post-date\">","</span>");
		_description = getTextBetween("watch-video-desc description\">","</span>");
		_description = _description.substring(_description.indexOf(">") + 1);
		 
		_category = getTextBetween("VideoWatch/VideoCategoryLink');\">","</a>");
		
		
		String s = getNextRelated();
		while ( !s.equals("") )
		{
			_related.add(s);
			s = getNextRelated();
		}
		
		
		if (_category.indexOf(_policy) < 0)
			_parse_error = _POLICY_MISMATCH; // wrong policy
		else if (_related.size() < 20)  
			_parse_error = _RELATED_ERROR; // if the related links were still loading...
		
		if (_parse_error < 0)
		{
//			System.out.println("Category:  " + _category + "  Policy : " + _policy + "   Error: " + _parse_error + "   Related Size: " + _related.size());
//			printToFile(_pageid + ".html");
		}
		
		return _parse_error;
		// now we only have to parse Related Links!!!
	}
	
	private int crawlPage()
	{
		try
		{
			_visited.add(_pageid); // zorg ervoor dat wat er ook gebeurd de pagina niet meer bekeken gaat worden.. 
			_contents.delete(0, _contents.length());

			System.out.println(_crawler_id + " crawling " + _pageid );

			URL url = new URL("http://www.youtube.com/watch?v=" + _pageid);
			URLConnection uc = url.openConnection();
			uc.setReadTimeout(10000);
		
			BufferedReader in = new BufferedReader(   new InputStreamReader(  uc.getInputStream()  )   );

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				_contents.append(inputLine + "\n"); // regel voor regel de pagina inlezen
				
			
			in.close();
		

			System.out.println(_crawler_id + " finished downloading " + _pageid );
			
			int r = parsePage();
			if (r == 0) 
				return pageToNetwork();
			else 
				return r;
		} catch (Exception ex)
		{
			System.out.println("WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA WAT IS DAT");
			return -1;
		}
	}
	
	public void print()
	{
		System.out.print(_contents);
	}
	
	public void printToFile(String s)
	{
	      try{
	    	    // Create file 
	    	    FileWriter fstream = new FileWriter(s);
	    	        BufferedWriter out = new BufferedWriter(fstream);
	    	       
	    	    out.write(_contents.toString());
	    	    //Close the output stream
	    	    out.close();
	    	    }catch (Exception e){//Catch exception if any
	    	      System.err.println("Error: " + e.getMessage());
	    	    }
	}	
}
