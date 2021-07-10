package bth.gui.menu;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bth.gui.MWin;
import bth.gui.options.OptionsGui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Menu extends JMenuBar {
	
	private final MWin mWin;
	private JDialog dOpt;
	
	public Menu(final MWin p_mWin)
	{
		mWin = p_mWin;
		
		//MenuBar items
		JMenu f = new JMenu("File");
		JMenu e = new JMenu("Edit");
		JMenu h = new JMenu("Help");
		
		//File items
		JMenuItem fExit = new JMenuItem("Exit");
		
		//File items implements
		f.add(fExit);
		
		//Edit items
		JMenuItem eRefresh = new JMenuItem("Refresh");
		JMenuItem eOptions = new JMenuItem("Options");
		//Edit items implements
		e.add(eRefresh);
		e.addSeparator();
		e.add(eOptions);
		eRefresh.setToolTipText("(F5)");
		
		
		//Help items
		JMenuItem hAPropos = new JMenuItem("APropos");
		//Help items implements
		h.add(hAPropos);
		
		//Menu implements
		add(f);
		add(e);
		add(h);
		
		//Event Manager
		fExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev)
			{
				System.exit(0);
			}
		});
		
		eRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev)
			{
				mWin.reload();
			}
		});
		
		eOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev)
			{
				dOpt = new JDialog();
				dOpt.setMinimumSize(new Dimension(800, 680));
				dOpt.setSize(800, 680);
				dOpt.setTitle("BT's Helper -> Options");
				dOpt.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dOpt.setContentPane(new OptionsGui(mWin, dOpt));
				dOpt.setVisible(true);
	
			}
			
		});
		hAPropos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev)
			{
				JDialog apropos = new APropos();
				apropos.setVisible(true);
			}
		});

	}
}
