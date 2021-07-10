package bth.gui.planning;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PlanningCellRenderer extends DefaultTableCellRenderer{
	
	ArrayList<Integer> weekCol = null;
	int columnOfToday = -1;
	public PlanningCellRenderer(ArrayList<Integer> p_weekCol)
	{
		weekCol = p_weekCol;
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(weekCol.contains((Integer)column))
		{
			//Color of weekend days
			c.setBackground(Color.decode("#A0E0E0"));
		}
		
		else if(value.equals("SS") || value.equals("SM"))
		{
			c.setBackground(Color.decode("#FF0000"));
		}
		else if(value.equals("RTT"))
		{
			c.setBackground(Color.decode("#00B0F0"));
		}
		else if(value.equals("C"))
		{
			c.setBackground(Color.decode("#0070C0"));
		}
		else if(value.equals("N"))
		{
			c.setBackground(Color.decode("#999933"));
		}
		else if(value.equals("Abs"))
		{
			c.setBackground(Color.decode("#CC99CC"));
		}
		else if(value.equals("Rec"))
		{
			c.setBackground(Color.decode("#A6CAF0"));
		}
		else if(value.equals("F"))
		{
			c.setBackground(Color.decode("#FFFF00"));
		}
		else if(row % 2 == 0)
		{
			//Pair line color
			c.setBackground(Color.WHITE);
		}
		else if(row % 2 == 1)
		{
			//Unpair line color
			c.setBackground(Color.decode("#E7F9FF"));
		}
		else
		{
			//Default color
			c.setBackground(Color.WHITE);
		}
	
		return c;
			
	}
}
