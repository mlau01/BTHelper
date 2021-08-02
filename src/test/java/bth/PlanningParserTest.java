package bth;

import bth.core.planning.PlanningParser;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlanningParserTest {

    @Test
    public void isDayRowTest_shouldReturnTrueWhenARowWithASuiteOfNumberIsGiven(){
        Element row1 = new Element("tr");
        row1.append("<td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>10</td>");

        Element row2 = new Element("tr");
        row2.append("<td>1</td><td>2</td><td>3</td>");

        assertTrue(PlanningParser.isDayRow(row1));
        assertFalse(PlanningParser.isDayRow(row2));
    }

    @Test
    public void isTechRowTest_shouldReturnTrueWhenCellAtPosition1ContainsAtLeast4Chars(){
        Element row1 = new Element("tr");
        row1.append("<td></td><td>C.RENIA</td><td>A/td>");

        Element row2 = new Element("tr");
        row2.append("<td></td><td>1</td><td>A/td>");

        assertTrue(PlanningParser.isTechRow(row1));
        assertFalse(PlanningParser.isTechRow(row2));
    }
}
