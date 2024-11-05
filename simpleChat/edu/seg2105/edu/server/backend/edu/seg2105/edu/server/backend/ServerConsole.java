package edu.seg2105.edu.server.backend;


import edu.seg2105.client.common.*;
import java.io.*;
import java.util.Scanner;

public class ServerConsole implements ChatIF {
	
	final public static int DEFAULT_PORT = 5555;
	
	EchoServer admin;
	
	Scanner fromConsole;
	
	public void display (String message) {
		System.out.println("> "+message);
		
	}
	
	public ServerConsole(int port) {
		admin = new EchoServer(port, this);
		fromConsole = new Scanner (System.in) ;
	}
	
	public void accept() 
	  {
	    try
	    {
	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        admin.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }

	
	public static void main(String[] args) 
	  {
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole sv = new ServerConsole(port) ;
	    sv.accept(); //Start listening for connections
	  }
}

