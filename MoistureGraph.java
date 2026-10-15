package project;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
//[1] https://www.youtube.com/watch?v=cw31L_OwX3A
//[2] https://www.youtube.com/watch?v=dPJndZjzyBY
//
//[3]https://www.tutorialspoint.com/jfreechart/jfreechart_file_interface.htm

public class MoistureGraph extends JFrame {
    private XYSeries series; //[2]

    private long startTime;

    public MoistureGraph() {                     // [2][3]
        setTitle(" Voltage v/s Time Graph");
        // initialize series
        series = new XYSeries("Live Voltage");
        startTime = System.currentTimeMillis();
        //series to collection
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        //line chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Soil Voltage",
                "Time (seconds)",
                " Voltage ( V )",
                dataset
        );
        //line
        XYPlot plot = chart.getXYPlot();   //[2][4]
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(4.00f));
        plot.setRenderer(renderer);
        // y 0 to 100
        plot.getRangeAxis().setRange(0, 5.00);
        //graph shows
        ChartPanel panel = new ChartPanel(chart);
        add(panel);
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    //live update of graph and x-axis
    public void updateGraph(double moisture) {
        long now = System.currentTimeMillis();
        double elapsedSeconds = (now - startTime) / 1000.0;
        series.addOrUpdate(elapsedSeconds, moisture);
    }
}

