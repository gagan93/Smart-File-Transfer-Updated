package com.gogo.sft;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;

public class MyJButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyJButton(String text)
	{
		super(text);
		setFocusPainted(false);
		setBorder(BorderFactory.createCompoundBorder(null,
				BorderFactory.createEmptyBorder(5, 15, 5, 15)));
		UIManager.put("Button.select", Color.BLACK);
		setForeground(Color.WHITE);
		setBackground(Color.BLACK);
	}
}
