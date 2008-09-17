
public class Todo {
  //private List _list; // some kind of representation of the list
	
  /**
   * see if a string is already in the list!
   */
  private boolean inList (String s)
  {
	  return false;
  }
  
  /**
   * add a unique ID to the list.
   * if it fails (the list is full) -1 is returned
   * we assume the unique ID is not included in the Visited ID's 
   */
  public int add(String s)
  {
	  if (!inList(s))
	  {
		  // add the item to the list
		  return 0;
	  } else return -1;
  }


}
