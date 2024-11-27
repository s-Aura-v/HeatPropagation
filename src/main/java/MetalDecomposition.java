import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MetalDecomposition {

    static final boolean debug = true;

    static final double HEATCONSTANT_1 = .75;
    static final double HEATCONSTANT_2 = 1.0;
    static final double HEATCONSTANT_3 = 1.25;
    static int HC1_PERCENTAGE;
    static int HC2_PERCENTAGE;
    static int HC3_PERCENTAGE;
    static final int height = 6;
    static final int width = height * 4;
    static final int topLeftTemperature_S = 30;
    static final int bottomRightTemperature_T = 28;
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
     * Filling the metal alloy with the metal cells with heat constants. The metal alloys are filled it by column rather than rows.
     *
     * @param metalAlloy - the 2d array of Metal Cells with no instantiated values
     * @return metalAlloy - the 2d array of Metal Cells with instantiated values
     * <p>
     * Visualization of Metal Alloy :
     * 0.75	|  0.75	|  0.75	|  0.75	|  0.75	|  1.0	|  1.0	|  1.0	|  1.0	|  1.25	|  1.25	|  1.25
     * 0.75	|  0.75	|  0.75	|  0.75	|  1.0	|  1.0	|  1.0	|  1.0	|  1.0	|  1.25	|  1.25	|  1.25
     * 0.75	|  0.75	|  0.75	|  0.75	|  1.0	|  1.0	|  1.0	|  1.0	|  1.25	|  1.25	|  1.25	|  1.25
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
