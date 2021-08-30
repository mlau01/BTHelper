package bth.core.datasource;

import java.util.ArrayList;

import bth.Observable;
import bth.Observer;
import bth.core.bt.Bt;

public interface IBtSource extends Observable {
	
	public void open() throws DatasourceException;
	public void close() throws DatasourceException;
	public ArrayList<Bt> getBts(Object source) throws DatasourceException;
	public String getDateFormat();

}
