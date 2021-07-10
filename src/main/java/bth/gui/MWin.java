package bth.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import bth.BTHelper;
import bth.Observer;
import bth.core.CoreManager;
import bth.core.bt.BTException;
import bth.core.datasource.DatasourceException;
import bth.core.options.OptionsException;
import bth.core.options.OptionsManager;
import bth.core.request.RequestException;
import bth.gui.menu.Menu;

public class MWin extends JFrame implements Observer {
	
	public final static Dimension currentDimension = new Dimension(1024, 600);
	private MPanel gui;
	private ArrayList<Fillable> fillableGui;
	private OptionsManager optMan;
	private CoreManager corma = null;
	private boolean w = false;
	private boolean verbose = false;

	public MWin(String[] args)
	{
		if(args.length > 0) argsManager(args);
		
		try {
			optMan = new OptionsManager();
		} catch (OptionsException e) {
			e.printStackTrace();
			showError(e.getClass().getName(), e.getMessage());
			return;
		}
		
		gui = loadGui();
		corma = initCoreManager(optMan.getCurrentProperties());
		corma.addObserver(this);
		
		if(corma != null && 
				optMan.getCurrentProperties().getProperty(BTHelper.FileUsed).equals("false")) 
			reload();
	}
	
	private final CoreManager initCoreManager(final Properties properties)
	{
		setWait(true);
		CoreManager init = null;
	
			try {
				init = new CoreManager(optMan.getCurrentProperties(), verbose);
			} catch (DatasourceException | RequestException e) {
				e.printStackTrace();
				showError(e.getClass().getName(), e.getMessage());
				gui.setStatusText("NOT Connected...");
			} finally {
				setWait(false);
			}
		
		
		return init;
	}
	
	private final MPanel loadGui()
	{
		this.setTitle(BTHelper.APP_NAME + " " + BTHelper.APP_VERSION);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		URL imgUrl = getClass().getResource(BTHelper.iconsPath + "BTHelper.png");
		ImageIcon imgIcon = new ImageIcon(imgUrl);
		this.setIconImage(imgIcon.getImage());
		this.setJMenuBar(new Menu(this));
		this.setPreferredSize(currentDimension);
		
		fillableGui = new ArrayList<Fillable>();
		
		final MPanel gui = new MPanel(this);
		this.setContentPane(gui);
		loadEvents(gui);
	
		
		this.pack();
		this.setVisible(true);
		
		return gui;
	}
	
	public void reload()
	{
		Runnable th1 = new Runnable()
		{
			@Override
			public void run() {
				if(corma == null) initCoreManager(optMan.getCurrentProperties());
				else {
					setWait(true);
					clear();
					loadBts(gui.getToolbar().getFilepath());
					fillGuis();
					setWait(false);
				}
				
			}
			
		};
		th1.run();

	}
	
	private final void clear()
	{
		gui.getPlanningGui().clear();
		gui.setStatusText("");
	}
	

	public void loadBts(final String filepath) {
		try {
			corma.bt_assign(filepath);
		} catch (BTException e)
		{
			e.printStackTrace();
			showError(e.getClass().getName(), e.getMessage());
		}
	}
	
	
	public void showError(final String title, final String msg)
	{
		JOptionPane.showMessageDialog(this, msg,title, JOptionPane.ERROR_MESSAGE);
		
	}
	
	public static Dimension getCurrentDimension()
	{
		return currentDimension;
	}
	
	public final CoreManager getCorma()
	{
		return corma;
	}
	
	public void addFillableGui(final Fillable fgui)
	{
		fillableGui.add(fgui);
	}
	
	private void fillGuis()
	{
		for(final Fillable fgui : fillableGui)
		{
			fgui.fillDatas();
		}
	}
	
	public static void main(String[] args)
	{
		new MWin(args);
	}
	
	public final void setWait(final boolean bool)
	{
		int cursor;
		if(bool) cursor = Cursor.WAIT_CURSOR;
		else cursor = Cursor.DEFAULT_CURSOR;
		
		this.getGlassPane().setCursor(Cursor.getPredefinedCursor(cursor));
		this.getGlassPane().setVisible(bool);
	}
	
	
	private void loadEvents(final MPanel p_gui)
	{
		p_gui.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "reloadAll");
		p_gui.getActionMap().put("reloadAll", new AbstractAction(){
		public void actionPerformed(ActionEvent ev)
		{
			reload();
		} });
	
	}
	
	private void argsManager(String[] args){
		for(String arg : args){
			if(arg.equals("-W")) w = true;
			if(arg.equals("-v")) verbose = true;
		}
	}
	
	public final OptionsManager getOptionManager()
	{
		return optMan;
	}

	@Override
	public void notify(String value) {
		gui.setStatusText(value);
	}
	
	public final boolean getW()
	{
		return w;
	}

}
