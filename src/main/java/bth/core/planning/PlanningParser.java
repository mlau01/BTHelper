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
	
	public static ArrayList<ArrayList<String>> getParsedArray(String httpContent)
	{
		Document doc = Jsoup.parse(httpContent);
		
		Element rawTable = doc.getElementsByTag("table").first();
		//logger.debug("Parsing table: {}", rawTable.toString());
		Elements rawRows = rawTable.getElementsByTag("tr");
		logger.trace("Cleaning useless lines from: {}", rawRows);
		//Remove 3 first line
		for(int i = 0; i < 3; i++)
			rawRows.remove(0);
		//Remove 2 last line
		for(int i = 0; i < 2; i++)
			rawRows.remove(rawRows.last());
		
		logger.trace("Cleaned datas: {}", rawRows);
		//Determine last column
		int lastColumn = 0;
		{
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
			plan.add(arrayRow);
		}
		
		return (plan);
	}

}
