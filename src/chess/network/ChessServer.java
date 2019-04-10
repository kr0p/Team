package chess.network;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import chess.engine.board.Board;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.gui.ChessTable.MoveLog;
import chess.network.ServerGUI;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ChessServer extends AbstractServer {
	private JTextArea log;
	private JLabel status;
	private Board board;
	private ArrayList<ConnectionToClient> clients = new ArrayList<>();
	private Integer playersNum = 0;
	private boolean allConnected = false;
	
	public ChessServer(int port, int timeout) {
		super(port);

		//Set the timeout value to the passed timeout value.
	    this.setTimeout(timeout);
	    
	    //Set the log and status objects to the current objects in the ServerGUI class.
	    setLog(ServerGUI.getLog());
	    setStatus(ServerGUI.getStatus());
		System.out.println("test for committing");

	}

	@Override
	protected void handleMessageFromClient(Object arg0, ConnectionToClient arg1) {
		if(arg0 instanceof Board) {
			sendToAllClients((Board) arg0);
		}
		if(arg0 instanceof Tile) {
			sendToAllClients((Tile) arg0);
		}
		if(arg0 instanceof Piece) {
			sendToAllClients((Piece) arg0);
		}
		if(arg0 instanceof MoveLog) {
			sendToAllClients((MoveLog) arg0);
		}
	}
	
	@Override
	public void serverStarted() {
		//Set the status and log to the current state of the ServerGUI objects.
		setStatus(this.status);
		setLog(this.log);
		
		//Update the status label to notify the user that the server is listening.
	    status.setText("Listening");
	    status.setForeground(Color.GREEN);
	    
	    
	    //Append a user notification to the Server Log.
	    log.append("Server Started\n");
	    this.board = Board.createInitialBoard();
	}
	
	//If the server has been stopped update the log and status 
	public void serverStopped()
	{
		log.append("Server Stopped Accepting New Clients - "
				+ "Press Listen to Start Accepting New Clients\n");
		status.setText("Stopped");
		status.setForeground(Color.RED);
	}

	//If the server has been closed update the log and status
	protected void serverClosed()
	{
		log.append("Server and All Current Clients are Closed - Press Listen to Restart\n");
		status.setText("Close");
		status.setForeground(Color.RED);
	}
	
	//If there is a listening exception error print the error and update the log and status
	protected void listeningException(Throwable exception) 
	{
		log.append("Listening Exception: " + exception);
		exception.printStackTrace();

		log.append("Press Listen to Restart Server\n");
		status.setText("Exception Occurred when Listening");
		status.setForeground(Color.RED);
	}
	
	@Override
	public void clientConnected(ConnectionToClient client) {
		 
		clients.add(client);
		//Set the log to the current state of the ServerGUI log object.
		setLog(this.log);
		  
		//Append the connected client's ID to the server log.
		log.append("Client" + client.getId() + " Connected\n");
		if(allConnected == false)
			playersNum++;
		System.out.println(playersNum);
		
		try {
			client.sendToClient(client.getId());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(playersNum == 2 && allConnected == false) {
			allConnected = true;
			for(ConnectionToClient c : this.clients) {
				try {
					c.sendToClient(board);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		 
	}
	
	//Sets the JTextArea object to the current state of the ServerGUI JTextArea object.
	public void setLog(JTextArea log) {
		this.log = ServerGUI.getLog();
	}
	  
	//Sets the JLabel object to the current state of the ServerGUI JLabel object.
	public void setStatus(JLabel status) {
		this.status = ServerGUI.getStatus();
	}
}
