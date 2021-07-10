package bth.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JToolBar;

import bth.BTHelper;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

public class Toolbar extends JToolBar {
	
	private MWin mWin;
	private JTextField filepath;
	
	public Toolbar(final MWin p_mWin) {
		mWin = p_mWin;
		this.setFloatable(false);
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		URL refreshIcon = Toolbar.class.getResource(BTHelper.iconsPath + "Refresh-icon.png");
		JButton bRefresh = new JButton(new ImageIcon(refreshIcon));
		bRefresh.setToolTipText("Refresh (F5)");
		
		this.add(bRefresh);
		
		if(mWin.getOptionManager().getCurrentProperties().getProperty(BTHelper.FileUsed).equals("true"))
		{
			filepath = new JTextField(50);
			filepath.setText(mWin.getOptionManager().getCurrentProperties().getProperty(BTHelper.Filepath) + "\\");
			filepath.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
			
				}
				
				@Override
				public void keyReleased(KeyEvent arg0) {
				}
				
				@Override
				public void keyPressed(KeyEvent arg0) {
					if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
						mWin.reload();
				}
			});
			this.add(filepath);
		}

		//#~~~ Event ~~~#
		
		bRefresh.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent ev)
			{
				mWin.reload();
			}
			public void mouseEntered(MouseEvent ev) { }
			public void mouseReleased(MouseEvent ev) { }
			public void mouseExited(MouseEvent ev) { }
			public void mousePressed(MouseEvent ev) { }
		});
	
	}
	
	public final String getFilepath()
	{
		if(filepath == null)
			return "";
			
		return filepath.getText();
	}

}
