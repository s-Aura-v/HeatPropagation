package serverBased;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static serverBased.MetalDecomposition.fillMetalAlloy;

public class HeatVisualizer {
    int FRAME_HEIGHT = 600;
    int FRAME_WIDTH = 1200;
    int NUM_ROWS = 4;
    int NUM_COLS = 16;
    JFrame frame = new JFrame("Heat Visualizer");
    JPanel bottomPanel = new JPanel(new GridLayout(NUM_ROWS, NUM_COLS));
    JPanel[][] panelGrid = new JPanel[NUM_ROWS][NUM_COLS];
    static double HEAT_CONSTANT_1 = .75;
    static double HEAT_CONSTANT_2 = 1.0;
    static double HEAT_CONSTANT_3 = 1.25;
    static double HC1_PERCENTAGE;
    static double HC2_PERCENTAGE;
    static double HC3_PERCENTAGE;
    static int height;
    static int width;
    static int topLeftTemperature_S = 100;
    static int bottomRightTemperature_T = 100;
    static double METAL_PERCENTAGE = .33;


    void setup() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // PARAMETERS
        JPanel topPanel = new JPanel();
        JLabel alloyLengthLabel = new JLabel("Alloy Length:");
        JTextField alloyLength = new JTextField(5);
        JLabel topLeftTemperature_S_Label = new JLabel("Top Left Temperature:");
        JTextField topLeftTemperature_S_parameter = new JTextField(5);
        JLabel bottomRightTemperature_T_Label = new JLabel("Bottom Right Temperature:");
        JTextField bottomRightTemperature_T_parameter = new JTextField(5);
        JLabel heatConstant_1_Label = new JLabel("Heat Constant 1:");
        JTextField heatConstant_1 = new JTextField(5);
        JLabel heatConstant_2_Label = new JLabel("Heat Constant 2:");
        JTextField heatConstant_2 = new JTextField(5);
        JLabel heatConstant_3_Label = new JLabel("Heat Constant 3:");
        JTextField heatConstant_3 = new JTextField(5);
        JButton submitParameters = new JButton("Submit");

        alloyLength.setText("8");
        topLeftTemperature_S_parameter.setText("100");
        bottomRightTemperature_T_parameter.setText("100");
        heatConstant_1.setText(".75");
        heatConstant_2.setText("1.00");
        heatConstant_3.setText("1.25");

        topPanel.add(alloyLengthLabel);
        topPanel.add(alloyLength);
        topPanel.add(topLeftTemperature_S_Label);
        topPanel.add(topLeftTemperature_S_parameter);
        topPanel.add(bottomRightTemperature_T_Label);
        topPanel.add(bottomRightTemperature_T_parameter);
        topPanel.add(heatConstant_1_Label);
        topPanel.add(heatConstant_1);
        topPanel.add(heatConstant_2_Label);
        topPanel.add(heatConstant_2);
        topPanel.add(heatConstant_3_Label);
        topPanel.add(heatConstant_3);
        topPanel.add(submitParameters);
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 90));
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        submitParameters.addActionListener(e -> {
            // Get the parameters from the text fields
            HEAT_CONSTANT_1 = Double.parseDouble(heatConstant_1.getText());
            HEAT_CONSTANT_2 = Double.parseDouble(heatConstant_2.getText());
            HEAT_CONSTANT_3 = Double.parseDouble(heatConstant_3.getText());
            height = Integer.parseInt(alloyLength.getText());
            width = height * 4;
            topLeftTemperature_S = Integer.parseInt(topLeftTemperature_S_parameter.getText());
            bottomRightTemperature_T = Integer.parseInt(bottomRightTemperature_T_parameter.getText());

            // You need to run the start method in a separate thread because the Java Swing Thread is ALWAYS busy updating the gui
            new Thread(() -> {
                try {
                    start();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        createMetalRepresentation();

        frame.setResizable(false);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    void createMetalRepresentation() {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                panelGrid[i][j] = new JPanel();
                bottomPanel.add(panelGrid[i][j]);
            }
        }
    }

    public void updateGrid(MetalCell[][] metalAlloy) {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                double temperature = metalAlloy[i][j].getTemperature();
                Color color = getColorBasedOnTemperature(temperature);
                panelGrid[i][j].setBackground(color);
            }
        }
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private Color getColorBasedOnTemperature(double temperature) {
        int maxTemp = 100;
        int minTemp = 0;

        // Map temperature to a range of 0-255
        int red = (int) Math.min(255, (temperature - minTemp) / (maxTemp - minTemp) * 255);
        int blue = 255 - red;  // Inverse to create a gradient from blue to red

        return new Color(red, 0, blue);
    }

    public void start() throws InterruptedException {
        MetalCell[][] combinedPartition = new MetalCell[height][width];
        fillMetalAlloy(combinedPartition);
        for (int i = 0; i < 10000; i++) {
            MetalAlloy alloy = new MetalAlloy(combinedPartition, topLeftTemperature_S, bottomRightTemperature_T, true);
            MetalCell[][] leftPartition = alloy.callLeftPartition();
            MetalCell[][] rightPartition = alloy.callServer();
            combinedPartition = alloy.mergePartitions(leftPartition, rightPartition);
            System.out.println("Combined\n" + Arrays.deepToString(combinedPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
            updateGrid(combinedPartition);
            Thread.sleep(150);
        }
    }
}
