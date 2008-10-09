import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class PageidList {
   Hashtable<String, String> _hash;
   String _filename;
   
   public PageidList(String line, String filename)
   {
		_hash = new Hashtable<String, String>();
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
			Enumeration<String> en = _hash.elements();
			while (en.hasMoreElements()) 
				out.write( en.nextElement() + "\n");
			
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
	   return _hash.contains(s);
   }   
   
  /**
   * add a unique ID to the list.
   * if it fails -1 is returned
   * we assume the unique ID is not included in the Visited ID's 
   */
   public synchronized int add(String s)
   {
	   if (!inList(s))
	   {
		   // add the item to the list
		   _hash.put(s,s);
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
	   _hash.put(s,s);
	   return 0;
    }
   
   /**
    * Pop the first item of the todo list off the list!
    * @return
    */
   public synchronized String pop() throws InterruptedException
   {
       while (_hash.size() == 0)  
           wait();
	   
	   String s = _hash.elements().nextElement();
	   _hash.remove(s);

	   notify();
	   return s;
   }
  
   
   /** 
    * Get the size of the todo list
    * @return
    */
   public synchronized int getSize() { return _hash.size();  }

}
