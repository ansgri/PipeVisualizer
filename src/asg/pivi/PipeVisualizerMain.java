package asg.pivi;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.views.ChartPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author asg
 */
public class PipeVisualizerMain {

    private static JFrame chartFrame = null;

    private static Map<String, ITrace2D> chartTraces = new HashMap<String, ITrace2D>();

    private static void putValue(String chartKey, double value) {
        ITrace2D t = chartTraces.get(chartKey);
        if (t == null) {
            if (chartFrame == null) {
                showGraph();
            }
            
            ZoomableChart chart = new ZoomableChart();
            t = new Trace2DSimple(chartKey);
            chartFrame.getContentPane().add(new ChartPanel(chart));
            chart.addTrace(t);
            chartTraces.put(chartKey, t);
            chartFrame.validate();
        }
        t.addPoint(t.getSize(), value);
    }

    private static void showGraph() {
        if (chartFrame == null) {
            JFrame frame = new JFrame("PipeVisualizer");
            frame.setSize(new Dimension(600, 600));
            frame.getContentPane().setLayout(new GridLayout(0, 3));

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            chartFrame = frame;
            chartFrame.setVisible(true);
        }
    }

    private static void processLine(String line) {
        if (line.startsWith("%")) {
            String[] parts = line.split("\\s", 2);
            if (parts.length != 2) {
                System.err.println("Illegal[partNumber] PipeVisualizer form: " + line);
            }
            try {
                putValue(parts[0].substring(1), Double.parseDouble(parts[1]));
            } catch (NumberFormatException e) {
                System.err.println("Illegal[numberFormat] PipeVisualizer form: " + line);
            }
        } else {
            System.out.println(line);
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // The logic: read stdin, filter lines of the form %something value,
        // add them to chart 'something', write other lines to stdout

        if (args.length != 0) {
            System.err.println("Usage: your-program | java -jar PipeVisualizer.jar");
        } else {
            String line;
            BufferedReader re = new BufferedReader(new InputStreamReader(System.in));
            while ((line = re.readLine()) != null) {
                processLine(line);
            }
        }
    }

}
