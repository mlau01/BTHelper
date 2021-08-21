package bth.gui.options;

import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.print.PrinterException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.model.Assignment;
import bth.core.options.OptionsException;
import bth.core.options.OptionsService;
import bth.core.schedule.ScheduleCategory;
import bth.core.schedule.ScheduleService;
import bth.gui.GridBagHelper;
import bth.gui.MWin;

public class SchedulePanel extends JPanel {
	
	private Vector<Vector<String>> t1nDatas;
	private Vector<Vector<String>> t1wDatas;
	private Vector<Vector<String>> t1sDatas;
	private Vector<Vector<String>> t2nDatas;
	private Vector<Vector<String>> t2wDatas;
	private Vector<Vector<String>> t2sDatas;
	private JTable t1nTable, t1wTable, t1sTable;
	private static final Logger logger = LogManager.getLogger();
	private String[] tableColumnName;
	
	private MWin mWin;
	
	public SchedulePanel(MWin p_mWin) {
		mWin = p_mWin;
		tableColumnName = new String[] {"Acronyme", "Début", "Fin"};
	}

	public void loadDatas(OptionsService optionService) {
		ScheduleService scheduleService = new ScheduleService();
		try {
			scheduleService.loadfromOptions(optionService);
		} catch (OptionsException e) {
			mWin.showError(e.getClass().toString(), e.getMessage());
			logger.error(e);
		} catch (Exception e) {
			mWin.showError(e.getClass().toString(), e.getMessage());
			logger.error(e);
		}
		
		t1nDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T1));
		t1nTable.setModel(new ScheduleTableModel(tableColumnName, t1nDatas));
		t1wDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T1W));
		t1wTable.setModel(new ScheduleTableModel(tableColumnName, t1wDatas));
		t1sDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T1S));
		t1sTable.setModel(new ScheduleTableModel(tableColumnName, t1sDatas));
		t2nDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T2));
		t2wDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T2W));
		t2sDatas = buildVectorArray(scheduleService.getAssignementList(ScheduleCategory.T2S));
		
		
		
	}
	
	/**
	 * Build a bidimentional array as Vector<Vector<String>>
	 * @param assignmentList
	 * @return
	 */
	public Vector<Vector<String>> buildVectorArray(List<Assignment> assignmentList) {
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		for(Assignment assignment : assignmentList) {
			Vector<String> column = new Vector<String>();
			column.add(assignment.getAssignment());
			column.add(assignment.getBeginTime().toString());
			column.add(assignment.getEndTime().toString());
			
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
			
			JTextField acronym = new JTextField();
			acronym.setPreferredSize(new Dimension(50, 20));
			textfields.add(acronym);
			
			textfields.add(new JLabel("Horaire début"));
			
			JTextField beginTime = new JTextField();
			beginTime.setPreferredSize(new Dimension(150, 20));
			textfields.add(beginTime);
			
			textfields.add(new JLabel("Horaire fin"));
			
			JTextField endTime = new JTextField();
			endTime.setPreferredSize(new Dimension(150, 20));
			textfields.add(endTime);
		}
		
		JPanel terminals = new JPanel();
		this.add(terminals);
		terminals.setLayout(new GridLayout(1, 2));
		{
			JPanel t1 = new JPanel();
			t1.setBorder(BorderFactory.createTitledBorder("BT du Terminal 1"));
			//t1.setPreferredSize(new Dimension(700, 150));
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
						t1nButtonAdd.setMaximumSize(new Dimension(50, 20));
						t1nButtons.add(t1nButtonAdd);
						
						JButton t1nButtonDelete = new JButton("Suprimer");
						t1nButtonDelete.setMaximumSize(new Dimension(50, 20));
						t1nButtons.add(t1nButtonDelete);
					}
					
					JPanel t1nTablePanel = new JPanel();
					t1nTablePanel.setLayout(new GridLayout(1,1));
					t1n.add(t1nTablePanel);
					{
						t1nTable = new JTable();
						JScrollPane scrollPane = new JScrollPane(t1nTable);
						t1nTable.setFillsViewportHeight(true);

						t1nTablePanel.add(scrollPane);
					}
				}
				
				JPanel t1w = new JPanel();
				t1w.setBorder(BorderFactory.createTitledBorder("Week-end"));
				t1.add(t1w);
				{
					JPanel t1wButtons = new JPanel();
					t1wButtons.setLayout(new GridLayout(2,1));
					t1w.add(t1wButtons);
					{
						JButton t1wButtonAdd = new JButton("Ajouter");
						t1wButtons.add(t1wButtonAdd);
						JButton t1wButtonDelete = new JButton("Suprimer");
						t1wButtons.add(t1wButtonDelete);
					}
					
					t1wTable = new JTable();
					JScrollPane scrollPane = new JScrollPane(t1wTable);
					t1w.add(scrollPane);
				}
				
				JPanel t1s = new JPanel();
				t1s.setBorder(BorderFactory.createTitledBorder("Super"));
				t1.add(t1s);
				{
					JPanel t1sButtons = new JPanel();
					t1sButtons.setLayout(new GridLayout(2,1));
					t1s.add(t1sButtons);
					{
						JButton t1sButtonAdd = new JButton("Ajouter");
						t1sButtons.add(t1sButtonAdd);
						JButton t1sButtonDelete = new JButton("Suprimer");
						t1sButtons.add(t1sButtonDelete);
					}
					
					t1sTable = new JTable();
					t1s.add(t1sTable);
				}
				
			}
			
			JPanel t2 = new JPanel();
			t2.setBorder(BorderFactory.createTitledBorder("BT du Terminal 2"));
			t2.setPreferredSize(new Dimension(700, 150));
			t2.setLayout(new GridLayout(1, 2));
			terminals.add(t2);
			{
				
				
			}
			
			
		}

	}

}
