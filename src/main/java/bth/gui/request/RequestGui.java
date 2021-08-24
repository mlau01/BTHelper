package bth.gui.request;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import bth.core.datasource.DatasourceException;
import bth.core.exception.RequestException;
import bth.core.request.RequestService;
import bth.gui.Fillable;
import bth.gui.MWin;

public class RequestGui extends JPanel implements Fillable {

	private final MWin mWin;
	
	private JTextArea queryArea;
	private JTextField queryName;
	private JList<String> queryList;
	private DefaultListModel<String> queryListModel = new DefaultListModel<String>();
	private JTable results;
	private JLabel stats;
	
	private JButton bNew;
	private JButton bDelete;
	
	private JButton bExecute;
	private JButton bEdit;
	
	private Color myGrey = new Color(225, 225, 225);
	private String lastSelectedItem = "";
	
	private RequestService requestService;
	
	public RequestGui(final MWin p_mWin)
	{
		mWin = p_mWin; 
		requestService = mWin.getCorma().getRequestService();
		createWidgets();
		createEvents();
		mWin.addFillableGui(this);
		
	}
	
	public void fillDatas()
	{
		queryListModel.clear();
		for(final String s : requestService.getQueriesTitle())
		{
			queryListModel.addElement(s);
		}
	}
	
	private void createWidgets()
	{
		this.setLayout(new BorderLayout());
		
		// ---- LEFT COL ----
		final JPanel leftCol = new JPanel(new BorderLayout());
		{	
			final JPanel buttonPanel = new JPanel(new GridLayout());
			{
				bNew = new JButton("New");
				buttonPanel.add(bNew);
				bDelete = new JButton("Delete");
				buttonPanel.add(bDelete);
			}
			leftCol.add(buttonPanel, BorderLayout.NORTH);
			
			queryList = new JList<String>();
			{
				queryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				queryList.setPreferredSize(new Dimension(100, 350));
				queryList.setModel(queryListModel);
				
			}
			leftCol.add(new JScrollPane(queryList));
		}
		this.add(leftCol, BorderLayout.WEST);
		
		// ---- CENTER COL ----
		final JPanel centerCol = new JPanel(new BorderLayout());
		this.add(centerCol, BorderLayout.CENTER);
		{
			
			final JPanel northPanel = new JPanel(new BorderLayout());
			centerCol.add(northPanel, BorderLayout.NORTH);
			{
				queryName = new JTextField();
				queryName.setBorder(BorderFactory.createTitledBorder("Query name"));
				queryName.setBackground(myGrey);
				queryName.setEditable(false);
				northPanel.add(queryName, BorderLayout.NORTH);
				
				queryArea = new JTextArea("Select or create a new query");
				queryArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				queryArea.setPreferredSize(new Dimension(500, 100));
				queryArea.setBackground(myGrey);
				queryArea.setEditable(false);
				northPanel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
				
				final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING)); 
				{
					final JPanel buttonPanel2 = new JPanel(new GridLayout());
					{
						bExecute = new JButton("Execute");
						buttonPanel2.add(bExecute);
						bEdit = new JButton("Edit");
						buttonPanel2.add(bEdit);
					}
					buttonPanel.add(buttonPanel2);
					
					
				}

				northPanel.add(buttonPanel, BorderLayout.SOUTH);
				
			}
	
			results = new JTable();
			final JScrollPane scroll = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			centerCol.add(scroll, BorderLayout.CENTER);
			{
				results.setBackground(myGrey);
				results.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				results.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
			
			final JPanel status = new JPanel();
			centerCol.add(status, BorderLayout.SOUTH);
			{
				status.setLayout(new FlowLayout(FlowLayout.LEADING));
				{
					stats = new JLabel();
					status.add(stats);
				}
			}
		}
	}

	private void createEvents()
	{
		queryList.addListSelectionListener(e -> {
			JList<String> source = (JList<String>)e.getSource();
			String selectedValue = source.getSelectedValue();
			if(lastSelectedItem.equals(selectedValue) || selectedValue == null) return;
			
			eventAction_setEditable(false);
			lastSelectedItem = selectedValue;
			queryName.setText(selectedValue);
			queryArea.setText(requestService.getQueryValue(selectedValue));
		});
		
		bExecute.addActionListener(e -> {
			
			try {
				eventAction_fillResult(requestService.execQuery(queryArea.getText()));
			} catch (SQLException | DatasourceException e1) {
				mWin.showError(e1.getClass().getName(), e1.getMessage());
				e1.printStackTrace();
			}

		});
		
		bEdit.addActionListener(e -> {
			if(bEdit.getText().equals("Edit"))
			{
				eventAction_setEditable(true);
				bEdit.setText("Save");
			}
			else if(bEdit.getText().equals("Save"))
			{
				eventAction_setEditable(false);
				if( ! bNew.isEnabled()) bNew.setEnabled(true);
				
				if(queryName.getText().isEmpty() || queryArea.getText().isEmpty()) return;
				
			
				try {
					requestService.writeQuery(queryName.getText(), queryArea.getText());
				} catch (RequestException e1) {
					e1.printStackTrace();
					mWin.showError(e1.getClass().getName(), e1.getMessage());
				} finally {
					queryListModel.clear();
					fillDatas();
				}
			}
		});
		bDelete.addActionListener(e -> {
			if(lastSelectedItem.isEmpty()) return;
			
			int res = JOptionPane.showConfirmDialog(mWin, "Supprimer: " + lastSelectedItem + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
			
			if(res == 0) try {
					requestService.delQuery(lastSelectedItem);
				} catch (RequestException e1) {
					e1.printStackTrace();
					mWin.showError(e1.getClass().getName(), e1.getMessage());
				} finally {
					queryListModel.clear();
					fillDatas();
					eventAction_clearFields();
				}
		});
		
		bNew.addActionListener(e -> {
			eventAction_setEditable(true);
			eventAction_clearFields();
			
			queryName.requestFocus();
		});
	}
	
	private final void eventAction_fillResult(final ResultSet rs) throws SQLException
	{
		final int colsCount = rs.getMetaData().getColumnCount();
		final String cols[] = new String[colsCount];
		for(int i = 1; i <= colsCount; i++)
		{
			cols[i - 1] = rs.getMetaData().getColumnLabel(i);
		}
		final ArrayList<String[]> content = new ArrayList<String[]>();
		while(rs.next())
		{
			String[] row = new String[colsCount];
			for(int i = 1; i <= colsCount; i++)
			{
				row[i-1] = rs.getString(i);
			}
			content.add(row);
		}
		
		final AbstractTableModel tmodel = new AbstractTableModel(){
			public String columnNames[] = cols;
			
			public final String getColumnName(int col){ 	return this.columnNames[col]; }
			public final int getColumnCount() {				return colsCount; }
			public final int getRowCount() { 				return content.size(); }
			public final String getValueAt(int x, int y) {	return content.get(x)[y]; }
		};
	
		results.setModel(tmodel);
		stats.setText("Results: " + content.size());
	}
	
	private final void eventAction_clearFields()
	{
		queryName.setText("");
		queryArea.setText("");
	}
	
	private final void eventAction_setEditable(final boolean bool)
	{
		if(bool)
		{
			queryName.setBackground(null);
			queryName.setEditable(true);
			queryArea.setBackground(null);
			queryArea.setEditable(true);
			bEdit.setText("Save");
		}
		else{
			queryName.setBackground(myGrey);
			queryName.setEditable(false);
			queryArea.setBackground(myGrey);
			queryArea.setEditable(false);
			bEdit.setText("Edit");
		}
	}

}
