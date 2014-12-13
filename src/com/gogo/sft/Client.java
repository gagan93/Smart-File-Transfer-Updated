package com.gogo.sft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

class Client extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField address;
	MyJButton recieve, cross, minimize;
	MyJLabel status, title;
	int screenX, screenY, myX, myY;
	JPanel lowerBorder;
	JProgressBar jpb;

	String location; // represents location where downloaded file is to be saved

	private void setFont(Font f, JComponent... c)
	{
		for (JComponent C : c)
			C.setFont(f);
	}

	private void add(JComponent parent, JComponent component, int gridx,
			int gridy, int gridwidth, int gridheight, int posInCell,
			Insets insets)
	{
		parent.add(component, new GridBagConstraints(gridx, gridy, gridwidth,
				gridheight, 1.0, 1.0, posInCell, GridBagConstraints.NONE,
				insets, 0, 0));
	}

	Client()
	{
		JPanel upperBorder, panel, flowPanel;
		final MyJLabel defaultLocation = new MyJLabel("Default location is : "
				+ (location = defLoc()));
		final MyJLabel changeLocation = new MyJLabel(
				"<html><u>Change Location where downloaded file is saved <u></html>");

		/* Initialise GUI components */
		address = new JTextField("Enter Address as specified by Server");
		recieve = new MyJButton("Recieve File");
		cross = new MyJButton("");
		minimize = new MyJButton("");
		status = new MyJLabel(
				"Enter Server Adress and press 'Recieve File' to Continue");
		title = new MyJLabel("  Smart File Transfer (Client)");

		address.setForeground(Menu.backgroundAndBorderColor);

		jpb = new JProgressBar();
		jpb.setStringPainted(true);
		jpb.setFont(new Font(Menu.defaultFont, Font.PLAIN, 20));
		jpb.setForeground(Menu.backgroundAndBorderColor);
		jpb.setBackground(Color.BLACK);

		panel = new JPanel();
		upperBorder = new JPanel();
		lowerBorder = new JPanel();
		flowPanel = new JPanel();

		cross.setIcon(new ImageIcon("res\\close.png"));
		minimize.setIcon(new ImageIcon("res\\minimize.png"));

		/* Set general properties of GUI components */
		panel.setBackground(Menu.backgroundAndBorderColor);
		upperBorder.setBackground(Menu.upperAndLowerColor);
		lowerBorder.setBackground(Menu.upperAndLowerColor);
		flowPanel.setBackground(Menu.upperAndLowerColor);

		setFont(new Font(Menu.defaultFont, Font.PLAIN, 20), status, recieve,
				title);
		setFont(new Font(Menu.defaultFont, Font.PLAIN, 22), cross, minimize);
		setFont(new Font(Menu.defaultFont, Font.PLAIN, 26), address);

		panel.setLayout(new GridBagLayout());
		upperBorder.setLayout(new BorderLayout());
		lowerBorder.setLayout(new FlowLayout(FlowLayout.CENTER));

		/* add components to parent panel */
		add(panel, address, 0, 0, 1, 1, GridBagConstraints.CENTER, new Insets(
				10, 10, 10, 10));
		add(panel, recieve, 0, 1, 1, 1, GridBagConstraints.CENTER, new Insets(
				10, 10, 10, 10));
		add(panel, defaultLocation, 0, 2, 3, 1, GridBagConstraints.WEST,
				new Insets(0, 0, 0, 0));
		add(panel, changeLocation, 0, 3, 3, 1, GridBagConstraints.WEST,
				new Insets(0, 0, 0, 0));

		flowPanel.add(minimize);
		flowPanel.add(cross);
		upperBorder.add(flowPanel, BorderLayout.EAST);
		upperBorder.add(title); // not centered, towards left.
		lowerBorder.add(status);
		add(panel);
		add(upperBorder, BorderLayout.NORTH);
		add(lowerBorder, BorderLayout.SOUTH);
		final JTextField temp = address;

		/*
		 * these two mouse listeners are to notice cursor position for dragging
		 * (because we have created our own title bar
		 */
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent me)
			{
				screenX = me.getXOnScreen();
				screenY = me.getYOnScreen();
				myX = getX();
				myY = getY();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent me)
			{
				int deltaX = me.getXOnScreen() - screenX;
				int deltaY = me.getYOnScreen() - screenY;
				setLocation(myX + deltaX, myY + deltaY);
			}
		});
		address.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				temp.setText("");
				temp.removeMouseListener(this);
			}
		});

		/*
		 * We do not have a Hyperlink button readymade in Java, so I have used
		 * MyJLabel and added few mouse listeners to make it behave like one
		 */
		changeLocation.addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent me)
			{
				changeLocation.setForeground(Color.GRAY);
			}

			public void mouseExited(MouseEvent me)
			{
				changeLocation.setForeground(Color.WHITE);
			}

			public void mouseClicked(MouseEvent me)
			{
				/*
				 * This part is responsible to choose default save location of
				 * file
				 */
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setCurrentDirectory(new File(location));
				int ret = jfc.showSaveDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						FileWriter fw = new FileWriter("c:\\sft\\log.bin");
						fw.write(jfc.getSelectedFile().toString());
						fw.close();
						defaultLocation
								.setText("Default Location is : "
										+ (location = jfc.getSelectedFile()
												.toString()));
					}
					catch (Exception e)
					{
						JOptionPane
								.showMessageDialog(
										null,
										"Error in changing path, The application will now exit",
										"Fatal Error", 0);
						if (Menu.applicationDebuggingMode)
							e.printStackTrace();
					}
				}
			}
		});
		recieve.addActionListener(this);
		cross.addActionListener(this);
		minimize.addActionListener(this);

		/* Set properties of main frame */
		setResizable(false);
		setUndecorated(true);
		setSize(600, 300);
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		getRootPane().setDefaultButton(recieve);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		requestFocus();
	}

	/* saves and retrieves default saving location from file */

	private String defLoc()
	{
		String ret = "";
		try
		{
			BufferedReader br = new BufferedReader(new FileReader("log.bin"));
			String s = br.readLine();
			br.close();
			ret = s;
			File f = new File(s);
			if (!f.isDirectory())
				new FileReader(s);
		}
		catch (Exception e)
		{
			if (e instanceof FileNotFoundException
					|| e instanceof NullPointerException)
			{
				if (Menu.applicationDebuggingMode)
					e.printStackTrace();
				try
				{
					FileWriter fw = new FileWriter("log.bin");
					ret = System.getProperty("user.home") + "\\Desktop\\";
					fw.write(ret);
					fw.close();
				}
				catch (Exception ee)
				{
					JOptionPane.showMessageDialog(null,
							"Critical Error, The application will now exit",
							"Critical Error", 0);

					if (Menu.applicationDebuggingMode)
						ee.printStackTrace();
				}
			}
		}
		return ret;
	}

	public void actionPerformed(ActionEvent ae)
	{
		String com = ae.getActionCommand();
		Object source = ae.getSource();

		/* First two cases control minimize and close actions */
		if (source == minimize)
			setState(JFrame.ICONIFIED);
		else if (source == cross)
			dispose();

		/* This part is actually responsible for receiving the file */
		else if (com.equals("Recieve File"))
		{
			recieve.setEnabled(false);
			int add[] = new int[4];
			int counter = 0;
			StringTokenizer st = new StringTokenizer(address.getText(), ".");
			while (st.hasMoreTokens())
			{
				try
				{
					if (counter >= 4)
						new Integer("g");
					add[counter++] = new Integer(st.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					address.setText("");
					recieve.setEnabled(true);
					JOptionPane
							.showMessageDialog(
									null,
									"IP address has only no.s seperated by dots (.)\nIt is of the form \"xxx.xxx.xxx.xxx\" \nwhere xxx is a no. less than 256",
									"Integer Data Expected", 0);

					if (Menu.applicationDebuggingMode)
						nfe.printStackTrace();
					return;
				}
			}

			/*
			 * Basic IP address validation
			 */
			if (!(counter == 4) || add[0] > 255 || add[1] > 255 || add[2] > 255
					|| add[3] > 255)
			{
				address.setText("");
				recieve.setEnabled(true);
				JOptionPane
						.showMessageDialog(
								null,
								"Incorrect IP Address \nIt is of the form \"xxx.xxx.xxx.xxx\" \nwhere xxx is a no. less than 256",
								"Invalid IP", 0);
				return;
			}
			/* start receiving file in a separate thread */
			new Thread(new Runnable()
			{
				public void run()
				{
					recieveFile();
				}
			}).start();
		}
	}

	private void recieveFile()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				status.setText("Searching for server...Please wait");
				address.setEnabled(false);
			}
		});
		byte b[] = new byte[100000];
		// array to retrieve data from server and send to client

		String sizeName[] = new String[2];
		// stores size and name of file as recieved from character streams

		double done = 0, length;
		// done is used to count the percentage

		int read = 0, i = 0;
		/*
		 * read counts the bytes read (within 4 bytes integer range) in WHILE
		 * loop
		 */

		// constructing streams
		BufferedReader br = null;
		// to read String and long data via Socket

		PrintWriter pw = null;
		// to write String and long data via Socket

		BufferedInputStream bis = null;
		// to write file contents (byte stream) via Socket

		BufferedOutputStream bos = null;
		// to read byte data via Socket

		FileOutputStream fos = null;
		// to read actual file using byte stream

		Socket s = null;
		// this will serve a local port for a client

		// now allocating memory to objects and starting main logic

		try
		{
			/* connect to server on port 4000 */
			s = new Socket(address.getText(), 4000);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
			StringTokenizer st = new StringTokenizer(br.readLine(), "/");
			status.setText("Connection Established, about to begin download");
			while (st.hasMoreTokens())
				sizeName[i++] = st.nextToken();
			pw.println("Recieved");
			pw.flush();
			length = new Double(sizeName[1]);
			bis = new BufferedInputStream(s.getInputStream());
			bos = new BufferedOutputStream(s.getOutputStream());
			fos = new FileOutputStream(location + "\\" + sizeName[0]);

			lowerBorder.remove(status);
			remove(lowerBorder);
			add(jpb, BorderLayout.SOUTH);
			repaint();
			revalidate();
			while (true)
			{
				done += read;
				if (done >= length)
				{
					break;
				}
				read = bis.read(b);
				ClientSwingWorker csw = new ClientSwingWorker(done, length,
						read, b, fos, jpb);
				csw.execute();
				while (!(csw.isDone()))
				{
				}
			}
			fos.flush();

			address.setEnabled(true);
			recieve.setEnabled(true);
			remove(jpb);
			lowerBorder.add(status);
			add(lowerBorder, BorderLayout.SOUTH);
			status.setText("Recieved 100%");
			repaint();
			revalidate();
			bis.close();
			bos.close();
			fos.close();
			pw.close();
			br.close();
			s.close();

			JOptionPane.showMessageDialog(null, "File Recieved and saved on "
					+ defLoc(), "File Recieved (Client)", 3);
			status.setText("Enter Server Adress and press 'Recieve File' to Continue");
		}
		catch (Exception e)
		{

			if (Menu.applicationDebuggingMode)
				e.printStackTrace();
			if (e instanceof ConnectException)
			{
				address.setText("Enter Address as specified by Server");
				status.setText("Enter Server Adress and press 'Connect and Start' button to Continue");
				address.setEnabled(true);
				recieve.setEnabled(true);
				JOptionPane.showMessageDialog(null,
						"No Running Server found on specified address [ "
								+ address.getText() + " ]", "Server Not Found",
						0);
				final JTextField temp = address;
				address.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me)
					{
						temp.setText("");
						temp.removeMouseListener(this);
					}
				});
				return;
			}
			else if (e instanceof FileNotFoundException)
			{
				JOptionPane
						.showMessageDialog(
								null,
								"Failed in saving file,\nLocation : "
										+ location
										+ " required administrative rights to save or it was an invalid path\nSelect some other location for downloaded files\nThe Program will now Exit and default location would be reset",
								"Error [" + location + "]", 0);
				try
				{
					FileWriter fw = new FileWriter("c:\\sft\\log.bin");
					fw.close();
				}
				catch (IOException ee)
				{
					if (Menu.applicationDebuggingMode)
						ee.printStackTrace();
				}
				System.exit(1);
			}
		}
	}
}

