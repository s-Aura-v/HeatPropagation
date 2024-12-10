package localmachine;

import serverBased.HeatVisualizer;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MetalDecomposition {

    public static final boolean debug = true;
    static final double HEATCONSTANT_1 = .75;
    static final double HEATCONSTANT_2 = 1.0;
    static final double HEATCONSTANT_3 = 1.25;
    static double HC1_PERCENTAGE;
    static double HC2_PERCENTAGE;
    static double HC3_PERCENTAGE;
    static final int height = 4;
    static final int width = height * 4;
    static final int topLeftTemperature_S = 100;
    static final int bottomRightTemperature_T = 100;
    static final double METAL_PERCENTAGE = .33;

    //REAL VARIABLES
    static MetalCell[][] finalMetalAlloy = new MetalCell[height][width];

    public static void main(String[] args) {
        System.out.println("HEAT_DECOMPOSITION");
        MetalCell[][] metalAlloy = new MetalCell[height][width];
        fillMetalAlloy(metalAlloy);
        MetalAlloy alloy = new MetalAlloy(metalAlloy, topLeftTemperature_S, bottomRightTemperature_T);
        alloy.compute();
    }

    /**
     * A deep copy of MetalAlloy that creates new references to MetalCell.
     *
     * @param metalAlloy - the 2d array to be copied
     * @return copiedMetalAlloy - the copy of metalAlloy with new references
     */
    static MetalCell[][] copyMetalAlloy(MetalCell[][] metalAlloy) {
        MetalCell[][] copiedMetalAlloy = new MetalCell[metalAlloy.length][metalAlloy[0].length];
        for (int i = 0; i < copiedMetalAlloy.length; i++) {
            for (int j = 0; j < copiedMetalAlloy[i].length; j++) {
                copiedMetalAlloy[i][j] = new MetalCell(
                        metalAlloy[i][j].getHC1_PERCENTAGE(),
                        metalAlloy[i][j].getHC2_PERCENTAGE(),
                        metalAlloy[i][j].getHC3_PERCENTAGE(),
                        metalAlloy[i][j].getHC1_CONSTANT(),
                        metalAlloy[i][j].getHC2_CONSTANT(),
                        metalAlloy[i][j].getHC3_CONSTANT());
                copiedMetalAlloy[i][j].setTemperature(metalAlloy[i][j].getTemperature());
            }
        }
        return copiedMetalAlloy;
    }

    /**
     * Applying a 20% margin of error in the metal count in the array
     */
    static void calculateHeatConstantProportion() {
//        int metalPercentage = (int) (METAL_PERCENTAGE * 100);
//        int hc1_temp = ThreadLocalRandom.current().nextInt((int) (metalPercentage * 0.8), (int) ( metalPercentage * 1.2));
//        int hc2_temp = ThreadLocalRandom.current().nextInt((int) (metalPercentage * 0.8), (int) (metalPercentage * 1.2));
//        int hc3_temp = 100 - hc1_temp - hc2_temp;
//
//        HC1_PERCENTAGE = (double) hc1_temp;
//        HC2_PERCENTAGE = (double) hc2_temp;
//        HC3_PERCENTAGE = (double) hc3_temp;
        HC1_PERCENTAGE = .34;
        HC2_PERCENTAGE = .33;
        HC3_PERCENTAGE = .33;
    }

    /**
     * Filling the metal alloy with the metal cells composed of 3 metals with unique heat constant.
     * The values represented are PERCENT OF METAL in a cell, not HEAT CONSTANT itself.
     *
     * @param metalAlloy - the 2d array of Metal Cells with no instantiated values
     * @return metalAlloy - the 2d array of Metal Cells with instantiated values
     * <p>
     * Visualization of Metal Alloy :
     * [HeatConstant1% ;; HeatConstant2% ;; HeatConstant3%]
     * 30;;26;;44	|  34;;29;;37	|  34;;34;;32	|  34;;32;;34	|  33;;37;;30	|  35;;26;;39	|  28;;26;;46	|  27;;37;;36
     * 36;;35;;29	|  35;;34;;31	|  35;;27;;38	|  29;;37;;34	|  26;;36;;38	|  29;;28;;43	|  29;;32;;39	|  28;;34;;38
     * 35;;38;;27	|  27;;28;;45	|  37;;35;;28	|  36;;34;;30	|  33;;34;;33	|  32;;33;;35	|  38;;35;;27	|  32;;36;;32
     * 35;;38;;27	|  36;;33;;31	|  27;;38;;35	|  27;;31;;42	|  33;;36;;31	|  30;;35;;35	|  37;;28;;35	|  28;;38;;34
     */
    static MetalCell[][] fillMetalAlloy(MetalCell[][] metalAlloy) {
        for (int columnIndex = 0; columnIndex < metalAlloy[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < metalAlloy.length; rowIndex++) {
                calculateHeatConstantProportion();
                metalAlloy[rowIndex][columnIndex] = new MetalCell(HC1_PERCENTAGE, HC2_PERCENTAGE, HC3_PERCENTAGE, HEATCONSTANT_1, HEATCONSTANT_2, HEATCONSTANT_3);
                metalAlloy[rowIndex][columnIndex].setTemperature(0);
                finalMetalAlloy[rowIndex][columnIndex] = new MetalCell(HC1_PERCENTAGE, HC2_PERCENTAGE, HC3_PERCENTAGE, HEATCONSTANT_1, HEATCONSTANT_2, HEATCONSTANT_3);
                finalMetalAlloy[rowIndex][columnIndex].setTemperature(0);
            }
        }
        metalAlloy[0][0].setTemperature(topLeftTemperature_S);
        metalAlloy[metalAlloy.length - 1][metalAlloy[0].length - 1].setTemperature(bottomRightTemperature_T);
        finalMetalAlloy[0][0].setTemperature(topLeftTemperature_S);
        finalMetalAlloy[metalAlloy.length - 1][metalAlloy[0].length - 1].setTemperature(bottomRightTemperature_T);
        if (debug) {
            System.out.println("Original Metal Alloy\n" + Arrays.deepToString(metalAlloy)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        }
        return metalAlloy;
    }
}
