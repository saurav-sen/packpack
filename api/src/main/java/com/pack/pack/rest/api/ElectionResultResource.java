package com.pack.pack.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.rest.web.util.eciresult.ElectionResultUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.utils.NotificationUtil;

/**
 * 
 * @author Saurav
 *
 */
/*@Singleton
@Provider
@Path("/electionResult")*/
public class ElectionResultResource {
	
	private static Logger $LOG = LoggerFactory.getLogger(ElectionResultResource.class);
	
	private static final String HTML_TEMPLATE_TEXT = "<!DOCTYPE html>\r\n" + 
			"<html lang=\"en-US\">\r\n" + 
			"<body>\r\n" + 
			"\r\n" + 
			"<br/>\r\n" + 
			"<h1>General Election Trends & Result 2019 (INDIA) </h1>\r\n" + 
			"\r\n" + 
			"<div id=\"barChart\"></div>\r\n" + 
			"<br/>\r\n" + 
			"<div id=\"table_div\"></div>\r\n" + 
			"\r\n" + 
			"<script type=\"text/javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js\"></script>\r\n" + 
			"<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\r\n" + 
			"\r\n" + 
			"<script type=\"text/javascript\">\r\n" + 
			"// Load google charts\r\n" + 
			"google.charts.load('current', {'packages':['corechart']});\r\n" + 
			"google.charts.setOnLoadCallback(drawChart);\r\n" + 
			"\r\n" + 
			"// Draw the chart and set the chart values\r\n" + 
			"function drawChart() {\r\n" + 
			"  var barChartData = [['Alliance', 'Leading+Wins'],['NDA', $_NDA_LEAD],['UPA', $_UPA_LEAD],['Others', $_OTHERS_LEAD]]\r\n" + 
			"  var data = google.visualization.arrayToDataTable(barChartData);\r\n" + 
			"\r\n" + 
			"  // Optional; add a title and set the width and height of the chart\r\n" + 
			"  var options = {'title':'Trends', chartArea: {width: '60%'}, hAxis: {title: 'Number Of Seats (Won/Leading)', minValue: 0}, vAxis: {title: 'Alliance'}};\r\n" + 
			"\r\n" + 
			"  // Display the chart inside the <div> element with id=\"piechart\"\r\n" + 
			"  var chart = new google.visualization.BarChart(document.getElementById('barChart'));\r\n" + 
			"  chart.draw(data, options);\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"google.charts.load('current', {'packages':['table']});\r\n" + 
			"      google.charts.setOnLoadCallback(drawTable);\r\n" + 
			"\r\n" + 
			"      var tableData = [['NDA', $_NDA_LEAD],['UPA', $_UPA_LEAD],['Others', $_OTHERS_LEAD]];\r\n" + 
			"      function drawTable() {\r\n" + 
			"        var data = new google.visualization.DataTable();\r\n" + 
			"        data.addColumn('string', 'Alliance');\r\n" + 
			"        data.addColumn('number', 'Leading');\r\n" +
			"        data.addRows(tableData);\r\n" + 
			"\r\n" + 
			"        var table = new google.visualization.Table(document.getElementById('table_div'));\r\n" + 
			"\r\n" + 
			"        table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});\r\n" + 
			"      }\r\n" + 
			"\r\n" + 
			"</script>\r\n" + 
			"\r\n" + 
			"</body>\r\n" + 
			"</html>\r\n" + 
			"";

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getElectionResults(@QueryParam("code") String countryCode) throws PackPackException {
		String response = HTML_TEMPLATE_TEXT;
		List<ElectionResult> loadResults = loadResults();
		for (ElectionResult loadResult : loadResults) {
			String name = loadResult.getName();
			response = response.replaceAll(Pattern.quote("$_" + name + "_LEAD"),
					String.valueOf(loadResult.getLeading()));
		}
		return Response.ok(response).header("Content-Type", "text/html")
				.build();
	}
	
	public static void main(String[] args) throws Exception {
		String json = "{\r\n" + 
				"	\"results\": [{\r\n" + 
				"		\"name\": \"NDA\",\r\n" + 
				"		\"leading\": 136\r\n" + 
				"	},\r\n" + 
				"	{\r\n" + 
				"		\"name\": \"UPA\",\r\n" + 
				"		\"leading\": 51\r\n" + 
				"	},\r\n" + 
				"	{\r\n" + 
				"		\"name\": \"OTHERS\",\r\n" + 
				"		\"leading\": 24\r\n" + 
				"	}]\r\n" + 
				"}";
		List<ElectionResult> result = JSONUtil.deserialize(json,
				ElectionResults.class, false).getResults();
		String htmlSnippet = HTML_TEMPLATE_TEXT;
		for (ElectionResult r : result) {
			String name = r.getName();
			htmlSnippet = htmlSnippet.replaceAll(Pattern.quote("$_" + name + "_LEAD"),
					String.valueOf(r.getLeading()));
		}
		System.out.println(htmlSnippet);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus updateElectionResult(String json) {
		JStatus status = new JStatus();
		try {
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			service.addToCache("ELECTION_RESULT_INDIA_LS_2019", json, 10 * 60 * 60);
			status.setInfo("Succesfully updated election result");
			status.setStatus(StatusType.OK);
			List<ElectionResult> result = JSONUtil.deserialize(json,
					ElectionResults.class, false).getResults();
			if(result == null || result.isEmpty()) {
				status.setInfo("Failed to update election result as it is empty");
				status.setStatus(StatusType.ERROR);
				return status;
			}
			String htmlSnippet = HTML_TEMPLATE_TEXT;
			StringBuilder notificationMsg = new StringBuilder();
			for (ElectionResult r : result) {
				notificationMsg = notificationMsg
						.append(r.getName().toUpperCase()).append(" ")
						.append("(Leading/Won:").append(r.getLeading())
						.append(")  ");
				String name = r.getName();
				htmlSnippet = htmlSnippet.replaceAll(Pattern.quote("$_" + name + "_LEAD"),
						String.valueOf(r.getLeading()));
			}
			ElectionResultUtil.storeElectionResultFeed(result, htmlSnippet);
			NotificationUtil.broadcastLiveNewsUpdateSummary(
					notificationMsg.toString(),
					"http://squill.in/api/electionResult?code=IN");
		} catch (Exception e) {
			$LOG.error("Failed to update election result", e);
			status.setInfo("Failed to update election result");
			status.setStatus(StatusType.OK);
		}
		return status;
	}
	
	private List<ElectionResult> loadResults() throws PackPackException {
		RedisCacheService service = ServiceRegistry.INSTANCE.findService(RedisCacheService.class);
		String json = service.getFromCache("ELECTION_RESULT_INDIA_LS_2019", String.class);
		List<ElectionResult> result = JSONUtil.deserialize(json,
				ElectionResults.class, false).getResults();
		return result;
		/*List<ElectionResult> result = new ArrayList<ElectionResult>();
		result.add(new ElectionResult("NDA", 263, 249));
		result.add(new ElectionResult("UPA", 157, 143));
		result.add(new ElectionResult("OTHERS", 101, 87));
		return result;*/
	}
}
