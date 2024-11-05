package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.*;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ServerConsole adminUI ;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ServerConsole admin) 
  {
    super(port);
    adminUI=admin ;
    try 
    {
      listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      adminUI.display("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient (Object msg, ConnectionToClient client) {
	  
	  if (client.getInfo("#connections")==null) {
		  client.setInfo("#connections", 0) ;
	  }
	  
	  String str = (String) msg ;
	 
	  if (str.regionMatches(0,"#login<",0,7)) {
		  
		  if ((int) client.getInfo("#connections")<1) {
			  String logID = str.substring(7,str.length()-1) ;
			  client.setInfo("loginID", logID);
			  client.setInfo("#connections", (int) client.getInfo("#connections") + 1) ;
		  }
		  
		  else {
			  try {
				client.close();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		  }

		  }
		  else {
			  String formatMsg= client.getInfo("loginID")+" : "+msg ;
			  adminUI.display(formatMsg);
			  this.sendToAllClients(formatMsg);
		  }
	  }
  
  public void handleMessageFromServerUI(String message ) throws IOException {
	  
	  String chain= "SERVER MSG> "+message;
	  
	  try {
		  if(message.startsWith("#")) {
			  handleCommand(message) ;
		  }
		  else {
			  adminUI.display(chain);
			  this.sendToAllClients(chain);
		  }
	  }
	  
	  catch (Exception exception) { 
		  adminUI.display("Could not send message to clients. Closing server.");
		  this.close() ;
	  }
  }
  
  private void handleCommand(String cmd) throws IOException {
	  
	  if (cmd.equals("#quit")) {
		  adminUI.display("The server is going to close.");
		  this.close();
	  }
	  
	  else if (cmd.equals("#stop")) {
		  if(this.isListening()) {
			  this.stopListening();
		  }
		  else {
			  adminUI.display("The server is already not listening for new connections");
		  }
	  }
	  
	  else if (cmd.equals("#close")) {
		  
		  if(this.isListening()) {
			  this.stopListening();
		  }
		  else {
			  adminUI.display("The server is already not listening for new connections");
		  }
		  this.close() ;
	  }
	  
	  else if (cmd.regionMatches(0,"#setport",0,8)) {
		  if (! this.isListening()) {
			  String port = cmd.substring(9,cmd.length()-1) ;
			  this.setPort(Integer.parseInt(port));
		  }
		  else {
			  adminUI.display("Server has to be closed to modify the connection port.");
		  }
	  }
	  
	  else if (cmd.equals("#getport")) {
		  String port = String.valueOf(this.getPort()) ;
		  adminUI.display(port) ;
	  }
	  
	  else if (cmd.equals("#start")) {
		  if (!this.isListening()) {
			  this.listen();
		  }
		  else {
			  adminUI.display("The server is already listening for new connections.");
		  }
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    adminUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    adminUI.display("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  /**
   * Implementation of the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  adminUI.display("New client connected to the server !") ;
	  this.sendToAllClients("New client connected to the server !") ;
  }

  /**
   * Implementation of the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  adminUI.display("A client has been disconnected  !") ;
	  this.sendToAllClients("A client has been disconnected !");
  }

  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  
   @Override
   synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	   this.clientDisconnected(client) ;
   }
}
//End of EchoServer class
