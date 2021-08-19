package bth.gui.options;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bth.BTHelper;
import bth.core.options.OptionsException;
import bth.gui.Fillable;
import bth.gui.GridBagHelper;
import bth.gui.MWin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;


public class OptionsGui extends JPanel implements Fillable {
	
	private JDialog dialog;
	private JScrollPane sp;
	
	private JPanel sqlConfPan;
	private JComponent[] sqlComp;
	private JRadioButton rSqlUsing;
	private JComboBox<String> bSqlProtocol;
	private JTextField fSqlHostname;
	private JTextField fSqlDatabase;
	private JCheckBox cSqlIntegrated;
	private JTextField fSqlUser;
	private JPasswordField fSqlPasswd;
	
	private JPanel fileConfPan;
	private JComponent[] fileComp;
	private JRadioButton rFileUsing;
	private JTextField fFilepath;
	private JButton bFileChoose;
	
	private JPanel maximoWebConfPan;
	private JComponent[] maximoComp;
	private JRadioButton rMaximoWebUsing;
	private JTextField fMaximoUrl;
	private JTextField fMaximoLogin;
	private JPasswordField fMaximoPassword;
	
	private JComponent[] httpComp;
	private JTextField fHttpUrl;
	private JTextField fHttpUser;
	private JPasswordField fHttpPasswd;
	
	private JComponent[] proxyComp;
	private JCheckBox cHttpUseProxy;
	private JCheckBox cHttpUseSystemProxy;
	private JTextField fHttpProxyHost;
	private JTextField fHttpProxyUser;
	private JPasswordField fHttpProxyPasswd;
	
	private JTextArea tSqlRequest;
	
	private JPanel schedulePanel;
	
	private JButton bRestore;
	private JButton bSave;
	private JButton bClose;
	
	private final MWin mWin;
	
	public OptionsGui(final MWin p_mWin, final JDialog p_dialog)
	{
		mWin = p_mWin;
		dialog = p_dialog;
		
		loadWidgets();
		fillDatas();
		loadEvents();
	}
	
	private void loadWidgets()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//~~~~ Tab Container ~~~~
		JTabbedPane tabPan = new JTabbedPane();
		this.add(tabPan);
		
		//~~~~�Planning Pane ~~~~
		JPanel planningPan = new JPanel();
		tabPan.add("Planning", planningPan);
		
		GridBagLayout planningGbl = new GridBagLayout();
		GridBagHelper planningGbh = new GridBagHelper(planningPan, planningGbl);
		
		//~~~~ General > HTTP Panel ~~~~
		JPanel httpConfPan = new JPanel();
		{
			httpConfPan.setBorder(BorderFactory.createTitledBorder("HTTP Config"));
			httpConfPan.setPreferredSize(new Dimension(700, 150));
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagHelper gbh = new GridBagHelper(httpConfPan, gbl);
			
			httpComp = new JComponent[]{
					new JLabel("Planning URL : "),
					new JTextField(50),
					new JLabel("User : "),
					new JTextField(15),
					new JLabel("Password : "),
					new JPasswordField(15)
			};
			
			
			gbh.add(httpComp[0], 0, 0, 1);
			gbh.add(httpComp[1], 0, 1, 1);
			fHttpUrl = (JTextField)httpComp[1];
			
			gbh.add(httpComp[2], 1, 0, 1);
			gbh.add(httpComp[3], 1, 1, 1);
			fHttpUser = (JTextField)httpComp[3];
			
			gbh.add(httpComp[4], 2, 0, 1);
			gbh.add(httpComp[5] , 2, 1, 1);
			fHttpPasswd = (JPasswordField)httpComp[5];
		}
		
