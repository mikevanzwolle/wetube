import java.io.*;

public class Network {
	BufferedWriter _out;

	public Network(){
		try
		{
			_out = new BufferedWriter(new FileWriter("network.txt"));
		} catch (Exception ex)
		{
			System.out.println("Error opening file for writing...");
		}
		
	}//constructor
	
	public synchronized void writeData(String s)
	{
		try{
			_out.write(s);
			_out.flush();
		} catch (Exception ex){
			System.out.println("Oops, error writing \"" + s + "\"");
		}
	}
	
	protected void finalize() throws Throwable
	{
		_out.flush();
		_out.close();
	}
	
}
