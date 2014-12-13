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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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

class Server extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextField nameField, sizeField, typeField;
	// textfields in front Dialog

	MyJButton browse, start, minimize, cross;
	// browse is to select the file and start is to start the server

	JFileChooser jfc;
	// to select the file from local computer

	MyJLabel status, address;
	// shows current status of the app

	JPanel lowerBorder;
	// is removed afterwards, so it is declared class-wide

	JProgressBar jpb;
	// progress bar to show file completion

	final String sizeArray[] = { "B", "KB", "MB", "GB", "TB" };
	// final string array to set the size of file in desired range

	int screenX, screenY, myX, myY;
	// dialog is undecorated, so these coordinates help to move the frame in the
	// screen

	boolean startFlag = false;
	// this flag sets the StartServer button "working"

	double fileSize;
	// stores the size of file in bytes

	String serverAddress = "", tempAddress = "";
	// string stores the address of the server, determined inside constructor

	boolean theAppIsRunning = true;

	// for closing misc threads

	private void add(JComponent parent, JComponent component, int gridx,
			int gridy, int gridwidth, int gridheight, int posInCell,
			Insets insets)
	{
		parent.add(component, new GridBagConstraints(gridx, gridy, gridwidth,
				gridheight, 1.0, 1.0, posInCell, GridBagConstraints.NONE,
				insets, 0, 0));
	}

	// GridBagLayout alignment and add function
	void retrieveServerIP(boolean flag) // flag is false when it is executed for
										// first time, else label is updated
	{
		try
		{
			tempAddress = InetAddress
					.getLocalHost()
					.toString()
					.substring(
							InetAddress.getLocalHost().toString()
									.lastIndexOf("/") + 1);
		}
		catch (Exception e)
		{

			if (Menu.applicationDebuggingMode)
				e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							this,
							"Unable to Retrieve System IP, Restart the Application and Retry",
							"Runtime Error !", 0); // EIWQ
			System.exit(0);
		}
		if (!(tempAddress.equals(serverAddress)))
		{
			serverAddress = tempAddress;
			if (flag)
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						address.setText("<html>Type this in client app : <u>"
								+ serverAddress + "</u> </html>");
					}
				});
		}
	}

	// starting constructor for basic initializations
	Server(String path)
	{
		this();
		openFile(new File(path));
	}

	Server()
	{
		// Retrieve Server's IP
		retrieveServerIP(false);

		// Constructos Declarations
		MyJLabel nameLabel, sizeLabel, typeLabel, title;
		JPanel panel, upperBorder, flowPanel;

		// Initializing References

		browse = new MyJButton("Browse");
		start = new MyJButton("Send File");
		cross = new MyJButton("");
		minimize = new MyJButton("");

		jfc = new JFileChooser();

		jpb = new JProgressBar();
		jpb.setStringPainted(true);
		jpb.setFont(new Font(Menu.defaultFont, Font.PLAIN, 22));
		jpb.setForeground(Menu.backgroundAndBorderColor);
		jpb.setBackground(Color.BLACK);

		nameLabel = new MyJLabel("File Name");
		sizeLabel = new MyJLabel("File Size");
		typeLabel = new MyJLabel("File Type");
		title = new MyJLabel("  Smart File Transfer (Server)");

		address = new MyJLabel("<html>Type this in client app : <u>"
				+ serverAddress + "</u> </html>");
		status = new MyJLabel("Select File and Start Server");

		panel = new JPanel();
		panel.setBackground(Menu.backgroundAndBorderColor);

		upperBorder = new JPanel();
		lowerBorder = new JPanel();
		flowPanel = new JPanel();

		nameField = new JTextField(30);
		sizeField = new JTextField(20);
		typeField = new JTextField(20);

		/* Set general properties of components */
		nameField.setEditable(false);
		sizeField.setEditable(false);
		typeField.setEditable(false);

		cross.setIcon(new ImageIcon("res\\close.png"));
		minimize.setIcon(new ImageIcon("res\\minimize.png"));

		upperBorder.setBackground(Menu.upperAndLowerColor);
		lowerBorder.setBackground(Menu.upperAndLowerColor);
		flowPanel.setBackground(Menu.upperAndLowerColor);

		nameField.setForeground(Menu.backgroundAndBorderColor);
		sizeField.setForeground(Menu.backgroundAndBorderColor);
		typeField.setForeground(Menu.backgroundAndBorderColor);

		// setting Panel and adding components to Panel
		panel.setLayout(new GridBagLayout());
		upperBorder.setLayout(new BorderLayout());
		lowerBorder.setLayout(new FlowLayout(FlowLayout.CENTER));
		lowerBorder.add(status);

		/* add components to panel using method */
		add(panel, nameLabel, 0, 1, 1, 1, GridBagConstraints.WEST, new Insets(
				0, 20, 0, 0));
		add(panel, nameField, 1, 1, 3, 1, GridBagConstraints.WEST, new Insets(
				0, 30, 0, 0));
		add(panel, browse, 4, 1, 1, 1, GridBagConstraints.CENTER, new Insets(0,
				0, 0, 0));
		add(panel, sizeLabel, 0, 2, 1, 1, GridBagConstraints.WEST, new Insets(
				0, 20, 0, 0));
		add(panel, sizeField, 1, 2, 3, 1, GridBagConstraints.WEST, new Insets(
				0, 30, 0, 0));
		add(panel, typeLabel, 0, 3, 1, 1, GridBagConstraints.WEST, new Insets(
				0, 20, 0, 0));
		add(panel, typeField, 1, 3, 3, 1, GridBagConstraints.WEST, new Insets(
				0, 30, 0, 0));
		add(panel, start, 2, 4, 1, 1, GridBagConstraints.CENTER, new Insets(0,
				0, 0, 0));
		add(panel, address, 0, 5, 0, 1, GridBagConstraints.CENTER, new Insets(
				0, 0, 0, 0));

		setFont(new Font(Menu.defaultFont, Font.PLAIN, 22), nameLabel,
				sizeLabel, typeLabel);
		setFont(new Font(Menu.defaultFont, Font.PLAIN, 16), nameField,
				sizeField, typeField);
		setFont(new Font(Menu.defaultFont, Font.PLAIN, 22), cross, address,
				minimize);
		setFont(new Font(Menu.defaultFont, Font.PLAIN, 22), start, browse,
				status, title);

		// add Listeners
		browse.addActionListener(this);
		start.addActionListener(this);
		cross.addActionListener(this);
		minimize.addActionListener(this);

		// set Top Level Container Properties
		flowPanel.add(minimize);
		flowPanel.add(cross);
		upperBorder.add(flowPanel, BorderLayout.EAST);
		upperBorder.add(title); // not centered, towards left
		add(upperBorder, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(lowerBorder, BorderLayout.SOUTH);

		/* add window listener to disable flag and stop misc threads */
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				theAppIsRunning = false;
			}
		});

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

		/* Set general properties of main frame */
		setSize(800, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		getRootPane().setDefaultButton(start);
		setVisible(true);
		browse.requestFocus();

		/*
		 * Start thread to retrieve IP on local machine.
		 * 
		 * This is running in infinite loop because if IP changes after starting
		 * the application, it will be reflected in the Application's GUI
		 */
		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					retrieveServerIP(true);
					if (!theAppIsRunning)
						break;
				}
			}
		}).start();
	}

	/* Method to set font for multiple components */
	private void setFont(Font f, JComponent... c)
	{
		for (JComponent C : c)
			C.setFont(f);
	}

	/* Locate the file to be sent */
	private void openFile(File path)
	{
		File f = path;
		int counter = 0;
		fileSize = f.length();
		if (fileSize == 0L)
		{
			JOptionPane.showMessageDialog(null,
					"No Valid file found on specified location",
					"File not found", 0);
			return;
		}
		String extension = f
				.toString()
				.substring(f.toString().lastIndexOf(".") + 1,
						f.toString().length()).toUpperCase();
		long tempSize = (long) fileSize;
		while (tempSize > 1000)
		{
			counter++;
			tempSize /= 1024;
		}
		if (extension.equals("JAVA") || extension.equals("C")
				|| extension.equals("CPP") || extension.equals("CS")
				|| extension.equals("CSS") || extension.equals("HTML")
				|| extension.equals("JS") || extension.equals("PHP")
				|| extension.equals("XML") || extension.equals("VB"))
			extension += " Source File";
		else if (extension.equals("JPG") || extension.equals("BMP")
				|| extension.equals("PNG") || extension.equals("GIF")
				|| extension.equals("TIFF") || extension.equals("ICO"))
			extension += " File (Image/Icon)";
		else if (extension.equals("MPG") || extension.equals("MPEG")
				|| extension.equals("MP4") || extension.equals("AVI")
				|| extension.equals("3GP") || extension.equals("RMVB")
				|| extension.equals("WMV") || extension.equals("MKV")
				|| extension.equals("VOB") || extension.equals("MOV")
				|| extension.equals("FLV"))
			extension += " File (Video)";
		else if (extension.equals("MP3") || extension.equals("WMA")
				|| extension.equals("FLAC") || extension.equals("AAC")
				|| extension.equals("AMF") || extension.equals("AMR")
				|| extension.equals("M4A") || extension.equals("M4R")
				|| extension.equals("OGG") || extension.equals("MP2")
				|| extension.equals("WAV"))
			extension += " File (Audio)";
		else if (extension.equals("EXE") || extension.equals("CMD")
				|| extension.equals("BAT") || extension.equals("DMG")
				|| extension.equals("MSI"))
			extension += " File (Executable File/Script)";
		else
			extension += " Document/File";
		nameField.setText(f.toString());
		sizeField.setText(tempSize + " " + sizeArray[counter]);
		typeField.setText(extension);
		startFlag = true;
	}

	/* ActionListener / ButtonListener */
	public void actionPerformed(ActionEvent ae)
	{
		String s = ae.getActionCommand();
		Object source = ae.getSource();

		/* If the user chooses to browse the file */
		if (s.equals("Browse"))
		{
			int ret = jfc.showOpenDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION)
				openFile(jfc.getSelectedFile());
			start.requestFocusInWindow();
		}

		/* Button to exit application */
		else if (source == cross)
			dispose();

		/* Button to minimize application */
		else if (source == minimize)
			setState(JFrame.ICONIFIED);

		/* Start sending file in a separate Thread */
		else if (s.equals("Send File"))
			if (startFlag)
				new Thread(new Runnable()
				{
					public void run()
					{
						startServer();
					}
				}).start();
			else
				JOptionPane.showMessageDialog(null,
						"Select a Valid file first ( size > 0B )", "Error", 0);
	}

	/* Method responsible for actually sending the file */
	private void startServer()
	{
		start.setEnabled(false);
		browse.setEnabled(false);
		status.setText("Waiting for Client to Connect");

		double startTime, endTime;
		// time variables;

		byte b[] = new byte[100000];
		// array to retrieve data from server and send to client

		double done = 0;
		// done is used to count the percentage

		int read = 0;
		// read counts the bytes read (within 4 bytes integer range) in WHILE
		// loop

		String data, fileName = nameField.getText().substring(
				nameField.getText().lastIndexOf("\\") + 1);
		// data is the data to be sent via BR, stores (filename + length)
		// fileName stores the name of the file

		// constructing streams
		BufferedReader br = null;
		// to read String and long data via Socket

		PrintWriter pw = null;
		// to write String and long data via Socket

		BufferedInputStream bis = null;
		// to write file contents (byte stream) via Socket

		BufferedOutputStream bos = null;
		// to read byte data via Socket

		FileInputStream fis = null;
		// to read actual file using byte stream

		ServerSocket ss = null;
		// this will open a socket on port 4000 on local system

		Socket s = null;
		// this will serve a local port for a client

		// now allocating memory to objects and starting main logic

		data = fileName + "/" + new Double(fileSize);
		try
		{

			/* Start server on port 4000 */
			ss = new ServerSocket(4000);
			s = ss.accept();
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
		}
		catch (IOException ioe)
		{
			if (Menu.applicationDebuggingMode)
				ioe.printStackTrace();
			JOptionPane.showMessageDialog(null, ioe.toString(), "error", 0);
			System.exit(0);
		}
		pw.println(data);
		pw.flush();
		try
		{
			status.setText("Begining File Transfer");
			if (!(br.readLine().equals("Recieved")))
			{
				JOptionPane
						.showMessageDialog(null,
								"Client not Ready or connection was closed",
								"Retry", 2);
				status.setText("Faliure");
				System.exit(0);
			}

			bis = new BufferedInputStream(s.getInputStream());
			bos = new BufferedOutputStream(s.getOutputStream());
			fis = new FileInputStream(nameField.getText());
			status.setText("Sending file to " + s);

			lowerBorder.remove(status);
			remove(lowerBorder);
			add(jpb, BorderLayout.SOUTH);
			repaint();
			revalidate();

			startTime = System.currentTimeMillis();
			while ((read = fis.read(b)) != -1)
			{
				done += read;
				ServerSwingWorker ssw = new ServerSwingWorker(done, fileSize,
						read, b, bos, jpb);
				ssw.execute();
				while (!(ssw.isDone()))
				{
				}
			}
			bos.flush();
			endTime = System.currentTimeMillis();
			browse.setEnabled(true);
			start.setEnabled(true);
			nameField.setText("");
			sizeField.setText("");
			typeField.setText("");
			remove(jpb);
			lowerBorder.add(status);
			add(lowerBorder, BorderLayout.SOUTH);
			startFlag = false;
			repaint();
			revalidate();
			status.setText("Sent 100 %");
			double time = (endTime - startTime) / 1000;
			double speed = (fileSize / time) / 1048576;
			String speedString = String.valueOf(speed);
			int indexOfDot = speedString.indexOf('.');
			int length = speedString.length();
			if (length > indexOfDot + 2)
				speedString = speedString.substring(0, indexOfDot + 2);

			JOptionPane.showMessageDialog(null, "Time taken is " + time
					+ "\nSpeed is " + speedString + " MBPS",
					"File Sent (Server)", 3);
			status.setText("Select File and Start Server");

			// reset for further operation
			status.setText("Select File and Start Server");
			bis.close();
			bos.close();
			fis.close();
			br.close();
			pw.close();
			ss.close();
			s.close();
		}
		catch (IOException ioe)
		{

			if (Menu.applicationDebuggingMode)
				ioe.printStackTrace();
			JOptionPane.showMessageDialog(null, ioe.toString(), "Error", 0);
			System.exit(0);
		}

	}
}

