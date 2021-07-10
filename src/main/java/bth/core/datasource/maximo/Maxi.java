package bth.core.datasource.maximo;

import java.util.ArrayList;

public class Maxi {
	
	public static final int NONE = -1;
	public static final int BQE = 0;
	public static final int PTE = 1;
	public static final int BLS = 2;
	public static final int EGATE = 3;
	public static final String GBQET1 = "8770/02";
	public static final String GBQET2 = "8770/12";
	public static final String GBQET22 = "8770/31";
	public static final String GPTET1 = "8770/03";
	public static final String GPTET2 = "8770/13";
	public static final String GPTET22 = "8770/32";
	public static final String GDBAT1 = "8770/35";
	public static final String GDBAT22 = "8770/34";
	public static final String GEGATET1 = "8773/T1";
	public static final String GEGATET2 = "8773/T2";
	public static final String GBLST1 = "8771/T1";
	public static final String GBLST22 = "8771/T22";
	
	
	public static int[] getIssueMap(String gear, String s_issue)
	{
		int[] value = new int[2];
		int gearType = NONE;
		if(gear.equals(GBQET1)) gearType = BQE;
		else if(gear.equals(GBQET2)) gearType = BQE;
		else if(gear.equals(GBQET22)) gearType = BQE;
		else if(gear.equals(GBQET1)) gearType = BQE;
		else if(gear.equals(GPTET1)) gearType = PTE;
		else if(gear.equals(GPTET2)) gearType = PTE;
		else if(gear.equals(GPTET22)) gearType = PTE;
		else if(gear.equals(GDBAT1)) gearType = BLS;
		else if(gear.equals(GDBAT22)) gearType = BLS;
		else if(gear.equals(GBLST1)) gearType = BLS;
		else if(gear.equals(GBLST22)) gearType = BLS;
		else if(gear.equals(GEGATET1)) gearType = EGATE;
		else if(gear.equals(GEGATET2)) gearType = EGATE;
		else return null;
		
		ISSUE issue = Maxi.ISSUE.valueOf(s_issue);
		if(issue == null) return null;
		value[0] = issue.getFirstIndex(gearType);
		value[1] = issue.getLastIndex();
		
		return value;
		
	}
	public enum ISSUE {
		
		
		TAG_JAM(0,0,NONE, NONE, NONE),
		TAG_ELEC(5,0,NONE, NONE, NONE),
		PC_CABLE(0,3,4, 2, 0),
		PC_APPLI(1,3,4, 2, 0),
		PC_HARDW(2,3,4, 2, 0),
		PC_ECRAN(3,3,4, 2, 0),
		
		USR_MANHARD(1,5,5, NONE, NONE),
		USR_MANSOFT(2,5,5, NONE, NONE),
		USR_RAS(3,5,5, NONE, NONE),
		RTE_APPLI(9,0, NONE, NONE, NONE),
		RTE_HARD(9,2,NONE, NONE, NONE),
		CAB_JAM(0,10, 7, NONE, NONE),
		CAB_PEC(2,10, 7, NONE, NONE),
		CAB_CONF(4,10, 7, NONE, NONE),
		CAB_ELEC(5,10, 7, NONE, NONE),
		
		GR_ELEC(3,NONE, 0, NONE, NONE),
		OKI_CABLE(0,NONE, 2, NONE, NONE),
		OKI_ELEC(2,NONE, 2, NONE, NONE),
		PAP_PAP(0,NONE,3, NONE, NONE),
		PAP_ENCROKI(2,NONE,3, NONE, NONE),
		COMPAGNIE(NONE, 6, 6, 6, NONE),
		NONUNISYS(NONE, 7, 8, 5, NONE),
		BLS_TAG(NONE, NONE, NONE, 7, NONE),
		BLS_EGATE_USR(NONE, NONE, NONE, 1, 2);
		
		private int index;
		private int bqe_index;
		private int pte_index;
		private int bls_index;
		private int egate_index;
		ISSUE(int p_index, int p_bqe_index, int p_pte_index, int p_bls_index, int p_egate_index){
			index = p_index;
			bqe_index = p_bqe_index;
			pte_index = p_pte_index;
			bls_index = p_bls_index;
			egate_index = p_egate_index;
		}
		
		public final int getLastIndex()
		{
			return this.index;
		}

		public final int getFirstIndex(int gearType)
		{
			int value = NONE;
			if(gearType == BQE) value = this.bqe_index;
			if(gearType == PTE) value = this.pte_index;
			if(gearType == BLS) value = this.bls_index;
			if(gearType == EGATE) value = this.egate_index;
			
			return value;
		}
	}
}
