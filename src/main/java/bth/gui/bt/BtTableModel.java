package bth.gui.bt;

import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import bth.core.bt.Bt;
import bth.core.datasource.maximo.Maxi;
import bth.core.datasource.maximo.Maxi.ISSUE;

public class BtTableModel extends AbstractTableModel{
	
	private ArrayList<Bt> bts;
	private final JTable table;
	
	public static final int C_WO = 0;
	public static final int C_DATE = 1;
	public static final int C_DESC = 2;
	public static final int C_NEWDESC = 3;
	public static final int C_TRAVEL = 4;
	public static final int C_DURATION = 5;
	public static final int C_ASSETNUM = 6;
	public static final int C_CODE = 7;
	public static final int C_ISSUE = 8;
	public static final int C_COMMENT = 9;
	public static final int C_W = 10;
	
	public BtTableModel(JTable p_table, ArrayList<Bt> p_bts)
	{
		super();
		table = p_table;
		bts = p_bts;
		
		table.setModel(this);
		
		TableColumn col = table.getColumnModel().getColumn(C_ISSUE);
		JComboBox<ISSUE> combo = new JComboBox<ISSUE>();
		ISSUE[] list = Maxi.ISSUE.values();
		for(ISSUE is : list)
		{
			combo.addItem(is);
		}
		col.setCellEditor(new DefaultCellEditor(combo));
	}
	
	public String columnNames[] = {"Numero", "Date", "Description", "NewDesc", "TravelTime", "Duration","AssetNum","Code", "Issue", "Comment", "W"};
	
	public final String getColumnName(int col){ 	return this.columnNames[col]; }
	public final int getColumnCount() {				return (columnNames.length); }
	public final int getRowCount() { 				return bts.size(); }
	
	public final Object getValueAt(int row, int col) {
		Object value = null;
		Bt bt = bts.get(row);
		
		if(col == C_WO) value = bt.getWonum();
		if(col == C_DATE) value = bt.getDate();
		if(col == C_DESC) value = bt.getDesc();
		if(col == C_NEWDESC) value = bt.getNewDesc();
		if(col == C_TRAVEL) value = bt.getTravelTime();
		if(col == C_DURATION) value = bt.getDuration();
		if(col == C_ASSETNUM) value = bt.getGear();
		if(col == C_CODE) value = bt.getCode();
		if(col == C_ISSUE) value = bt.getIssue();
		if(col == C_COMMENT) value = bt.getComment();
		if(col == C_W) value = bt.isW();
		
		return value;
	}
	public boolean isCellEditable(int row, int col)
	{
		if(col == C_NEWDESC) return true;
		else if(col == C_TRAVEL)  return true;
		else if(col == C_DURATION)  return true;
		else if(col == C_ISSUE)  return true;
		else if(col == C_COMMENT)  return true;
		else if(col == C_W)  return true;
		else return false;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		Bt bt = bts.get(row);
		if(col == C_NEWDESC) bt.setNewDesc(String.valueOf(value));
		if(col == C_TRAVEL) bt.setTravelTime(String.valueOf(value));
		if(col == C_DURATION) bt.setDuration(String.valueOf(value));
		if(col == C_ISSUE) bt.setIssue(String.valueOf(value));
		if(col == C_COMMENT) bt.setComment(String.valueOf(value));
		if(col == C_W) bt.setW((boolean)value);
		fireTableRowsUpdated(row, col);
	}
	
	 /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
}
