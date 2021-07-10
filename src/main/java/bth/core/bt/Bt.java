package bth.core.bt;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic bt structure class
 * @author Matt
 *
 */
public class Bt {
	
	private final String wonum;
	private final String date;
	private final String desc;
	private final String gear;
	private final String code;
	private String newDesc;
	private String travelTime;
	private String duration;
	private String issue;
	private String comment;
	private boolean w;
	
	
	public Bt(final String p_wonum, final String p_date, final String p_desc, final String p_gear, final String p_code)
	{
		wonum = p_wonum;
		date = p_date;
		desc = p_desc;
		gear = p_gear;
		code = p_code;
		
		//String newDesc = makeNewDesc(desc);
		setNewDesc(unlecut(desc));
		
		String travelTime = "5";
		if(desc.toLowerCase().contains("bqe")) travelTime = String.valueOf(ThreadLocalRandom.current().nextInt(5, 9));
		setTravelTime(travelTime);
		
		setDuration(String.valueOf(ThreadLocalRandom.current().nextInt(8, 12)));
		String issue = "PC_APPLI";
		if(desc.toLowerCase().contains("tag")) issue = "TAG_ELEC";
		setIssue(issue);
		setComment("");
		setW(false);
	}
	
	public final String getWonum() { return wonum; }
	public final String getDate() { return date; }
	public final String getDesc() { return desc; }
	public final String getGear() { return gear; }
	public final String getCode() { return code; }
	
	// ---- OTHERS ----
	
	public String toString()
	{
		return wonum + "\t" + date + "\t" + desc;
	}
	
	private String unlecut(String orgDesc)
	{
		orgDesc = orgDesc.toUpperCase();
		Pattern p = Pattern.compile("((PTE)|(BQE)) [A-G]{0,1}\\d{1,2}");
		Matcher m = p.matcher(orgDesc);
		//System.out.print(m.matches());
		if(m.find()){
			String locPart = m.group();
			int start = orgDesc.indexOf(locPart);
			if(start - 1 > 0 && orgDesc.charAt(start - 1) == ' ') locPart = " " + locPart;
			orgDesc = orgDesc.replace(locPart, "");
			int firstSpace = 0;
			while(firstSpace < orgDesc.length())
			{
				if(orgDesc.charAt(firstSpace) == ' ') break;
				firstSpace++;
			}
			String firstPart = orgDesc.substring(0, firstSpace);
			String lastPart = orgDesc.substring(firstSpace);
			orgDesc = (firstPart + locPart + lastPart);
		}
		return orgDesc;
	}

	/**
	 * @return the newDesc
	 */
	public String getNewDesc() {
		return newDesc;
	}

	/**
	 * @param newDesc the newDesc to set
	 */
	public void setNewDesc(String newDesc) {
		
		this.newDesc = newDesc;
	}

	/**
	 * @return the travelTime
	 */
	public String getTravelTime() {
		return travelTime;
	}

	/**
	 * @param travelTime the travelTime to set
	 */
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the w
	 */
	public boolean isW() {
		return w;
	}

	/**
	 * @param w the w to set
	 */
	public void setW(boolean w) {
		this.w = w;
	}

	/**
	 * @return the issue
	 */
	public String getIssue() {
		return issue;
	}

	/**
	 * @param issue the issue to set
	 */
	public void setIssue(String issue) {
		this.issue = issue;
	}
}
