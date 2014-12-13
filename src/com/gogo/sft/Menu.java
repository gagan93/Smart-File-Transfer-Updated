package com.gogo.sft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Menu extends JFrame implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Labels used to represent title and some message */
	MyJLabel choose, title;
	
	/* Buttons for various operations */ 
	MyJButton sender, receiver, cross, minimize;
	
	/* Panels for various positions */
	JPanel upperPanel, mainPanel, flowPanel, lowerPanel;

	// current font used everywhere in the application
	public static final String defaultFont = "segoe ui light";
	
	// colors of 1. background and border 2. upper and lower panels
	public static final Color backgroundAndBorderColor = new Color(86, 164, 246);
	public static final Color upperAndLowerColor = Color.BLACK;

	/* Integers controlling mouse drag */
	protected int screenX, screenY, myX, myY;
	static String arg[];
	
	public static final boolean applicationDebuggingMode=false;

	Menu()
	{
		/* Constructor initializations */
		choose = new MyJLabel("What do you want to do ?");
		title = new MyJLabel("  Smart File Transfer Menu");
		sender = new MyJButton("Send File");
		receiver = new MyJButton("Recieve File");
		cross = new MyJButton("");
		minimize = new MyJButton("");

		upperPanel = new JPanel();
		mainPanel = new JPanel();
		flowPanel = new JPanel();
		lowerPanel = new JPanel();

		cross.setIcon(new ImageIcon("res\\close.png"));
		minimize.setIcon(new ImageIcon("res\\minimize.png"));
		choose.setFont(new Font(defaultFont, Font.PLAIN, 28));
		setFont(new Font(defaultFont, Font.PLAIN, 24), sender, receiver, title);
		setFont(new Font(defaultFont, Font.PLAIN, 18), cross, minimize);

		title.setForeground(Color.WHITE);

		mainPanel.setBackground(backgroundAndBorderColor);
		flowPanel.setBackground(upperAndLowerColor);
		upperPanel.setBackground(upperAndLowerColor);
		lowerPanel.setBackground(upperAndLowerColor);

		mainPanel.setLayout(new GridBagLayout());
		upperPanel.setLayout(new BorderLayout());

		flowPanel.add(minimize);
		flowPanel.add(cross);

		upperPanel.add(flowPanel, BorderLayout.EAST);
		upperPanel.add(title, BorderLayout.WEST);
		
		lowerPanel.add(choose);

		mainPanel.add(sender, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						50, 0, 50, 0), 0, 0));
		mainPanel.add(receiver, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						50, 0, 50, 0), 0, 0));

		add(upperPanel, BorderLayout.NORTH);
		add(mainPanel);
		add(lowerPanel , BorderLayout.SOUTH);

		cross.setFocusable(false);
		cross.addActionListener(this);
		minimize.setFocusable(false);
		minimize.addActionListener(this);

		sender.addActionListener(this);
		receiver.addActionListener(this);

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
		setSize(600, 250);
		setBackground(backgroundAndBorderColor);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getRootPane().setBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1));
		setVisible(true);
	}

	private void setFont(Font f, JComponent... c)
	{
		for (JComponent C : c)
			C.setFont(f);
	}

	/* Main method */

	public static void main(final String... args)
	{
		arg = args;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new Menu();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == sender)
			if (arg.length == 1 && !(arg[0].equals("%1")))
				new Server(arg[0]);
			else
				new Server();
		else if (source == receiver)
			new Client();
		else if (source == minimize)
			setState(JFrame.ICONIFIED);
		else
			System.exit(0);
	}
}
