package localmachine;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HeatVisualizer {

    void setup() {
        JFrame frame = new JFrame("Heat Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // PARAMETERS
        JPanel topPanel = new JPanel();
        JLabel alloyLengthLabel = new JLabel("Alloy Length:");
        JTextField alloyLength = new JTextField(5);
        JLabel topLeftTemperature_S_Label = new JLabel("Top Left Temperature:");
        JTextField topLeftTemperature_S = new JTextField(5);
        JLabel bottomRightTemperature_T_Label = new JLabel("Bottom Right Temperature:");
        JTextField bottomRightTemperature_T = new JTextField(5);
        JLabel heatConstant_1_Label = new JLabel("Heat Constant 1:");
        JTextField heatConstant_1 = new JTextField(5);
        JLabel heatConstant_2_Label = new JLabel("Heat Constant 2:");
        JTextField heatConstant_2 = new JTextField(5);
        JLabel heatConstant_3_Label = new JLabel("Heat Constant 3:");
        JTextField heatConstant_3 = new JTextField(5);
        JButton submitParameters = new JButton("Submit");
        topPanel.add(alloyLengthLabel);
        topPanel.add(alloyLength);
        topPanel.add(topLeftTemperature_S_Label);
        topPanel.add(topLeftTemperature_S);
        topPanel.add(bottomRightTemperature_T_Label);
        topPanel.add(bottomRightTemperature_T);
        topPanel.add(heatConstant_1_Label);
        topPanel.add(heatConstant_1);
        topPanel.add(heatConstant_2_Label);
        topPanel.add(heatConstant_2);
        topPanel.add(heatConstant_3_Label);
        topPanel.add(heatConstant_3);
        topPanel.add(submitParameters);
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 90));

        // ALLOY REPRESENTATION
        JPanel bottomPanel = createMetalRepresentation();

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.CENTER);
        frame.setSize(1200, 600);
        frame.setVisible(true);
    }

    JPanel createMetalRepresentation() {
        JPanel bottomPanel = new JPanel();
        int width = bottomPanel.getPreferredSize().width;
        int height = bottomPanel.getPreferredSize().height;

        return bottomPanel;
    }

    public static void main(String[] args) {
        HeatVisualizer heatVisualizer = new HeatVisualizer();
        heatVisualizer.setup();
    }

}