/*
 * This class is used to send file and update the GUI accordingly As per
 * specifications, we must update the GUI through event dispatching thread (EDT)
 * while the time consuming work should be done in a separate thread.
 * 
 * Time consuming work like making connection to a server, sending/receiving
 * file etc should be done in a separate thread, but to update GUI (say
 * progressbar) accordingly, we extend SwingWorker class. For information
 * regarding methods, generics etc, you can refer javadoc of SwingWorker class
 */
class ServerSwingWorker extends SwingWorker<Void, Void>
{
	JProgressBar jpb;
	final double done, size;
	byte b[] = new byte[100000];
	final int read;
	BufferedOutputStream bos;

	/* Assign references */
	ServerSwingWorker(double done, double size, int read, byte b[],
			BufferedOutputStream bos, JProgressBar jpb)
	{
		this.done = done;
		this.size = size;
		this.read = read;
		this.b = b;
		this.jpb = jpb;
		this.bos = bos;
	}

	/* Do the logical (time consuming) work here */
	protected Void doInBackground() throws Exception
	{
		bos.write(b, 0, read);
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
				tString = new Double(temp).toString();
				tString = tString.substring(0, breakPoint);
				jpb.setString("Sending : " + tString + " %");
				jpb.setValue((int) temp);
			}
		});
	}
}