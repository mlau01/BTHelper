package bth.core.bt;

public enum SEARCH {
	
	ENTIRE(0),
	START(-1),
	END(1);
	
	private int val = 0;
	
	SEARCH(int pval)		{ this.val = pval;}
	public String toString(){ return ("" + this.val); }
	public int getVal()		{ return this.val; }
}
