package chess.network;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import chess.network.ChessServer;;

public class ServerGUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JPanel panel2;
	private JPanel panel3;
	private JLabel sLabel;
	private static JLabel status;
	private JLabel Port;
	private JLabel Timeout;
	private JLabel serverLog;
	private static JTextField portNum;
	private JTextField timeoutNum;
	private JButton listen;
	private JButton close;
	private JButton stop;
	private JButton quit;
	private static JTextArea log;
	private JScrollPane serverPane;
	private static int port;
	private static int timeout;
	private ChessServer server;
	private static boolean flag;
	
	public static void main(String[] args) {
		new ServerGUI();
	}
	
	public ServerGUI() {
		this.setTitle("Chess Server");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		flag = false;
		
		//Set initial port and timeout values to -1.
		port = -1;
		timeout = -1;
		server = new ChessServer(port, timeout);
		
		//Create new JPanels for the top, center, and bottom of the window, setting to GridBagLayout.
		panel = new JPanel(new GridBagLayout());
	    panel2 = new JPanel(new GridBagLayout());
	    panel3 = new JPanel(new GridBagLayout());
		
	    //Create new status labels and set their text.
		sLabel = new JLabel("Status: ");
		status = new JLabel("Not Connected");
		status.setForeground(Color.RED);
		serverLog = new JLabel("Server Log");
		
		//Create the other labels and set their text to the labels array elements.
		Port = new JLabel("Port: ");
		Timeout = new JLabel("Timeout: ");
		
		portNum = new JTextField(10);
		timeoutNum = new JTextField(10);
		
		//Create a new JTextArea and set its dimensions.
		log = new JTextArea();
		log.setColumns(40);
		log.setRows(10);
				
		//Mount the JTextArea in a new JScrollPane.
		serverPane = new JScrollPane(log);
		
		listen = new JButton("Listen");
		listen.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//If either the Port or Timeout textfields are empty, display an error message in Server Log.
						if(portNum.getText().isEmpty() || timeoutNum.getText().isEmpty())
							log.append("Port Number/timeout not entered before pressing Listen.\n");
						else {	
							//Else, set the values of port and timeout to the values contained in the textfields.
							flag = true; 
							port = Integer.parseInt(portNum.getText());
							timeout = Integer.parseInt(timeoutNum.getText());
							
							//Only set the port/timeout values if greater than zero. Else, display an error message.
							if(port > 0 && timeout >= 0) {
								//Set the port and timeout server values to the stored variable values.
								server.setPort(port);
								server.setTimeout(timeout);
								
								//Activate the server object's listen method.
								try {
									server.listen();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							else
								log.append("Port and Timeout values cannot be negative, and Port must be greater than zero.\n");
						}
					}
				});
		
		close = new JButton("Close");
		close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//Checks to see if listen has been started 
						if(flag == false)
						{
							log.append("Server Not Currently Started\n");
						}
						else if(flag == true) {
						try {
							//Closes server connection if already listening 
							server.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					}
				});

		stop = new JButton("Stop");
		stop.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//Check to see if listen has been started
						if(flag == false)
						{
							log.append("Server Not Currently Started\n");
						}
						else 
						{
							//calls stopListening to set log and status
							server.stopListening();
						}
					}
				});
		
		
		quit = new JButton("Quit");
		//Clicking quit will close the window.
				quit.addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								//Close the server, and exit the application.
								try {
									server.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								System.exit(0);
							}
						});
		
		/*
	     * This is an example of a reasonably efficient way to use GridBagLayout. Also see
	     * the addItem method below.
	     */
	    addItem(panel, sLabel, 0, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE); 
	    addItem(panel, status, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    addItem(panel, Port, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
	    addItem(panel, portNum, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    addItem(panel, Timeout, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
	    addItem(panel, timeoutNum, 1, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    
	    addItem(panel2, serverLog, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE); 
	    addItem(panel2, serverPane, 0, 3, 1, 5, GridBagConstraints.CENTER, GridBagConstraints.NONE);
	    
	    addItem(panel3, listen, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    addItem(panel3, close, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    addItem(panel3, stop, 2, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    addItem(panel3, quit, 3, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    
	    //Add the first, second, and third panels to the NORTH, CENTER, and SOUTH of the JFrame, respectively.
	    this.add(panel, BorderLayout.NORTH);
	    this.add(panel2,  BorderLayout.CENTER);
	    this.add(panel3, BorderLayout.SOUTH);
		
	    //Set the window size and set visibility to true.
	    this.setSize(500, 500);
	    this.setVisible(true);
	}
	
	private void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align, int fill) {
	    GridBagConstraints gc = new GridBagConstraints();
	    gc.gridx = x;
	    gc.gridy = y;
	    gc.gridwidth = width;
	    gc.gridheight = height;
	    gc.weightx = 100.0;
	    gc.weighty = 100.0;
	    gc.insets = new Insets(10, 10, 10, 10);
	    gc.anchor = align;
	    gc.fill = fill;
	    p.add(c, gc);
	  }
	
	public static JTextArea getLog() {
		return log;
	}
	
	public static JLabel getStatus() {
		return status;
	}
	
	//Getter for the current port value.
	public static int getPort() {
		return port;
	}
		
	//Getter for the current timeout value.
	public static int getTimeout() {
		return timeout;
	}
}
