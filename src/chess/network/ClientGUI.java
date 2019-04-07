package chess.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class ClientGUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JLabel header;
	private JLabel URL;
	private JLabel port;
	private JTextField ip;
	private JTextField portNum;
	private JButton connect;
	private JButton exit;
	private String userIp;
	private int userPort;
	private ChessClient client;
	
	public static void main(String[] args) {
		new ClientGUI();
	}
	
	public ClientGUI() {
		this.setTitle("Chess Client");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		userIp = "-1";
		userPort = -1;
		
		
		SpringLayout l1 = new SpringLayout();
		panel = new JPanel(l1);
		
		header = new JLabel("Enter IP and Port information below");
		URL = new JLabel("IP: ");
		port = new JLabel("Port: ");
		
		ip = new JTextField();
		ip.setColumns(10);
		portNum = new JTextField();
		portNum.setColumns(10);
		
		connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	try {
		    		//Open a connection to the listening server.
		    		setIP();
		    		setPort();
		    		client = new ChessClient(getIP(), getPort());
		    		client.setClient(client);
		    		client.openConnection();
		    		header.setText("Connected. Waiting for another player to connect...");
		    		ip.setEditable(false);
					portNum.setEditable(false);
		        } 
		        catch (IOException e1) {
		    	    e1.printStackTrace();
		        }
		    }
		        });
		exit = new JButton("Exit");
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		l1.putConstraint(SpringLayout.NORTH, header, 10, SpringLayout.NORTH, panel);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, header, 0, SpringLayout.HORIZONTAL_CENTER, panel);
		l1.putConstraint(SpringLayout.NORTH, URL, 10, SpringLayout.SOUTH, header);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, URL, -50, SpringLayout.HORIZONTAL_CENTER, panel);
		l1.putConstraint(SpringLayout.NORTH, port, 10, SpringLayout.SOUTH, URL);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, port, -50, SpringLayout.HORIZONTAL_CENTER, panel);
		l1.putConstraint(SpringLayout.NORTH, ip, 10, SpringLayout.SOUTH, header);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, ip, 50, SpringLayout.HORIZONTAL_CENTER, panel);
		l1.putConstraint(SpringLayout.NORTH, portNum, 10, SpringLayout.SOUTH, ip);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, portNum, 50, SpringLayout.HORIZONTAL_CENTER, panel);
		l1.putConstraint(SpringLayout.SOUTH, connect, -50, SpringLayout.SOUTH, panel);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, connect, -50, SpringLayout.HORIZONTAL_CENTER, panel);
		l1.putConstraint(SpringLayout.SOUTH, exit, -50, SpringLayout.SOUTH, panel);
		l1.putConstraint(SpringLayout.HORIZONTAL_CENTER, exit, 50, SpringLayout.HORIZONTAL_CENTER, panel);
		
		panel.add(header);
		panel.add(URL);
		panel.add(port);
		panel.add(ip);
		panel.add(portNum);
		panel.add(connect);
		panel.add(exit);
		
		this.add(panel);
		this.setSize(350, 300);
		this.setVisible(true);
	}
	
	public void setIP() {
		this.userIp = ip.getText();
	}
	
	public void setPort() {
		this.userPort = Integer.parseInt(portNum.getText());
	}
	
	public String getIP() {
		return userIp;
	}
	
	public int getPort() {
		return userPort;
	}
}