		JPanel proxyPan = new JPanel();
		{
			proxyPan.setBorder(BorderFactory.createTitledBorder("Proxy Config"));
			proxyPan.setPreferredSize(new Dimension(700, 150));
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagHelper gbh = new GridBagHelper(proxyPan, gbl);
			
			proxyComp = new JComponent[] {
					new JCheckBox("Use system proxy settings"),
					new JLabel("Proxy Host : "),
					new JTextField(50),
					new JLabel("Proxy user : "),
					new JTextField(15),
					new JLabel("Proxy password : "),
					new JPasswordField(15)
			};
			
			cHttpUseProxy = new JCheckBox("Use proxy");
			gbh.add(cHttpUseProxy, 0, 0, 1);
			
			gbh.add(proxyComp[0], 0, 1, 1);
			cHttpUseSystemProxy = (JCheckBox)proxyComp[0];
			
			gbh.add(proxyComp[1], 1, 0, 1);
			gbh.add(proxyComp[2], 1, 1, 1);
			fHttpProxyHost = (JTextField)proxyComp[2];
			
			gbh.add(proxyComp[3], 2, 0, 1);
			gbh.add(proxyComp[4], 2, 1, 1);
			fHttpProxyUser = (JTextField)proxyComp[4];
			
			gbh.add(proxyComp[5], 3, 0, 1);
			gbh.add(proxyComp[6], 3, 1, 1);
			fHttpProxyPasswd = (JPasswordField)proxyComp[6];
		}
		
		planningGbh.add(httpConfPan, 0, 0, 1, GridBagConstraints.EAST, null);
		planningGbh.add(proxyPan, 1, 0, 1, GridBagConstraints.EAST, null);
		
		
		//#### BT Pane ####
		JPanel btPan = new JPanel();
		tabPan.add("BT", btPan);
		
		GridBagLayout btGbl = new GridBagLayout();
		GridBagHelper btGbh = new GridBagHelper(btPan, btGbl);
		
		ButtonGroup bg = new ButtonGroup();
		
		//~~~~ General > SQL Panel ~~~~
		rSqlUsing = new JRadioButton("Use SQL Database");
		bg.add(rSqlUsing);
		sqlConfPan = new JPanel();
		{
			sqlConfPan.setBorder(BorderFactory.createTitledBorder("SQL Config"));
			sqlConfPan.setPreferredSize(new Dimension(700, 200));
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagHelper gbh = new GridBagHelper(sqlConfPan, gbl);
			
			sqlComp = new JComponent[] {
					new JLabel("Protocol : "),
					new JComboBox<String>(new String[] {BTHelper.mysql, BTHelper.sqlserver}),
					new JLabel("Hostname : "),
					new JTextField(50),
					new JLabel("Database : "),
					new JTextField(15),
					new JCheckBox("Use Windows credentials"),
					new JLabel("User : "),
					new JTextField(15),
					new JLabel("Password : "),
					new JPasswordField(15)	
			};
			
			gbh.add(sqlComp[0], 1, 0, 1, 0, new Insets(0, 0, 15, 0));
			gbh.add(sqlComp[1] , 1, 1, 1, 0, new Insets(0, 0, 15, 0));
			bSqlProtocol = (JComboBox<String>) sqlComp[1];
			
			gbh.add(sqlComp[2], 2, 0, 1);
			gbh.add(sqlComp[3], 2, 1, 1);
			fSqlHostname = (JTextField)sqlComp[3];
			
			gbh.add(sqlComp[4], 3, 0, 1);
			gbh.add(sqlComp[5], 3, 1, 1);
			fSqlDatabase = (JTextField)sqlComp[5];
			
			gbh.add(sqlComp[6], 4, 0, 2, 0, new Insets(10, 0, 0, 0));
			cSqlIntegrated = (JCheckBox)sqlComp[6];
			
			gbh.add(sqlComp[7], 5, 0, 1);
			gbh.add(sqlComp[8], 5, 1, 1);
			fSqlUser = (JTextField)sqlComp[8];
			
			gbh.add(sqlComp[9], 6, 0, 1);
			gbh.add(sqlComp[10], 6, 1, 1);
			fSqlPasswd = (JPasswordField)sqlComp[10];	
		}
		
