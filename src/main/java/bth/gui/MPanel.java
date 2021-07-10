package bth.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import bth.BTHelper;
import bth.gui.bt.BtGui;
import bth.gui.planning.PlanningGui;
import bth.gui.request.RequestGui;

public class MPanel extends JPanel {
	
	private final short verboseLevel = 2;
	private final MWin mWin;
	private final JLabel statusLabel;
	private final Toolbar gToolbar;
	private final BtGui btGui;
	private final PlanningGui planningGui;
	private final RequestGui requestGui;
	
	public MPanel(final MWin p_mWin) {
		
		mWin = p_mWin;
		
		//~~~~ Toolbar ~~~~
		JPanel cToolBar = new JPanel();
		cToolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		gToolbar = new Toolbar(mWin);
		cToolBar.add(gToolbar);
		
		//~~~~ Tabs ~~~~
		JTabbedPane tab = new JTabbedPane();
		btGui = new BtGui(mWin);
		tab.add("BT's", btGui);
		planningGui = new PlanningGui(mWin);
		tab.add("Planning", planningGui);
		requestGui = new RequestGui(mWin);
		tab.addTab("Request", requestGui);
	
		//~~~~ Gui ~~~~
		this.setLayout(new BorderLayout());
		this.add(cToolBar, BorderLayout.NORTH);
		this.add(tab, BorderLayout.CENTER);
		
		// ---- Status Bar ----
		JPanel statusBar = new JPanel();
		this.add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new BorderLayout());
		statusBar.setPreferredSize(new Dimension(MWin.getCurrentDimension().getSize().width, 25));
		statusLabel = new JLabel();
		statusBar.add(statusLabel, BorderLayout.WEST);
	}
	
	public void setStatusText(final String text)
	{
		if(verboseLevel > 0) System.out.println(this.getClass().getName() + " -> Call: setLeftStatusText(\"" + text + "\")");
		statusLabel.setText(text);
	}

	public final BtGui getBtGui()
	{
		return btGui;
	}
	public final PlanningGui getPlanningGui()
	{
		return planningGui;
	}
	public final Toolbar getToolbar()
	{
		return gToolbar;
	}

}
