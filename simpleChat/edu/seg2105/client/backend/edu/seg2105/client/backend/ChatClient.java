// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;





/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
	
  ChatIF clientUI; 
  String loginID ;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String ID) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID=ID;
    openConnection();
    this.sendToServer("#login<"+ID+">");
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.startsWith("#")) {
    		handleCommand(message) ;
    	}
    	else {
    		sendToServer(message) ;
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String cmd) throws IOException {
	  
	  if (cmd.equals("#quit")) {
		  clientUI.display("The client is going to quit.");
		  quit();
	  }
	  
	  else if (cmd.equals("#logoff")) {
		  try {
			  if(this.isConnected()) {
				  this.closeConnection();
			  }
			  else {
				  clientUI.display("The client is already disconnected");
			  }
		  }
		  catch (IOException e) {
			  clientUI.display("Error occured in the client's disconnnection process. Try again !");
		  }
	  }
	  
	  else if (cmd.regionMatches(0,"#sethost",0,8)) {
		  if (! this.isConnected()) {
			  String host = cmd.substring(9,cmd.length()-1) ;
			  this.setHost(host);
		  }
		  else {
			  clientUI.display("Client has to be disconnected to modify the host.");
		  }
	  }
	  
	  else if (cmd.regionMatches(0,"#setport",0,8)) {
		  if (! this.isConnected()) {
			  String port = cmd.substring(9,cmd.length()-1) ;
			  this.setPort(Integer.parseInt(port));
		  }
		  else {
			  clientUI.display("Client has to be disconnected to modify his connection port.");
		  }
	  }
	  
	  else if (cmd.equals("#login")) {
		  if (! this.isConnected()) {
			  this.openConnection() ;
		  }
		  else {
			  clientUI.display(this.loginID+" is already logged in.");
		  }
	  }
	  
	  else if (cmd.equals("#gethost")) {
		  clientUI.display(this.getHost());
	  }
	  
	  else if (cmd.equals("#getport")) {
		  String port = String.valueOf(this.getPort()) ;
		  clientUI.display(port) ;
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
   @Override  
   protected void connectionClosed() {
	   clientUI.display("The connection has been closed");
	}

	/**
	 * Implementation of the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  
     @Override  
	protected void connectionException(Exception exception) {
    	 clientUI.display("The server has shut down");
    	 System.exit(0);
	}

	/**
	 * Implementation of the hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
}
//End of ChatClient class