		//---- File Database ----
		rFileUsing = new JRadioButton("Use File Database");
		bg.add(rFileUsing);
		fileConfPan = new JPanel();
		{
			fileConfPan.setBorder(BorderFactory.createTitledBorder("File Config"));
			fileConfPan.setPreferredSize(new Dimension(700, 50));
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagHelper gbh = new GridBagHelper(fileConfPan, gbl);
			
			fileComp = new JComponent[] {
					new JLabel("Filepath : "),
					new JTextField(50),
					new JButton("Browse")
			};
			
			
			gbh.add(fileComp[0], 0, 0, 1);
			gbh.add(fileComp[1], 0, 1, 1);
			fFilepath = (JTextField)fileComp[1];
			gbh.add(fileComp[2], 0, 2, 1);
			bFileChoose = (JButton)fileComp[2];
		}
		
		//---- MAXIMO Web interface ----
		rMaximoWebUsing = new JRadioButton("Use Maximo Web interface");
		bg.add(rMaximoWebUsing);
		maximoWebConfPan = new JPanel();
		{
			maximoComp = new JComponent[] {
					new JLabel("URL : "),
					new JTextField(50),
					new JLabel("Login : "),
					new JTextField(50),
					new JLabel("Password : "),
					new JPasswordField(15)
			};
			maximoWebConfPan.setBorder(BorderFactory.createTitledBorder("Maximo Web interface"));
			maximoWebConfPan.setPreferredSize(new Dimension(700, 100));
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagHelper gbh = new GridBagHelper(maximoWebConfPan, gbl);
			
			gbh.add(maximoComp[0], 0, 0, 1);
			gbh.add(maximoComp[1], 0, 1, 1);
			fMaximoUrl = (JTextField)maximoComp[1];
			
			gbh.add(maximoComp[2], 1, 0, 1);
			gbh.add(maximoComp[3], 1, 1, 1);
			fMaximoLogin = (JTextField)maximoComp[3];
			
			gbh.add(maximoComp[4], 2, 0, 1);
			gbh.add(maximoComp[5], 2, 1, 1);
			fMaximoPassword = (JPasswordField)maximoComp[5];
		}
		
		//---- attach panes to the panel
		btGbh.add(rSqlUsing, 0, 0, 0);
		btGbh.add(sqlConfPan,1, 0, 1, GridBagConstraints.EAST, null);
		btGbh.add(rFileUsing, 2, 0, 0);
		btGbh.add(fileConfPan, 3, 0, 1, GridBagConstraints.EAST, null);
		btGbh.add(rMaximoWebUsing, 4, 0, 0);
		btGbh.add(maximoWebConfPan, 5, 0, 1, GridBagConstraints.EAST, null);
		
		// SchedulePan
		schedulePanel = new SchedulePanel();
		((SchedulePanel)schedulePanel).loadWidgets();
		tabPan.add("Horaire", schedulePanel);
		//#### Request Pane ####
		JPanel reqPan = new JPanel();
		BoxLayout reqLyt = new BoxLayout(reqPan, BoxLayout.LINE_AXIS);
		reqPan.setLayout(reqLyt);
		
		tSqlRequest = new JTextArea();
		sp = new JScrollPane(tSqlRequest);
		reqPan.add(sp);

		tabPan.add("SQL Request", reqPan);
		
		
		//~~~~ Button Panel�~~~~
		
		JPanel buttonPan = new JPanel();
		buttonPan.setMaximumSize(new Dimension(800, 80));
		bRestore = new JButton("Restore");
		bRestore.setToolTipText("Restore default's configurations");
		bRestore.setPreferredSize(new Dimension(100, 30));
		bSave = new JButton("Save");
		bSave.setPreferredSize(new Dimension(100, 30));
		bClose = new JButton("Close");
		bClose.setPreferredSize(new Dimension(100, 30));
		
		buttonPan.add(bRestore);
		buttonPan.add(bSave);
		buttonPan.add(bClose);
		
