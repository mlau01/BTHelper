package bth.gui.planning;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import bth.core.MONTH;
import bth.core.exception.HttpConnectionException;
import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningException;
import bth.core.options.OptionException;
import bth.core.planning.Planning;
import bth.core.planning.PlanningService;
import bth.gui.MWin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlanningGui extends JPanel implements ActionListener{
	
	private JTable table = null;
	private JLabel lMonth = null;
	final JLabel lastModified;
	final JLabel info;
	private final MWin mWin;
	private final PlanningService planningService;
	
	public PlanningGui(final MWin p_mWin) {
		
		mWin = p_mWin;
		planningService = mWin.getCorma().getPlanningService();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//~~~~ Button List ~~~~
		JPanel cButton = new JPanel();
		cButton.setMaximumSize(new Dimension(MWin.getCurrentDimension().width, 60));
		for(int i = 0; i < MONTH.values().length; i++)
		{
			JButton button = new JButton(MONTH.values()[i].toString());
			button.setFont(new Font(button.getFont().getFontName(), Font.BOLD, 10));
			button.addActionListener(this);
			cButton.add(button);
		}
		this.add(cButton);

		//~~~~ Label Month ~~~~
		lMonth = new JLabel();
		lMonth.setFont(new Font("Monospace", Font.BOLD, 24));
		this.add(lMonth);

		//~~~~ Table ~~~~
		this.add(Box.createRigidArea(new Dimension(0, 20)));
		table = new JTable();
		this.add(table);
		table.setVisible(false);
		
		// ---- Last modified date ----
		this.add(Box.createRigidArea(new Dimension(0, 20)));
		lastModified = new JLabel();
		this.add(lastModified);
		lastModified.setVisible(false);
		
		// ---- INFO ----
		this.add(Box.createRigidArea(new Dimension(0, 20)));
		info = new JLabel();
		this.add(info);
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		final String sMonth = ((JButton)ev.getSource()).getText();
		
		try {
			Planning planning = planningService.get(MONTH.valueOf(sMonth));
			lMonth.setText(sMonth);
			table.setModel(getTableModel(planning.getArray()));
			table.setVisible(true);
			String lastModifiedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(planning.getLastModified());
			lastModified.setText("Edition: " + lastModifiedDate);
			lastModified.setVisible(true);
			if(planning.isLocalMode())
			{
				info.setVisible(true);
				info.setForeground(Color.RED);
				info.setText("Local Mode");
			}
			
		} catch (OptionException e) {
			mWin.showError("Http error", e.getMessage());
		} catch (PlanningException e)
		{
			table.setVisible(false);
			lastModified.setText("");
			info.setVisible(true);
			info.setText("No data for this planning...");
		} catch (PlanningDeserializeException e) {
			table.setVisible(false);
			lastModified.setText("");
			info.setVisible(true);
			info.setText("No backup found for this planning...");
			mWin.showError(e.getClass().getName(), "Cannot get this planning, no connection, no backup...: " + e.getMessage());
			e.printStackTrace();
		}
		setGraphicPreference();
	}
	
	private final  AbstractTableModel getTableModel(final ArrayList<ArrayList<String>> plan)
	{
		int maxCol = plan.get(0).size();
		int  maxRow = plan.size();
		return (new AbstractTableModel(){
			public int getRowCount() 				{ return (maxRow); }
			public int getColumnCount() 			{ return (maxCol); }
			public String getValueAt(int c, int r)	{ return plan.get(c).get(r); }

		});
	}
	
	private void setGraphicPreference()
	{
		TableColumnModel columnModel = table.getColumnModel();		
		
		//Force size of the second column
		columnModel.getColumn(1).setPreferredWidth(400);	
		
		//Browse the table to search which column represents weekend
		TableModel tableModel = table.getModel();
		
		int x = tableModel.getColumnCount();
		int y = tableModel.getRowCount();
		
		ArrayList<Integer> weekCol = new ArrayList<Integer>();
		for(int i = 0; i < x; i++)
		{
			String val = (String)table.getValueAt(0, i); 
			if(val.equals("S") || val.equals("D"))
			{
				weekCol.add(i);
			}
		}
		
		//Set default renderer for applying color
		table.setDefaultRenderer(Object.class, new PlanningCellRenderer(weekCol));
	}
	
	public final void clear()
	{
		table.setVisible(false);
		lastModified.setText("");
		lastModified.setVisible(false);
		info.setText("");
		info.setVisible(false);
	}
}