/*
 * 
 * This class is used to send file and update the GUI accordingly As per
 * specifications, we must update the GUI through event dispatching thread (EDT)
 * while the time consuming work should be done in a separate thread.
 * 
 * Time consuming work like making connection to a server, sending/receiving
 * file etc should be done in a separate thread, but to update GUI (say
 * progressbar) accordingly, we extend SwingWorker class. For information
 * regarding methods, generics etc, you can refer javadoc of SwingWorker class
 */
class ClientSwingWorker extends SwingWorker<Void, Void>
{
	JProgressBar jpb;
	final double done, size;
	byte b[] = new byte[100000];
	final int read;
	FileOutputStream fos;

	/* Assign references */
	ClientSwingWorker(double done, double size, int read, byte b[],
			FileOutputStream fos, JProgressBar jpb)
	{
		this.done = done;
		this.size = size;
		this.read = read;
		this.b = b;
		this.jpb = jpb;
		this.fos = fos;
	}

	/* Do the logical (time consuming) work here */
	protected Void doInBackground() throws Exception
	{
		fos.write(b, 0, read);
		return null;
	}

	/* update the GUI here */
	protected void done()
	{
		final double temp = (done / size) * 100;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				String tString = new Double(temp).toString();
				int index = tString.indexOf(".");
				int breakPoint = (index + 3) > tString.length() ? tString
						.length() : (index + 3);
				tString = tString.substring(0, breakPoint);
				jpb.setString("Recieving : " + tString + " %");
				jpb.setValue((int) temp);
			}
		});
	}
}
