package project;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;

public class TestGraph {
    public static void main(String[] args) {
        // tested sensor in 4 things
        String[] myLabels = {"Air", "Dry Soil", "Wet Soil", "Water"};
        double[] myRaw = {716, 634, 554, 516};
        // convert to voltage
        double v1 = myRaw[0] * (5.0 / 1023.0); // air
        double v2 = myRaw[1] * (5.0 / 1023.0); // dry soil
        double v3 = myRaw[2] * (5.0 / 1023.0); // wet soil
        double v4 = myRaw[3] * (5.0 / 1023.0); // water
        double[] myVolts = {v1, v2, v3, v4};
        // y = mx + c
        // using air and water as my two points
        double myM = (v4 - v1) / (myRaw[3] - myRaw[0]);
        double myC = v1 - myM * myRaw[0];

        XYSeriesCollection myDataset = new XYSeriesCollection();

        // add each dot
        for (int i = 0; i < myRaw.length; i++) {
            XYSeries dot = new XYSeries(myLabels[i]);
            dot.add(myRaw[i], myVolts[i]);
            myDataset.addSeries(dot);
        }
        // straight line using equation
        XYSeries myLine = new XYSeries("Straight Line");
        for (double r = 500; r <= 730; r++) {
            myLine.add(r, myM * r + myC);
        }
        myDataset.addSeries(myLine);
        // make the chart
        JFreeChart myChart = ChartFactory.createXYLineChart(
                "My Sensor Test Graph",
                "Raw Reading",
                "Voltage (V)",
                myDataset
        );

        XYPlot myPlot = myChart.getXYPlot();
        XYLineAndShapeRenderer myRenderer = new XYLineAndShapeRenderer();
        Color[] dotColors = {Color.RED, Color.ORANGE, Color.GREEN, Color.MAGENTA};
        for (int i = 0; i < myRaw.length; i++) {
            myRenderer.setSeriesLinesVisible(i, false);
            myRenderer.setSeriesShapesVisible(i, true);
            myRenderer.setSeriesPaint(i, dotColors[i]);
            myPlot.addAnnotation(new org.jfree.chart.annotations.XYTextAnnotation(
                    myLabels[i], myRaw[i], myVolts[i] + 0.05));
        }
        // blue line
        myRenderer.setSeriesLinesVisible(4, true);
        myRenderer.setSeriesShapesVisible(4, false);
        myRenderer.setSeriesPaint(4, Color.BLUE);
        myRenderer.setSeriesStroke(4, new BasicStroke(2.0f));
        myPlot.setRenderer(myRenderer);
        JFrame myFrame = new JFrame("Sensor Graph");
        myFrame.add(new ChartPanel(myChart));
        myFrame.setSize(800, 600);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
    }
}