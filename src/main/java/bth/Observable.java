package bth;

public interface Observable {
	
	public void addObserver(final Observer obs);
	
	public void notifyObserver(final String notification);
	
	public void removeObserver(final Observer obs);


}
