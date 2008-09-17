import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class YTCrawler {
	private int _parse_error;
	
	private Visited _visited;
	private Todo _todo;
	private Network _n;
	private String _pageid;
	
	private String _contents;

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

	public YTCrawler(Visited v, Todo t, Network n)
	{
		 _visited = v;
		 _todo = t;
		 _n = n;
		 
		 _contents = "";
		 _related = new Vector();
		 
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
	 * Parse the page
	 * the order in which we find the different keywords is important!!
	 */
	private int parsePage(){
		_parse_error = 0;
		
		_tempsearch = 0;
		_related.clear();

		_description = getTextBetween("<meta name=\"description\" content=\"","\">");
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
		_category = getTextBetween("VideoWatch/VideoCategoryLink');\">","</a>");
		
		String s = getNextRelated();
		while ( !s.equals("") )
		{
			_related.add(s);
			s = getNextRelated();
		}
		
		return _parse_error;

		// now we only have to parse Related Links!!!
		
	}
	
	public int crawlPage(String pageid)
	{
		_pageid = pageid; // store the page id internally for future use
		StringBuilder b = new StringBuilder();

		try
		{
			URL url = new URL("http://www.youtube.com/watch?v=" + _pageid);
			URLConnection uc = url.openConnection();
			uc.setReadTimeout(10000);
		
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							uc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
			{
				// regel voor regel de gegevens intern opslaan
				b.append(inputLine + "\n");
			}
				
			in.close();
		 
			_contents = b.toString();
			
			return parsePage();
		} catch (Exception ex)
		{
			return -1;
		}
	}
	
	public void print()
	{
		System.out.print(_contents);
	}
}
