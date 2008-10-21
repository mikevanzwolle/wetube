import java.io.*;
import java.util.*;

public class PageidList {
   TreeSet<String> _set;
   LinkedList<String> _queue;
   
   String _filename;
   
   public PageidList(String line, String filename)
   {
		_set = new TreeSet<String>();
		_queue = new LinkedList<String>();
		_filename = filename;
   }
   
   public void readFromFile(String f)
   {
	   try
	   {
		   int i = 1;
		   BufferedReader bufRead = new BufferedReader(new FileReader(f));
		   String line = bufRead.readLine();
		   while (line != null){
			   if (line.charAt(0) != '#')
				   fastAdd(line);
			   if (i % 1000 == 0)
			      System.out.print(".");
			   i++;
			   line = bufRead.readLine();
		   }
		   
	   }catch (Exception ex)
	   {
		   System.out.println("\n" + ex);
		   System.exit(0);
	   }
   }
   
   public synchronized void writeListToFile(String s)
   {
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(s + ".txt"));
			Iterator<String> iter = _queue.iterator();
			while (iter.hasNext()) 
				out.write( iter.next() + "\n");
			
			out.flush();
			out.close();
			
		} catch (Exception ex)
		{
			System.out.println("Error writing to " + _filename);
		}
   }
   
   /**
    * Determine if the item is present in the list
    * @param s
    * @return
    */
   public synchronized boolean inList(String s)
   {
	   return _set.contains(s);
   }   
   
  /**
   * add a unique ID to the list.
   * if it fails -1 is returned
   * we assume the unique ID is not included in the Visited ID's 
   */
   public synchronized int add(String s)
   {
	   if (_set.add(s))
	   {
		   _queue.add(s);
		   notify();
		   return 0;
	   }
	   notify();
	   return -1;
   }

   /**
    * add a unique ID to the list.
    * if it fails -1 is returned
    * we assume the unique ID is not included in the Visited ID's 
    */
    public synchronized int fastAdd(String s)
    {
    	_set.add(s);
    	_queue.add(s);
	   return 0;
    }
   
   /**
    * Pop the first item of the todo list off the list!
    * @return
    */
   public synchronized String pop() throws InterruptedException
   {
       while (_set.size() == 0)  
           wait();
       
	   String s = _queue.pop();
	   _set.remove(s);
       
	   notify();
	   return s;
   }
  
   
   /** 
    * Get the size of the todo list
    * @return
    */
   public synchronized int getSize() { return _set.size();  }

}
