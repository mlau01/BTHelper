package bth.gui.menu;

import javax.swing.JDialog;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bth.BTHelper;

public class APropos extends JDialog {
	
	public APropos()
	{
		JPanel mainContainer = new JPanel();
		this.setContentPane(mainContainer);
		this.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		this.setSize(300, 100);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mainContainer.add(new JLabel(BTHelper.APP_NAME + " version " + BTHelper.APP_VERSION));
		mainContainer.add(new JLabel("Author: " + BTHelper.AUTHOR));
		mainContainer.add(new JLabel(BTHelper.AUTHORMAIL));
	}

}
