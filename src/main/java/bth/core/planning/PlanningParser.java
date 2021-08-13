package bth.core.planning;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PlanningParser {
	
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Search a suite of number starting by 1 in the row given
	 * @param row
	 * @return true if the row represent a suite of number starting by 1
	 */
	public static boolean isDayRow(Element row) {
		int dayNumberExpected = 1;
		for(Element cell : row.getElementsByTag("td")) {
			if(cell.text().contains(String.valueOf(dayNumberExpected))) {
				dayNumberExpected++;
				if(dayNumberExpected > 10) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Search a text node with at least 4 chars at row position 1 which normally represent the technician name
	 * @param row
	 * @return true if the text node is found
	 */
	public static boolean isTechRow(Element row) {
		Elements cells = row.getAllElements();
		for (Element cell : cells) {
			logger.trace("Check for technician name cell: {}", cell.text());
			if(cell.text().matches("^[A-Z](\\.|,)[A-Z]{3,}")) {
				logger.trace("Found tech cell: {}", cell.text());
				return true;
			}
			else {
				logger.trace("Not a tech name cell: {}", cell.text());
			}
		}


		return false;
	}
	
	public static ArrayList<ArrayList<String>> getParsedArray(String httpContent)
	{
		Document doc = Jsoup.parse(httpContent);

		//Get the first table in the content
		Element rawTable = doc.getElementsByTag("table").first();

		//Get all rows of the table (tr marker)
		Elements rawRows = rawTable.getElementsByTag("tr");

		//Remove all first row that are before the day row (day row represent all days in a month)
		Elements rawRowsCopy = rawRows.clone();
		int rowIndex = 0;
		for(Element row : rawRowsCopy) {
			if (!isDayRow(row) && ! isTechRow(row)) {
				logger.trace("Removing row: {}", row);
				rawRows.remove(rowIndex);
			} else {
				rowIndex++;
			}
		}
		
		logger.trace("Cleaned datas: {}", rawRows);

		//Determine last column by counting the day row length
		int lastColumn = 0;
		//Row with user cell
		Element dayRow = rawRows.get(0);
		for(Element cell : dayRow.getElementsByTag("td"))
		{
			if(lastColumn > 5 && (cell.text().isEmpty() || cell.text().equals("&nbsp")))
			{
				logger.info("getArray(...) : Last column determinated at {}",lastColumn);
				break;
			}

			lastColumn++;
		}
		
		
		//Creating ArrayList based on table info retrieved before
		final ArrayList<ArrayList<String>> plan = new ArrayList<ArrayList<String>>();
		Elements rawCols = null;
		for(Element rawRow : rawRows)
		{
			
			int curCols = 0;
			rawCols = rawRow.getElementsByTag("td");
			ArrayList<String> arrayRow = new ArrayList<String>();
			
			for(Element cell : rawCols)
			{	
				if(curCols < lastColumn)
					arrayRow.add(cell.text());	
				curCols++;
			}
			logger.trace("Adding planning line(length:{}): {}",arrayRow.size(), arrayRow);
			plan.add(arrayRow);
		}
		
		return (plan);
	}

}
