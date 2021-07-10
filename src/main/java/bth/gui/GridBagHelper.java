//|-----------------------------|
//| GridBagHelper				|
//| provide an help				|
//|	for laying component in a 	|
//|	GridBoxLayout 				|
//|-----------------------------|

package bth.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GridBagHelper {
	
	//|----------------|
	//| Exemple of use |
	//|----------------|
	public static void main(String[] args)
	{
		//-- Init a new frame for the exemple --
		JFrame mWin = new JFrame();
		mWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mWin.setSize(800,  600);
		JPanel mc = new JPanel();
		
		//---------------------------------------
		
		//Init a new container
		JPanel container = new JPanel();
		//Set the border style of this
		container.setBorder(BorderFactory.createTitledBorder("an input container"));
		
		//Init a new GridBagLayout and our GridBagHelper
		GridBagLayout layout = new GridBagLayout();
		GridBagHelper lh = new GridBagHelper(container, layout);
		
		//Starting to add new components with the add method of our GBH
		lh.add(new JLabel("Field 1 : "), 0, 0, 1);
		lh.add(new JTextField(15), 0, 1, 1);
		lh.add(new JLabel("Field 2 : "), 1, 0, 1);
		lh.add(new JTextField(), 1, 1, 1);
		lh.add(new JCheckBox("Checkbox"), 2, 0, 2, 0, new Insets(10, 0, 0, 0));
		lh.add(new JLabel("Field 3 : "), 3, 0, 1);
		lh.add(new JTextField(15), 3, 1, 1);
		lh.add(new JLabel("Field 4 : "), 4, 0, 1);
		lh.add(new JPasswordField(), 4, 1, 0);
		
		//Finally add the container to the main container
		mc.add(container);
		
		//Finallize the window setting
		mWin.setContentPane(mc);
		mWin.pack();
		mWin.setVisible(true);
		
	}
	
	GridBagLayout l = null;
	Container c = null;
	
	
	public GridBagHelper(Container p_c, GridBagLayout p_gbl)
	{
		c = p_c;
		l = p_gbl;
		c.setLayout(l);
		
	}
	
	public void add(Component comp, int row, int col, int span)
	{
		GridBagConstraints lc = new GridBagConstraints();
		lc.fill = GridBagConstraints.BOTH;
		lc.gridy = row;
		lc.gridx = col;
		lc.gridwidth = span;
		c.add(comp, lc);
	}
	
	public void add(Component comp, int row, int col, int span, int anchor, Insets insets)
	{
		GridBagConstraints lc = new GridBagConstraints();
		lc.fill = GridBagConstraints.BOTH;
		lc.gridy = row;
		lc.gridx = col;
		lc.gridwidth = span;
		if(anchor != 0)
		{
			lc.anchor = anchor;
		}
		if(insets != null)
		{
			lc.insets = insets;
		}
		c.add(comp, lc);
	}

}
