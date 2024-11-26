import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Tester {

    static final double HEATCONSTANT_1 = .75;
    static final double HEATCONSTANT_2 = 1.0;
    static final double HEATCONSTANT_3 = 1.25;
    static int HC1_COUNT;
    static int HC2_COUNT;
    static int HC3_COUNT;
    static final int height = 3;
    static final int width = height * 4;


    public static void main(String[] args) {
        //CONSTANTS
        int topLeftTemperature_S;
        int bottomRightTemperature_T;
//        int dimensionFactor = 9;
        int height = 3;
        int width = height * 4;
        int partitionedWidth = ((width) / 2);
        MetalCell[][] metalAlloy = new MetalCell[height][width];
        MetalCell[][] leftPartition = new MetalCell[height][partitionedWidth];
        MetalCell[][] rightPartition = new MetalCell[height][partitionedWidth];

        applyNoise();
        fillMetalAlloy(metalAlloy);
        splitMetalAlloy(metalAlloy, leftPartition, rightPartition);
    }

    /**
     * Applying a 20% margin of error in the metal count in the array
     */
    static void applyNoise() {
        int totalCells = height * width;
        int base = totalCells / 3;
        HC1_COUNT = ThreadLocalRandom.current().nextInt((int) (base * 0.8), (int) (base * 1.2));
        HC2_COUNT = ThreadLocalRandom.current().nextInt((int) (base * 0.8), (int) (base * 1.2));
        HC3_COUNT = totalCells - HC1_COUNT - HC2_COUNT;
//        System.out.println(HC1_COUNT + " " + HC2_COUNT + " " + HC3_COUNT);
    }

    /**
     * Filling the metal alloy with the metal cells with heat constants. The metal alloys are filled it by column rather than rows.
     * @param metalAlloy - the 2d array of Metal Cells with no instantiated values
     * @return metalAlloy - the 2d array of Metal Cells with instantiated values
     *
     * Visualization of Metal Alloy :
     *   0.75	|  0.75	|  0.75	|  0.75	|  0.75	|  1.0	|  1.0	|  1.0	|  1.0	|  1.25	|  1.25	|  1.25
     *   0.75	|  0.75	|  0.75	|  0.75	|  1.0	|  1.0	|  1.0	|  1.0	|  1.0	|  1.25	|  1.25	|  1.25
     *   0.75	|  0.75	|  0.75	|  0.75	|  1.0	|  1.0	|  1.0	|  1.0	|  1.25	|  1.25	|  1.25	|  1.25
     */
    static MetalCell[][] fillMetalAlloy(MetalCell[][] metalAlloy) {
        int HC1_temp = HC1_COUNT;
        int HC2_temp = HC2_COUNT;
        int HC3_temp = HC3_COUNT;
        for (int columnIndex = 0; columnIndex < metalAlloy[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < metalAlloy.length; rowIndex++) {
                if (HC1_temp != 0) {
                    metalAlloy[rowIndex][columnIndex] = new MetalCell(HEATCONSTANT_1);
                    HC1_temp--;
                } else if (HC1_temp == 0 && HC2_temp != 0) {
                    metalAlloy[rowIndex][columnIndex] = new MetalCell(HEATCONSTANT_2);
                    HC2_temp--;
                } else if (HC1_temp == 0 && HC2_temp == 0 && HC3_temp != 0) {
                    metalAlloy[rowIndex][columnIndex] = new MetalCell(HEATCONSTANT_3);
                    HC3_temp--;
                }
            }
        }
        System.out.println("Representation of Metal Alloy\n" + Arrays.deepToString(metalAlloy)
                .replace("],", "\n").replace(",", "\t| ")
                .replaceAll("[\\[\\]]", " "));
        return metalAlloy;
    }

    /**
     * Partitioning the metalAlloy into 2 halves so it can be worked on in parallel.
     * @param metalAlloy - the 2d array of Metal Cells with instantiated values
     * @param leftPartition - the empty left side of metalAlloy
     * @param rightPartition - the empty right side of metalAlloy
     * No direct output, but a side effect - the left and right partitioned are filled
     */
    static void splitMetalAlloy(MetalCell[][] metalAlloy, MetalCell[][] leftPartition, MetalCell[][] rightPartition) {
        int midpoint = width / 2;
        for (int i = 0; i < leftPartition.length; i++) {
            for (int j = 0; j < leftPartition[0].length; j++) {
                leftPartition[i][j] = metalAlloy[i][j];
                rightPartition[i][j] = metalAlloy[i][j + midpoint];
            }
        }
        System.out.println("Representation of Left Partition\n" + Arrays.deepToString(leftPartition)
                .replace("],", "\n").replace(",", "\t| ")
                .replaceAll("[\\[\\]]", " "));

        System.out.println("Representation of Right Partition\n" + Arrays.deepToString(rightPartition)
                .replace("],", "\n").replace(",", "\t| ")
                .replaceAll("[\\[\\]]", " "));
    }
}
