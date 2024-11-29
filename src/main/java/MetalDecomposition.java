import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MetalDecomposition {

    static final boolean debug = false;

    static final double HEATCONSTANT_1 = .75;
    static final double HEATCONSTANT_2 = 1.0;
    static final double HEATCONSTANT_3 = 1.25;
    static int HC1_PERCENTAGE;
    static int HC2_PERCENTAGE;
    static int HC3_PERCENTAGE;
    static final int height = 6;
    static final int width = height * 4;
    static final int topLeftTemperature_S = 100;
    static final int bottomRightTemperature_T = 100;
    static final int METAL_PERCENTAGE = 33;


    public static void main(String[] args) {
        //CONSTANTS
//        int dimensionFactor = 9;
        int partitionedWidth = ((width) / 2);
        MetalCell[][] metalAlloy = new MetalCell[height][width];
        MetalCell[][] leftPartition = new MetalCell[height][partitionedWidth];
        MetalCell[][] rightPartition = new MetalCell[height][partitionedWidth];

        fillMetalAlloy(metalAlloy);
        splitMetalAlloy(metalAlloy, leftPartition, rightPartition);

        MetalAlloy alloy = new MetalAlloy(leftPartition, rightPartition, topLeftTemperature_S, bottomRightTemperature_T);
        alloy.heatMetalAlloy(true);

        if (true) {
            System.out.println("Representation of Left Partition\n" + Arrays.deepToString(leftPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));

            System.out.println("Representation of Right Partition\n" + Arrays.deepToString(rightPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        }
    }

    /**
     * Applying a 20% margin of error in the metal count in the array
     */
    static void calculateHeatConstantProportion() {
        HC1_PERCENTAGE = ThreadLocalRandom.current().nextInt((int) (METAL_PERCENTAGE * 0.8), (int) (METAL_PERCENTAGE * 1.2));
        HC2_PERCENTAGE = ThreadLocalRandom.current().nextInt((int) (METAL_PERCENTAGE * 0.8), (int) (METAL_PERCENTAGE * 1.2));
        HC3_PERCENTAGE = 100 - HC1_PERCENTAGE - HC2_PERCENTAGE;
    }


    /**
     * Filling the metal alloy with the metal cells composed of 3 metals with unique heat constant.
     * The values represented are PERCENT OF METAL in a cell, not HEAT CONSTANT itself.
     * @param metalAlloy - the 2d array of Metal Cells with no instantiated values
     * @return metalAlloy - the 2d array of Metal Cells with instantiated values
     * <p>
     * Visualization of Metal Alloy :
     * [HeatConstant1% ;; HeatConstant2% ;; HeatConstant3%]
        30;;26;;44	|  34;;29;;37	|  34;;34;;32	|  34;;32;;34	|  33;;37;;30	|  35;;26;;39	|  28;;26;;46	|  27;;37;;36
        36;;35;;29	|  35;;34;;31	|  35;;27;;38	|  29;;37;;34	|  26;;36;;38	|  29;;28;;43	|  29;;32;;39	|  28;;34;;38
        35;;38;;27	|  27;;28;;45	|  37;;35;;28	|  36;;34;;30	|  33;;34;;33	|  32;;33;;35	|  38;;35;;27	|  32;;36;;32
        35;;38;;27	|  36;;33;;31	|  27;;38;;35	|  27;;31;;42	|  33;;36;;31	|  30;;35;;35	|  37;;28;;35	|  28;;38;;34
     */
    static MetalCell[][] fillMetalAlloy(MetalCell[][] metalAlloy) {
        for (int columnIndex = 0; columnIndex < metalAlloy[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < metalAlloy.length; rowIndex++) {
                calculateHeatConstantProportion();
                metalAlloy[rowIndex][columnIndex] = new MetalCell(HC1_PERCENTAGE, HC2_PERCENTAGE, HC3_PERCENTAGE);
                metalAlloy[rowIndex][columnIndex].setTemperature(0);
            }
        }
        if (debug) {
            System.out.println("Representation of Metal Alloy\n" + Arrays.deepToString(metalAlloy)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        }
        return metalAlloy;
    }

    /**
     * Partitioning the metalAlloy into 2 halves so it can be worked on in parallel.
     *
     * @param metalAlloy     - the 2d array of Metal Cells with instantiated values
     * @param leftPartition  - the empty left side of metalAlloy
     * @param rightPartition - the empty right side of metalAlloy
     *                       No direct output, but a side effect - the left and right partitioned are filled
     */
    static void splitMetalAlloy(MetalCell[][] metalAlloy, MetalCell[][] leftPartition, MetalCell[][] rightPartition) {
        int midpoint = width / 2;
        for (int i = 0; i < leftPartition.length; i++) {
            for (int j = 0; j < leftPartition[0].length; j++) {
                leftPartition[i][j] = metalAlloy[i][j];
                rightPartition[i][j] = metalAlloy[i][j + midpoint];
            }
        }
        if (debug) {
            System.out.println("Representation of Left Partition\n" + Arrays.deepToString(leftPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));

            System.out.println("Representation of Right Partition\n" + Arrays.deepToString(rightPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        }
    }
}
