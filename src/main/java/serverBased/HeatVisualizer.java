package serverBased;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static serverBased.MetalDecomposition.fillMetalAlloy;

public class HeatVisualizer {
    int FRAME_HEIGHT = 600;
    int FRAME_WIDTH = 1200;
    JFrame frame = new JFrame("Heat Visualizer");
    JPanel bottomPanel;
    JPanel[][] panelGrid;
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
        bottomPanel = new JPanel();

        submitParameters.addActionListener(e -> {
            // Get the parameters from the text fields
            HEAT_CONSTANT_1 = Double.parseDouble(heatConstant_1.getText());
            HEAT_CONSTANT_2 = Double.parseDouble(heatConstant_2.getText());
            HEAT_CONSTANT_3 = Double.parseDouble(heatConstant_3.getText());
            height = Integer.parseInt(alloyLength.getText());
            width = height * 4;
            topLeftTemperature_S = Integer.parseInt(topLeftTemperature_S_parameter.getText());
            bottomRightTemperature_T = Integer.parseInt(bottomRightTemperature_T_parameter.getText());
            bottomPanel = new JPanel(new GridLayout(height, width));
            panelGrid = new JPanel[height][width];
            createMetalRepresentation();
            frame.add(bottomPanel, BorderLayout.CENTER);

            // You need to run the start method in a separate thread because the Java Swing Thread is ALWAYS busy updating the gui
            new Thread(() -> {
                try {
                    start();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        frame.setResizable(false);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    public void start() throws InterruptedException {
        MetalCell[][] combinedPartition = new MetalCell[height][width];
        fillMetalAlloy(combinedPartition);
        MetalAlloy serverAlloy = new MetalAlloy(combinedPartition, topLeftTemperature_S, bottomRightTemperature_T, false, MetalDecomposition.ITERATIONS);
        MetalAlloy clientAlloy = new MetalAlloy(combinedPartition, topLeftTemperature_S, bottomRightTemperature_T, true, MetalDecomposition.ITERATIONS);
        Client client = new Client(clientAlloy);

    }

    void createMetalRepresentation() {
        if (bottomPanel == null || panelGrid == null) {
            return;
        }
        bottomPanel.removeAll(); // Clear previous panels, if any
        for (int i = 0; i < panelGrid.length; i++) {
            for (int j = 0; j < panelGrid[i].length; j++) {
                panelGrid[i][j] = new JPanel();
                bottomPanel.add(panelGrid[i][j]);
            }
        }
    }


    public void updateGrid(MetalCell[][] metalAlloy) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double temperature = metalAlloy[i][j].getTemperature();
                Color color = getColorBasedOnTemperature(temperature);
                panelGrid[i][j].setBackground(color);
            }
        }
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private Color getColorBasedOnTemperature(double temperature) {
        int maxTemp = 0;
        int minTemp = 0;
        if (topLeftTemperature_S > bottomRightTemperature_T) {
            maxTemp = topLeftTemperature_S;
        } else {
            maxTemp = bottomRightTemperature_T;
        }

        // Map temperature to a range of 0-255
        int red = (int) Math.min(255, (temperature - minTemp) / (maxTemp - minTemp) * 255);
        int blue = 255 - red;  // Inverse to create a gradient from blue to red

        return new Color(red, 0, blue);
    }
}
