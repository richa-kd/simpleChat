package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import ocsf.server.ConnectionToClient;
import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client.getInfo("loginID"));
    String message = (String)msg;
    String[] arg = message.split(" ");
    String checkID = arg[0];
	if (checkID.equals("#login")){
		String loginID = arg[1];
		client.setInfo("loginID",loginID);
		System.out.println(client.getInfo("loginID")+" has logged on.");
	}
	else {
		this.sendToAllClients(msg);
	}
    
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * implements hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A new client has connected");
  }
  
  /**
   * implements hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client){
	  System.out.println(client.getInfo("loginID")+" client has disconnected");
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServer(String message)
  {
     this.sendToAllClients("SERVER MSG> : " + message);
  }
  
  /*
   * This method handles messages that are commands.
   * 
   * @param command The message from UI that starts with #
   */
  public boolean handleHashCommand(String command) {
	  String[] message = command.split(" ");
	  if (command.equals("#quit")) {
		System.exit(0);
		return true;
	  }
	  else if (command.equals("#stop")) {
		  stopListening();
		  return true;
	  }
	  else if (command.equals("#close")) {
		  try {
				close();
			} catch (IOException e) {
				System.exit(0);
			}
		  return true;
	  }
	  else if (command.startsWith("#setPort")) {
		  setPort(Integer.parseInt(message[1]));
		  return true;
	  }
	  else if (command.equals("#start")) {
		  if (!isListening()){
			  try {
				listen();
			} catch (IOException e) {
				//error
			}
		  }
		 else{
				System.out.println("Error: Server is already on and listening.");  
			  }
		  return true;
	  }
	  else if (command.equals("#getport")) {
		  getPort();
		  return true;
	  }
	  else if(command.startsWith("#login")) {
		  System.out.println("Error: You are already connected. Terminating connection.");
		  ConnectionToClient client = null;
		  try {
			client.close();
		} catch (IOException e) {
			// error
		}
		return true;
	  }
	  else {
		  return false;
	  }
  }
  
  
}