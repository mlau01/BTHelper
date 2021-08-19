package bth.gui.options;

import java.util.Vector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

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

import bth.gui.GridBagHelper;

public class SchedulePanel extends JPanel {
	
	private Vector<Vector<String>> t1nDatas = new Vector<Vector<String>>();
	private Vector<Vector<String>> t1wDatas = new Vector<Vector<String>>();
	private Vector<Vector<String>> t1sDatas = new Vector<Vector<String>>();
	private Vector<Vector<String>> t2nDatas = new Vector<Vector<String>>();
	private Vector<Vector<String>> t2wDatas = new Vector<Vector<String>>();
	private Vector<Vector<String>> t2sDatas = new Vector<Vector<String>>();
	
	private Vector<String> tableColumnName;
	
	public SchedulePanel() {
		tableColumnName = new Vector<String>();
		tableColumnName.add(0, "Acronyme");
		tableColumnName.add(1, "Début");
		tableColumnName.add(2, "Fin");
		
		/*Only For Test, remove after*/
		
		Vector<String> line1 = new Vector<String>();
		line1.add("S1");
		line1.add("19:00:33");
		line1.add("23:00:33");
		
		t1nDatas.add(line1);
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
			t1.setBorder(BorderFactory.createTitledBorder("Terminal 1"));
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
					t1nButtons.setLayout(new BoxLayout(t1nButtons, BoxLayout.Y_AXIS));
					t1n.add(t1nButtons);
					{
						JButton t1nButtonAdd = new JButton("Ajouter");
						t1nButtonAdd.setMaximumSize(new Dimension(150,20));
						t1nButtons.add(t1nButtonAdd);
						
						JButton t1nButtonDelete = new JButton("Suprimer");
						t1nButtonDelete.setMaximumSize(new Dimension(150,20));
						t1nButtons.add(t1nButtonDelete);
					}
					
					JPanel t1nTablePanel = new JPanel();

					t1n.add(t1nTablePanel);
					{
						JTable t1nTable = new JTable(t1nDatas, tableColumnName);
						t1nTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
						t1nTable.setFillsViewportHeight(true);
						JScrollPane scrollPane = new JScrollPane(t1nTable);
						t1nTablePanel.add(scrollPane);
						t1nTablePanel.add(t1nTable);
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
					
					JTable t1wTable = new JTable(t1wDatas, tableColumnName);
					t1w.add(t1wTable);
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
					
					JTable t1sTable = new JTable(t1sDatas, tableColumnName);
					t1s.add(t1sTable);
				}
				
			}
			
			JPanel t2 = new JPanel();
			t2.setBorder(BorderFactory.createTitledBorder("Terminal 2"));
			t2.setPreferredSize(new Dimension(700, 150));
			t2.setLayout(new GridLayout(1, 2));
			terminals.add(t2);
			{
				
				
			}
			
			
		}

	}

}
