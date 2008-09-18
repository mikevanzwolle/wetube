import java.util.Hashtable;
import java.util.Vector;

public class Todo {
   Hashtable<String, String> _hash;
   Vector<String> _list;
	
   public Todo()
   {
		_hash = new Hashtable<String, String>();
		_list = new Vector<String>();
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
		   _list.add(s);
		   return 0;
	   }
	   notify();
	   return -1;
   }

   /**
    * Pop the first item of the todo list off the list!
    * @return
    */
   public synchronized String pop() throws InterruptedException
   {
       while (_list.size() == 0) {
           wait();
       }	   
	   
	   String s = ""; 
	   s = _list.remove(0);
	   _hash.remove(s);

	   notify();
	   return s;
   }
  
   
   /** 
    * Get the size of the todo list
    * @return
    */
   public synchronized int getSize() { return _list.size();  }
}
