import java.io.*;
import java.util.Vector;

public class Network {
	
	BufferedWriter _out;
	
	public Network(String line, boolean appendmode){
		try
		{
			_out = new BufferedWriter(new FileWriter("network.txt", appendmode));
			_out.write(line + "\n");
			_out.flush();
		} catch (Exception ex)
		{
			System.out.println("Error opening network file for writing...");
		}
		
	}//constructor
	
	public synchronized void writeData(String pageid, String title, String user, String date, String description, String labels, String category, String movielength, String rating, String numratings, String views, String textmessages, String videomessages, Vector<String> related)
	{
		try{
			String s = "";
			for (int i=0; i<related.size(); i ++){
				s = s + "\t" + rNlT(related.elementAt(i));
			}
			_out.write(rNlT(pageid) + "\t" +
					rNlT(title) + "\t" +
					rNlT(user) + "\t" +	
					rNlT(date) + "\t" +
					rNlT(description) + "\t" +
					rNlT(labels) + "\t" +
					rNlT(category) + "\t" +
					rNlT(movielength) + "\t" +
					rNlT(rating) + "\t" +
					rNlT(numratings) + "\t" +
					rNlT(views) + "\t" +
					rNlT(textmessages) + "\t" +
					rNlT(videomessages) + 
					s + "\n");
			
			_out.flush();
		} catch (Exception ex){
			System.out.println("Oops, error writing \"" + pageid + "\" to network");
		}
	}

	public synchronized void writeDataSimple(String pageid, Vector<String> categoryrelated)
	{
		try{
			String s = "";
			for (int i=0; i<categoryrelated.size(); i ++){
				s = s + "\t" + rNlT(categoryrelated.elementAt(i));
			}
			_out.write(rNlT(pageid) + "\t" +
					s + "\n");
			
			_out.flush();
		} catch (Exception ex){
			System.out.println("Oops, error writing \"" + pageid + "\"");
		}
	}
	
	// replace new line characters and tabs by spaces
	private String rNlT (String s) {
		s = s.replaceAll("\\n", " ");
		s = s.replaceAll("\\t", " ");
		return s;
	}
	
	protected void finalize() throws Throwable
	{
		_out.flush();
		_out.close();
	}
	
}
