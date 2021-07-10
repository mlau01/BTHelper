package bth.gui.bt;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import bth.core.CoreManager;
import bth.core.bt.Bt;
import bth.core.datasource.maximo.MaximoConnectionException;
import bth.gui.Fillable;
import bth.gui.MWin;

import javax.swing.JLabel;
import javax.swing.BoxLayout;


public class BtGui extends JPanel implements MouseListener, Fillable {
	
	private final MWin mWin;
	private final JTable table;
	private final JLabel lBt;
	private final JPanel cTechsBts;
	private final ButtonGroup rg;
	private final ArrayList<BtButton> btButtons;
	private BtButton btButtonSelected;
	

	public BtGui(final MWin p_mWin) {
		mWin = p_mWin;
		mWin.addFillableGui(this);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//---- BT's container ----
		JPanel cBt = new JPanel();
		cBt.setLayout(new BorderLayout());
		cBt.setMaximumSize(new Dimension(MWin.getCurrentDimension().width, 200));
		
		//---- BT's number ----
		JPanel cBtcTitle = new JPanel();
		lBt = new JLabel();
		lBt.setFont(new Font("Monospace", Font.BOLD, 24));
		cBtcTitle.add(lBt);
		
		//---- Techs Bt's ----
		cTechsBts = new JPanel(new GridLayout(2,5));
		btButtons = new ArrayList<BtButton>();
	
		rg = new ButtonGroup();
		
		cBt.add(cBtcTitle, BorderLayout.NORTH);
		cBt.add(cTechsBts, BorderLayout.CENTER);
		
		
		//
		if(mWin.getW())
		{
			JPanel w = new JPanel();
			GridLayout layout = new GridLayout(1, 6, 100, 0);
			JButton bw = new JButton("W");
			w.add(bw);
			this.add(w);
			bw.addMouseListener(new MouseListener()
			{
				public void mouseClicked(MouseEvent ev)
				{
					doW();
				}
				public void mouseEntered(MouseEvent ev) { }
				public void mouseReleased(MouseEvent ev) { }
				public void mouseExited(MouseEvent ev) { }
				public void mousePressed(MouseEvent ev) { }
			});
			JTextField wth = new JTextField("0:00");
			wth.setPreferredSize(new Dimension(50, 20));
			w.add(wth);
			
			JTextField wtc = new JTextField("");
			wtc.setPreferredSize(new Dimension(200, 20));
			w.add(wtc);
			
			JButton wtb = new JButton("P");
			w.add(wtb);
			wtb.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e) {
					if( ! wth.getText().equals("0:00") & ! wtc.getText().isEmpty() )
						doP(new String[] {wth.getText(), wtc.getText()});
				}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e){}
				public void mouseReleased(MouseEvent e) {}
				
			});
		}
		//---- Table Container ----
		JPanel cTable = new JPanel();
		cTable.setLayout(new BorderLayout());
		
		table = new JTable();
		table.setToolTipText("Click to copy");
		JScrollPane scrollpane = new JScrollPane(table);
		cTable.add(table.getTableHeader(), BorderLayout.NORTH);
		cTable.add(scrollpane, BorderLayout.CENTER);
		
		table.addMouseListener(this);

		this.add(cBt);
		this.add(cTable);
		
		
		
	}
	
	public void fillDatas()
	{
		
		mWin.setWait(true);
		final CoreManager interact = mWin.getCorma();
		
		// Tech stats / buttons
		ArrayList<Bt> allBts = null;
		
		
		final ArrayList<String> tecList = interact.technician_get_list();
		for(final String tec : tecList) {
			ArrayList<Bt> tecBts = interact.technician_get_bts(tec);
			BtButton tecButton = btButtonsContains(tec);
			if(tecButton == null){
				tecButton = new BtButton(tec, createJRadioButton(cTechsBts, rg));
				btButtons.add(tecButton);
			}
			tecButton.setBts(tecBts);
			if(tec.equals("All")) allBts = tecBts;
		}
		
		if(btButtonSelected != null) btButtonSelected.getButton().setSelected(true);
		else btButtonSelected = btButtonsContains("All");
		
		lBt.setText(".: " + allBts.size()  + " BT's :.");
		if(allBts.size() > 99) lBt.setForeground(Color.RED);
		else lBt.setForeground(Color.BLACK);
		
		setSimpleTableModel(btButtonSelected.getBts());
		mWin.setWait(false);
		
	}
	
	private final BtButton btButtonsContains(final String tec)
	{
		for(final BtButton btButton : btButtons)
		{
			if(btButton.getTec().equals(tec)) return btButton;
		}
		
		return null;
	}
	
	private final BtButton btButtonsContains(final JRadioButton button)
	{
		for(final BtButton btButton : btButtons)
		{
			if(btButton.getButton() == button) return btButton;
		}
		
		return null;
	}
	
	private final JRadioButton createJRadioButton(final JPanel container, final ButtonGroup group)
	{
		final JRadioButton rb = new JRadioButton();
		rb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ev)
			{
				final JRadioButton src = ((JRadioButton)ev.getSource()); 
				if(src.isSelected())
				{
					selectTec(src);
				}
			}
		});
		group.add(rb);
		container.add(rb);
		
		return rb;
		
	}
	
	private void setSimpleTableModel(final ArrayList<Bt> bts)
	{
		AbstractTableModel model = new AbstractTableModel(){
			public String columnNames[] = {"Numero", "Date", "Description"};
			
			public final String getColumnName(int col){ 	return this.columnNames[col]; }
			public final int getColumnCount() {				return (columnNames.length); }
			public final int getRowCount() { 				return bts.size(); }
			public final String getValueAt(int x, int y) {	
				String value = "";
				Bt bt = bts.get(x);
				if(y == 0) value = bt.getWonum();
				if(y == 1) value = bt.getDate();
				if(y == 2) value = bt.getDesc();
				
				return value;
			}
		};
		
		table.setModel(model);
	}
	
	private void setWTableModel(final ArrayList<Bt> bts)
	{
		BtTableModel model = new BtTableModel(table, bts);
		 final TableColumnModel columnModel = table.getColumnModel();
		    for (int column = 0; column < table.getColumnCount(); column++) {
		        int width = 15; // Min width
		        for (int row = 0; row < table.getRowCount(); row++) {
		            TableCellRenderer renderer = table.getCellRenderer(row, column);
		            Component comp = table.prepareRenderer(renderer, row, column);
		            width = Math.max(comp.getPreferredSize().width +1 , width);
		        }
		        if(width > 300)
		            width=300;
		        //columnModel.getColumn(column).
		    }
	}
	
	private final void selectTec(final JRadioButton rb)
	{
		final BtButton select = btButtonsContains(rb);
		btButtonSelected = select;
		if(mWin.getW()) setWTableModel(select.getBts());
		else setSimpleTableModel(select.getBts());
	}
	
	private void doW()
	{
		mWin.setWait(true);
		try {
			mWin.getCorma().w(btButtonSelected.getBts());
		} catch (MaximoConnectionException | IOException | InterruptedException e) {
			e.printStackTrace();
			mWin.showError(e.getClass().getName(), e.getMessage());
		}
		mWin.setWait(false);
		
	}
	private void doP(String[] args)
	{
		mWin.setWait(true);
		try {
			mWin.getCorma().p(args);
		} catch (MaximoConnectionException | IOException | InterruptedException e) {
			e.printStackTrace();
			mWin.showError(e.getClass().getName(), e.getMessage());
		}
		mWin.setWait(false);
	}
	
	
	//#### EVENT ####
	
	
	public void mouseClicked(MouseEvent ev)
	{
		int row = ((JTable)ev.getSource()).getSelectedRow();
		String wonum = ((String)((JTable)ev.getSource()).getValueAt(row, 0));
		StringSelection stringSelection = new StringSelection(wonum);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
	public void mouseReleased(MouseEvent ev) { }
	public void mouseEntered(MouseEvent ev) { }
	public void mouseExited(MouseEvent ev) { }
	public void mousePressed(MouseEvent ev) { }
}
