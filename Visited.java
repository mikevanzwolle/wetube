import java.util.Hashtable;
import java.util.Vector;

public class Visited {
   Hashtable<String, String> _hash;
   Vector<String> _list;
	

   public Visited()
	{
		_hash = new Hashtable<String, String>();
		_list = new Vector<String>();
	} // constructor
	
	
   public synchronized boolean inList(String s)
   {
	   return _hash.contains(s);
   }
   
	/**
	 *  Write the visited list representation to a file 
	 */
	public synchronized int writeList()
	{
		return -1;
	}
	
	/**
	 * Read the visited list from a file
	 * @return
	 */
	public synchronized int readList()
	{
		return -1;
	}
	
    /** 
     * Add a unique ID to the list.
     * if it fails (for example when list is full) -1 is returned 
     */
	public synchronized int add(String s)
   {
		if (!inList(s)) {
			_hash.put(s, s);
			_list.add(s);
			return 0; // everything went fine, String s has been put into the hashtable
		}
			
		return -1; // an error occurred, the String s is already present in the hashtable
   }
}
