package com.pack.pack.rest.api;

import java.io.File;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class JFreeChartTest {

	public static void main(String[] args) throws Exception {
		final String nda = "NDA";
		final String upa = "UPA";
		final String others = "OTHERS";
		final String leading = "Leading";
		final String won = "Won";

		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(263, nda, leading);
		dataset.addValue(249, nda, won);

		dataset.addValue(157, upa, leading);
		dataset.addValue(143, upa, won);

		dataset.addValue(101, others, leading);
		dataset.addValue(87, others, won);

		JFreeChart barChart = ChartFactory.createBarChart(
				"LS_ELECTION_RESULTS_2019", "Alliance", "Score", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File BarChart = new File("F:/BarChart.jpg");
		ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
	}
}
