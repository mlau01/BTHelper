package bth.gui.options;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class ScheduleTableModel extends AbstractTableModel {
	
	private String[] columnNames;
	private Vector<Vector<String>> datas;

	public ScheduleTableModel(String[] p_columnName, Vector<Vector<String>> p_datas) {
		this.datas = p_datas;
		this.columnNames = p_columnName;
	}
	
	@Override
	public int getRowCount() {
		return datas.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return datas.get(rowIndex).get(columnIndex);
	}

}
