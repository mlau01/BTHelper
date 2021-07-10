package bth.gui.bt;

import java.util.ArrayList;

import javax.swing.JRadioButton;

import bth.core.bt.Bt;

public class BtButton {
	
	private final JRadioButton button;
	private ArrayList<Bt> bts;
	private final String tec;
	

	public BtButton(final String p_tec, final JRadioButton p_button)
	{
		button = p_button;
		tec = p_tec;
	}
	
	public void setBts(final ArrayList<Bt> p_bts)
	{
		bts = p_bts;
		button.setText(tec + ": " + bts.size());
	}
	
	public final ArrayList<Bt> getBts()
	{
		return bts;
	}
	
	public final String getTec()
	{
		return tec;
	}
	
	public final JRadioButton getButton()
	{
		return button;
	}
}

