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
        MetalCell[][] metalAlloy = new MetalCell[height][width];
        int partitionedWidth = ((width) / 2);
        MetalCell[][] leftPartition = new MetalCell[height][partitionedWidth];
        MetalCell[][] rightPartition = new MetalCell[height][partitionedWidth];

        applyNoise();
        fillMetalAlloy(metalAlloy);
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
        System.out.println(Arrays.deepToString(metalAlloy)
                .replace("],", "\n").replace(",", "\t| ")
                .replaceAll("[\\[\\]]", " "));
        return metalAlloy;
    }
}
