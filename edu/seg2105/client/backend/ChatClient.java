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
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  // this variable is to store the loginID of the client
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
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
    		handleHashCommand(message);
    	}
    	else{
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  /*
   * This method handles messages that are commands.
   * 
   * @param command The message from UI that starts with #
   */
  
  private void handleHashCommand(String command) {
	  String[] message = command.split(" ");
	  if (command.equals("#quit")) {
		  quit();
	  }
	  else if (command.equals("#logoff")){
		  if (isConnected()) {
				try {
					closeConnection();
				} catch (IOException e) {
					// error
				}
		}  
		  else {
			  System.out.println("Error: Client isn't connected.");
		}
	  }
	  else if (command.startsWith("#sethost")) {
		  if (!isConnected()){
			  setHost(message[1]);
			  }
		  else {
			  System.out.println("Error: Client must log off first.");
		  }
	  }
	  else if (command.startsWith("#setport")) {
		  if (!isConnected()){
			  int port = Integer.parseInt(message[1]);
			  try {
			  setPort(port);
			  }
			  catch (NumberFormatException ne) {
			    	setPort(5555);
			    }
			  }
		  else {
			  System.out.println("Error: Client must log off first.");
		  }
	  }
	  else if (command.startsWith("#login")) {
		  if (!isConnected()) {
			  try {
				openConnection();
			} catch (IOException e) {
				// error
			}
		  }
		  else {
			  System.out.println("Error: Client is already connected.");
		  }
	  }
	  else if (command.equals("#gethost")) {
		  getHost();
	  }
	  else if (command.equals("#getport")) {
		  getPort();
	  }
	  else {
		  try {
		  sendToServer(command);
		  }
		  catch(IOException e)
		    {
		      clientUI.display
		        ("Could not send message to server.  Terminating client.");
		      quit();
		    }
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
  
  /**
	 * implements hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
	  clientUI.display("Server has shutdown.");
	  quit();
	}
  
  /**
	 * implements hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  	  clientUI.display("Connection has been closed.");
	}
  	
  	/**
	 * implements hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
  	@Override
	protected void connectionEstablished() {
		try {
			sendToServer("#login "+loginID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Cannot establish connection with client. Terminating client.");
			quit();
		}
	}
}
//End of ChatClient class
