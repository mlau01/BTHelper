package bth.gui.options;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.directory.InvalidAttributeValueException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.model.Assignment;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.schedule.ScheduleCategory;
import bth.core.schedule.ScheduleService;
import bth.core.schedule.exception.AssignmentNotFoundException;
import bth.gui.MWin;

public class SchedulePanel extends JPanel {
	
	private Map<ScheduleCategory, Vector<Vector<String>>> dataMap;
	private Map<ScheduleCategory, JTable> tableMap;
	private JTextField acronym, beginTime, endTime;
	private static final Logger logger = LogManager.getLogger();
	private String[] tableColumnName;
	
	private MWin mWin;
	private ScheduleService scheduleService;
	
	public SchedulePanel(MWin p_mWin) {
		mWin = p_mWin;
		tableColumnName = new String[] {"Acronyme", "Début", "Fin"};
		tableMap = new Hashtable<ScheduleCategory, JTable>();
		dataMap = new Hashtable<ScheduleCategory, Vector<Vector<String>>>();
	}

	public void loadDatas() {
		scheduleService = mWin.getCorma().getScheduleService();
		
		Vector<Vector<String>> t1nDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T1));
		dataMap.put(ScheduleCategory.T1, t1nDatas);
		tableMap.get(ScheduleCategory.T1).setModel(new ScheduleTableModel(tableColumnName, t1nDatas));
		
		Vector<Vector<String>> t1wDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T1W));
		dataMap.put(ScheduleCategory.T1W, t1wDatas);
		tableMap.get(ScheduleCategory.T1W).setModel(new ScheduleTableModel(tableColumnName, t1wDatas));
		
		Vector<Vector<String>> t1sDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T1S));
		dataMap.put(ScheduleCategory.T1S, t1sDatas);
		tableMap.get(ScheduleCategory.T1S).setModel(new ScheduleTableModel(tableColumnName, t1sDatas));
		
		Vector<Vector<String>> t2nDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T2));
		dataMap.put(ScheduleCategory.T2, t2nDatas);
		tableMap.get(ScheduleCategory.T2).setModel(new ScheduleTableModel(tableColumnName, t2nDatas));
		
		Vector<Vector<String>> t2wDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T2W));
		dataMap.put(ScheduleCategory.T2W, t2wDatas);
		tableMap.get(ScheduleCategory.T2W).setModel(new ScheduleTableModel(tableColumnName, t2wDatas));
		
		Vector<Vector<String>> t2sDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T2S));
		dataMap.put(ScheduleCategory.T2S, t2sDatas);
		tableMap.get(ScheduleCategory.T2S).setModel(new ScheduleTableModel(tableColumnName, t2sDatas));

	}
	
	/**
	 * Build a bidimentional array as Vector<Vector<String>>
	 * @param assignmentList
	 * @return
	 */
	public Vector<Vector<String>> buildVectorArray(List<Assignment> assignmentList) {
		DateTimeFormatter formatter = scheduleService.getDateTimeFormatter();
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		for(Assignment assignment : assignmentList) {
			Vector<String> column = new Vector<String>();
			column.add(assignment.getAssignment());
			column.add(assignment.getBeginTime().format(formatter));
			column.add(assignment.getEndTime().format(formatter));
			
			rows.add(column);
		}
		
		return rows;
	}

	public void loadWidgets() {

		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel textfields = new JPanel();
		textfields.setMaximumSize(new Dimension(800, 150));
		this.add(textfields);
		textfields.setLayout(new FlowLayout());
		{
			textfields.add(new JLabel("Acronyme"));
			
			acronym = new JTextField();
			acronym.setPreferredSize(new Dimension(50, 20));
			textfields.add(acronym);
			
			textfields.add(new JLabel("Horaire début"));
			
			beginTime = new JTextField();
			beginTime.setPreferredSize(new Dimension(150, 20));
			textfields.add(beginTime);
			
			textfields.add(new JLabel("Horaire fin"));
			
			endTime = new JTextField();
			endTime.setPreferredSize(new Dimension(150, 20));
			textfields.add(endTime);
		}
		
		JPanel terminals = new JPanel();
		this.add(terminals);
		terminals.setLayout(new GridLayout(1, 2));
		{
			JPanel t1 = new JPanel();
			t1.setBorder(BorderFactory.createTitledBorder("BT du Terminal 1"));
			t1.setLayout(new GridLayout(3, 1));
			terminals.add(t1);
			{
				JPanel t1n = new JPanel();
				t1n.setBorder(BorderFactory.createTitledBorder("Normal"));
				t1n.setLayout(new GridLayout(1,2));
				t1.add(t1n);
				{
					JPanel t1nButtons = new JPanel();
					t1nButtons.setLayout(new GridLayout(2,1));
					t1nButtons.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
					t1n.add(t1nButtons);
					{
						JButton t1nButtonAdd = new JButton("Ajouter");
						t1nButtonAdd.addActionListener(e -> {
							action_addAssignment(ScheduleCategory.T1);
						});
						t1nButtonAdd.setMaximumSize(new Dimension(50, 20));
						t1nButtons.add(t1nButtonAdd);
						
						JButton t1nButtonDelete = new JButton("Suprimer");
						t1nButtonDelete.addActionListener(e -> {
							action_deleteAssignment(ScheduleCategory.T1);
						});
						t1nButtonDelete.setMaximumSize(new Dimension(50, 20));
						t1nButtons.add(t1nButtonDelete);
					}
					
					JPanel t1nTablePanel = new JPanel();
					t1nTablePanel.setLayout(new GridLayout(1,1));
					t1n.add(t1nTablePanel);
					{
						tableMap.put(ScheduleCategory.T1, new JTable());
						JScrollPane scrollPane = new JScrollPane(tableMap.get(ScheduleCategory.T1));
						tableMap.get(ScheduleCategory.T1).setFillsViewportHeight(true);

						t1nTablePanel.add(scrollPane);
					}
				}
				
				JPanel t1w = new JPanel();
				t1w.setBorder(BorderFactory.createTitledBorder("Week-end"));
				t1w.setLayout(new GridLayout(1,2));
				t1.add(t1w);
				{
					JPanel t1wButtons = new JPanel();
					t1wButtons.setLayout(new GridLayout(2,1));
					t1wButtons.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
					t1w.add(t1wButtons);
					{
						JButton t1wButtonAdd = new JButton("Ajouter");
						t1wButtonAdd.addActionListener(e -> {
							action_addAssignment(ScheduleCategory.T1W);
						});
						t1wButtonAdd.setMaximumSize(new Dimension(50, 20));
						t1wButtons.add(t1wButtonAdd);
						
						JButton t1wButtonDelete = new JButton("Suprimer");
						t1wButtonDelete.addActionListener(e -> {
							action_deleteAssignment(ScheduleCategory.T1W);
						});
						t1wButtonDelete.setMaximumSize(new Dimension(50, 20));
						t1wButtons.add(t1wButtonDelete);
					}
					
					JPanel t1wTablePanel = new JPanel();
					t1wTablePanel.setLayout(new GridLayout(1,1));
					t1w.add(t1wTablePanel);
					{
						tableMap.put(ScheduleCategory.T1W, new JTable());
						JScrollPane scrollPane = new JScrollPane(tableMap.get(ScheduleCategory.T1W));
						tableMap.get(ScheduleCategory.T1W).setFillsViewportHeight(true);

						t1wTablePanel.add(scrollPane);
					}
				}
				
				JPanel t1s = new JPanel();
				t1s.setBorder(BorderFactory.createTitledBorder("Super"));
				t1s.setLayout(new GridLayout(1,2));
				t1.add(t1s);
				{
					JPanel t1sButtons = new JPanel();
					t1sButtons.setLayout(new GridLayout(2,1));
					t1sButtons.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
					t1s.add(t1sButtons);
					{
						JButton t1sButtonAdd = new JButton("Ajouter");
						t1sButtonAdd.addActionListener(e -> {
							action_addAssignment(ScheduleCategory.T1S);
						});
						t1sButtonAdd.setMaximumSize(new Dimension(50, 20));
						t1sButtons.add(t1sButtonAdd);
						
						JButton t1sButtonDelete = new JButton("Suprimer");
						t1sButtonDelete.addActionListener(e -> {
							action_deleteAssignment(ScheduleCategory.T1S);
						});
						t1sButtonDelete.setMaximumSize(new Dimension(50, 20));
						t1sButtons.add(t1sButtonDelete);
					}
					
					JPanel t1sTablePanel = new JPanel();
					t1sTablePanel.setLayout(new GridLayout(1,1));
					t1s.add(t1sTablePanel);
					{
						tableMap.put(ScheduleCategory.T1S, new JTable());
						JScrollPane scrollPane = new JScrollPane(tableMap.get(ScheduleCategory.T1S));
						tableMap.get(ScheduleCategory.T1S).setFillsViewportHeight(true);

						t1sTablePanel.add(scrollPane);
					}
				}
				
			}
			
			JPanel t2 = new JPanel();
			t2.setBorder(BorderFactory.createTitledBorder("BT du Terminal 2"));
			t2.setPreferredSize(new Dimension(700, 150));
			t2.setLayout(new GridLayout(3, 1));
			terminals.add(t2);
			{				
				JPanel t2n = new JPanel();
				t2n.setBorder(BorderFactory.createTitledBorder("Normal"));
				t2n.setLayout(new GridLayout(1,2));
				t2.add(t2n);
				{
					JPanel t2nButtons = new JPanel();
					t2nButtons.setLayout(new GridLayout(2,1));
					t2nButtons.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
					t2n.add(t2nButtons);
					{
						JButton t2nButtonAdd = new JButton("Ajouter");
						t2nButtonAdd.addActionListener(e -> {
							action_addAssignment(ScheduleCategory.T2);
						});
						t2nButtonAdd.setMaximumSize(new Dimension(50, 20));
						t2nButtons.add(t2nButtonAdd);
						
						JButton t2nButtonDelete = new JButton("Suprimer");
						t2nButtonDelete.addActionListener(e -> {
							action_deleteAssignment(ScheduleCategory.T2);
						});
						t2nButtonDelete.setMaximumSize(new Dimension(50, 20));
						t2nButtons.add(t2nButtonDelete);
					}
					
					JPanel t2nTablePanel = new JPanel();
					t2nTablePanel.setLayout(new GridLayout(1,1));
					t2n.add(t2nTablePanel);
					{
						tableMap.put(ScheduleCategory.T2, new JTable());
						JScrollPane scrollPane = new JScrollPane(tableMap.get(ScheduleCategory.T2));
						tableMap.get(ScheduleCategory.T2).setFillsViewportHeight(true);

						t2nTablePanel.add(scrollPane);
					}
				}
				
				JPanel t2w = new JPanel();
				t2w.setBorder(BorderFactory.createTitledBorder("Week-end"));
				t2w.setLayout(new GridLayout(1,2));
				t2.add(t2w);
				{
					JPanel t2wButtons = new JPanel();
					t2wButtons.setLayout(new GridLayout(2,1));
					t2wButtons.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
					t2w.add(t2wButtons);
					{
						JButton t2wButtonAdd = new JButton("Ajouter");
						t2wButtonAdd.addActionListener(e -> {
							action_addAssignment(ScheduleCategory.T2W);
						});
						t2wButtonAdd.setMaximumSize(new Dimension(50, 20));
						t2wButtons.add(t2wButtonAdd);
						
						JButton t2wButtonDelete = new JButton("Suprimer");
						t2wButtonDelete.addActionListener(e -> {
							action_deleteAssignment(ScheduleCategory.T2W);
						});
						t2wButtonDelete.setMaximumSize(new Dimension(50, 20));
						t2wButtons.add(t2wButtonDelete);
					}
					
					JPanel t2wTablePanel = new JPanel();
					t2wTablePanel.setLayout(new GridLayout(1,1));
					t2w.add(t2wTablePanel);
					{
						tableMap.put(ScheduleCategory.T2W, new JTable());
						JScrollPane scrollPane = new JScrollPane(tableMap.get(ScheduleCategory.T2W));
						tableMap.get(ScheduleCategory.T2W).setFillsViewportHeight(true);

						t2wTablePanel.add(scrollPane);
					}
				}
				
				JPanel t2s = new JPanel();
				t2s.setBorder(BorderFactory.createTitledBorder("Super"));
				t2s.setLayout(new GridLayout(1,2));
				t2.add(t2s);
				{
					JPanel t2sButtons = new JPanel();
					t2sButtons.setLayout(new GridLayout(2,1));
					t2sButtons.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
					t2s.add(t2sButtons);
					{
						JButton t2sButtonAdd = new JButton("Ajouter");
						t2sButtonAdd.addActionListener(e -> {
							action_addAssignment(ScheduleCategory.T2S);
						});
						t2sButtonAdd.setMaximumSize(new Dimension(50, 20));
						t2sButtons.add(t2sButtonAdd);
						
						JButton t2sButtonDelete = new JButton("Suprimer");
						t2sButtonDelete.addActionListener(e -> {
							action_deleteAssignment(ScheduleCategory.T2S);
						});
						t2sButtonDelete.setMaximumSize(new Dimension(50, 20));
						t2sButtons.add(t2sButtonDelete);
					}
					
					JPanel t2sTablePanel = new JPanel();
					t2sTablePanel.setLayout(new GridLayout(1,1));
					t2s.add(t2sTablePanel);
					{
						tableMap.put(ScheduleCategory.T2S, new JTable());
						JScrollPane scrollPane = new JScrollPane(tableMap.get(ScheduleCategory.T2S));
						tableMap.get(ScheduleCategory.T2S).setFillsViewportHeight(true);

						t2sTablePanel.add(scrollPane);
					}
				}
				
				
				
			}
		}
	}

	private void action_addAssignment(ScheduleCategory scheduleCategory) {
		String acronymValue = acronym.getText();
		String beginTimeValue = beginTime.getText();
		String endTimeValue = endTime.getText();
		if(acronymValue.isEmpty() || beginTimeValue.isEmpty() || endTimeValue.isEmpty()) {
			mWin.showError("Input field error", "Champ(s) vide(s) !");
			return;
		}
		try {
			scheduleService.getDateTimeFormatter().parse(beginTimeValue);
			scheduleService.getDateTimeFormatter().parse(endTimeValue);
		} catch (DateTimeParseException e) {
			mWin.showError("Input field error", "Format d'horaire invalide");
			return;
		}

		try {
			scheduleService.addAssignment(scheduleCategory, acronym.getText(), beginTime.getText(), endTime.getText());
		} catch (Exception e) {
			mWin.showError("error", e.getMessage());
			return;
		}
		
		Vector<String> newLine = new Vector<String>();
		newLine.add(acronym.getText());
		newLine.add(beginTime.getText());
		newLine.add(endTime.getText());
		
		JTable table = tableMap.get(scheduleCategory);
		Vector<Vector<String>> datas = dataMap.get(scheduleCategory);
		datas.add(newLine);
		((ScheduleTableModel)table.getModel()).fireTableDataChanged();
		clearFields();
	}
	
	private void action_deleteAssignment(ScheduleCategory scheduleCategory) {
		JTable table = tableMap.get(scheduleCategory);
		int row = table.getSelectedRow();
		Vector<String> line = dataMap.get(scheduleCategory).get(row);
		String acronym = line.get(0);
		String beginTime = line.get(1);
		String endTime = line.get(2);

		int response = JOptionPane.showConfirmDialog(table, "Delete : " + acronym + " (" + beginTime + " - " + endTime + ") ?");
		if(response != JOptionPane.YES_OPTION) {
			return;
		}
		
		try {
			scheduleService.deleteAssignement(scheduleCategory, acronym, beginTime, endTime);
		} catch (AssignmentNotFoundException | OptionException e) {
			mWin.showError(e.getClass().getName(), e.getMessage());
			return;
		}
		
		dataMap.get(scheduleCategory).remove(line);
		((ScheduleTableModel)table.getModel()).fireTableDataChanged();
	}
	
	private void clearFields() {
		acronym.setText("");
		beginTime.setText("");
		endTime.setText("");
	}

}