		this.add(buttonPan);
		
	}
	
	private void loadEvents()
	{
		cSqlIntegrated.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ev)
			{
				final boolean selection = ((JCheckBox)ev.getSource()).isSelected();
				action_SqlUseCredentialsED(selection);
			}
		});
		
		cHttpUseProxy.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = ((JCheckBox)e.getSource()).isSelected();
				action_setEnableHttpProxy(selected);
			}
		});
		

		cHttpUseSystemProxy.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = ((JCheckBox)e.getSource()).isSelected();
				action_setEnableUseSystemProxy(selected);
			}
		});
		
		rFileUsing.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = ((JRadioButton)e.getSource()).isSelected();
				action_setEnableFile(selected);
			}
		});
		bFileChoose.addActionListener(e -> {
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(dialog);
			if(returnVal == JFileChooser.APPROVE_OPTION)
				fFilepath.setText(fc.getSelectedFile().getAbsolutePath());
		});
		
		rSqlUsing.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = ((JRadioButton)e.getSource()).isSelected();
				action_setEnableSql(selected);
			}
		});
		
		rMaximoWebUsing.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = ((JRadioButton)e.getSource()).isSelected();
				action_setEnableMaximoWeb(selected);
			}
		});
		
		bRestore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//restoreDefaults();				
			}
		});
		
		bSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		bClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});	
	}
	
	private void action_setEnableHttpProxy(boolean selected)
	{
		for(JComponent comp : proxyComp)
		{
			comp.setEnabled(selected);
		}
	}
	
	private void action_setEnableUseSystemProxy(boolean select)
	{
		
	}
	private void action_setEnableSql(boolean select)
	{
		for(JComponent comp : sqlComp)
		{
			comp.setEnabled(select);
		}
	}
	private void action_setEnableFile(boolean select)
	{
		for(JComponent comp : fileComp)
		{
			comp.setEnabled(select);
		}
	}
	private void action_setEnableMaximoWeb(boolean select)
	{
		maximoWebConfPan.setEnabled(select);
		for(JComponent comps : maximoComp)
		{
			comps.setEnabled(select);
		}
	}
	
	private void action_SqlUseCredentialsED(final boolean selection)
	{
		sqlComp[7].setEnabled(!selection);
		sqlComp[8].setEnabled(!selection);
		sqlComp[9].setEnabled(!selection);
		sqlComp[10].setEnabled(!selection);
	}
	private void fillField(final Properties p)
	{
		rSqlUsing.setSelected(Boolean.parseBoolean(p.getProperty(BTHelper.SqlUsed)));
		action_setEnableSql(Boolean.parseBoolean(p.getProperty(BTHelper.SqlUsed)));
		bSqlProtocol.setSelectedItem(p.getProperty(BTHelper.SqlProtocol));
		fSqlHostname.setText(p.getProperty(BTHelper.SqlHostname));
		fSqlDatabase.setText(p.getProperty(BTHelper.SqlDatabase));
		fSqlUser.setText(p.getProperty(BTHelper.SqlUser));
		fSqlPasswd.setText(p.getProperty(BTHelper.SqlPasswd));
		
		cSqlIntegrated.setSelected(Boolean.parseBoolean(p.getProperty(BTHelper.SqlUseCredentials)));
		action_SqlUseCredentialsED(Boolean.parseBoolean(p.getProperty(BTHelper.SqlUseCredentials)));
		
		rFileUsing.setSelected(Boolean.parseBoolean(p.getProperty(BTHelper.FileUsed)));
		action_setEnableFile(Boolean.parseBoolean(p.getProperty(BTHelper.FileUsed)));
		fFilepath.setText(p.getProperty(BTHelper.Filepath));
		
		rMaximoWebUsing.setSelected(Boolean.parseBoolean(p.getProperty(BTHelper.MaximoUsed)));
		action_setEnableMaximoWeb(Boolean.parseBoolean(p.getProperty(BTHelper.MaximoUsed)));
		fMaximoUrl.setText(p.getProperty(BTHelper.MaximoUrl));
		fMaximoLogin.setText(p.getProperty(BTHelper.MaximoLogin));
		fMaximoPassword.setText(p.getProperty(BTHelper.MaximoPassword));
		
		fHttpUrl.setText(p.getProperty(BTHelper.HttpUrl));
		fHttpUser.setText(p.getProperty(BTHelper.HttpUser));
		fHttpPasswd.setText(p.getProperty(BTHelper.HttpPasswd));
		
		cHttpUseProxy.setSelected(Boolean.parseBoolean(p.getProperty(BTHelper.HttpUseProxy)));
		action_setEnableHttpProxy(Boolean.parseBoolean(p.getProperty(BTHelper.HttpUseProxy)));
		
		cHttpUseSystemProxy.setSelected(Boolean.parseBoolean(p.getProperty(BTHelper.HttpUseSystemProxy)));
		fHttpProxyHost.setText(p.getProperty(BTHelper.HttpProxyHost));
		fHttpProxyUser.setText(p.getProperty(BTHelper.HttpProxyUser));
		fHttpProxyPasswd.setText(p.getProperty(BTHelper.HttpProxyPassword));
		
		tSqlRequest.setText(p.getProperty(BTHelper.SqlRequest));
	}
	
	private void save()
	{
		Properties p = new Properties();
		p.setProperty(BTHelper.SqlUsed, String.valueOf(rSqlUsing.isSelected()));
		p.setProperty(BTHelper.SqlProtocol, (String)bSqlProtocol.getSelectedItem());
		p.setProperty(BTHelper.SqlHostname, fSqlHostname.getText());
		p.setProperty(BTHelper.SqlDatabase, fSqlDatabase.getText());
		p.setProperty(BTHelper.SqlUser, fSqlUser.getText());
		p.setProperty(BTHelper.SqlPasswd, String.valueOf(fSqlPasswd.getPassword()));
		
		p.setProperty(BTHelper.SqlUseCredentials, String.valueOf(cSqlIntegrated.isSelected()));
		
		p.setProperty(BTHelper.FileUsed, String.valueOf(rFileUsing.isSelected()));
		p.setProperty(BTHelper.Filepath, fFilepath.getText());
		
		p.setProperty(BTHelper.MaximoUsed, String.valueOf(rMaximoWebUsing.isSelected()));
		p.setProperty(BTHelper.MaximoUrl, fMaximoUrl.getText());
		p.setProperty(BTHelper.MaximoLogin, fMaximoLogin.getText());
		p.setProperty(BTHelper.MaximoPassword, String.valueOf(fMaximoPassword.getPassword()));
		
		p.setProperty(BTHelper.HttpUrl, fHttpUrl.getText());
		p.setProperty(BTHelper.HttpUser, fHttpUser.getText());
		p.setProperty(BTHelper.HttpPasswd, String.valueOf(fHttpPasswd.getPassword()));
		
		p.setProperty(BTHelper.HttpUseProxy, String.valueOf(cHttpUseProxy.isSelected()));
		p.setProperty(BTHelper.HttpUseSystemProxy, String.valueOf(cHttpUseSystemProxy.isSelected()));
		
		p.setProperty(BTHelper.HttpProxyHost, fHttpProxyHost.getText());
		p.setProperty(BTHelper.HttpProxyUser, fHttpProxyUser.getText());
		p.setProperty(BTHelper.HttpProxyPassword, String.valueOf(fHttpProxyPasswd.getPassword()));
		
		p.setProperty(BTHelper.SqlRequest, tSqlRequest.getText());
		
		try {
			mWin.getOptionManager().setProperties(p);
		} catch (OptionsException e) {
			e.printStackTrace();
			mWin.showError(e.getClass().getName(), e.getMessage());
		}
		
	}
	
	public void fillDatas()
	{
		fillField(mWin.getOptionManager().getCurrentProperties());
	}

}
