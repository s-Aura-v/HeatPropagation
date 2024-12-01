import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

public class MetalAlloy extends RecursiveTask<Double> {
    MetalCell[][] originalMetalAlloy;
    MetalCell[][] editedMetalAlloy;
    int topLeftTemperature_S;
    int bottomRightTemperature_T;

    public MetalAlloy(MetalCell[][] metalAlloy, int topLeftTemperature_S, int bottomRightTemperature_T) {
        this.originalMetalAlloy = metalAlloy;
        this.editedMetalAlloy = copyMetalAlloy(metalAlloy);
        this.topLeftTemperature_S = topLeftTemperature_S;
        this.bottomRightTemperature_T = bottomRightTemperature_T;
    }

    /**
     * Creates a fork that sends to heat the right partition, then heats left partition itself.
     * Part of RecursiveTask<Double>
     */
    @Override
    protected Double compute() {
        if (MetalDecomposition.LEFT_COMPUTED == 0) {
            MetalDecomposition.LEFT_COMPUTED++;
            originalMetalAlloy[0][0].setTemperature(topLeftTemperature_S);
            originalMetalAlloy[originalMetalAlloy.length - 1][originalMetalAlloy[0].length - 1].setTemperature(bottomRightTemperature_T);
            MetalAlloy rightPartitionFork = new MetalAlloy(originalMetalAlloy, topLeftTemperature_S, bottomRightTemperature_T);
            rightPartitionFork.fork();
            heatMetalAlloy(true);
        } else {
            heatMetalAlloy(false);
        }
        return 0.0;
    }

    /**
     * Heats the metal alloy
     * <p>
     * Explanation:
     * Formula: Sum(m=1 to 3) of Cm * (Sum(n=1 to #ofNeighbors) of (temp_n * p_n_m) / #ofNeighbors)
     * LEFT SIDE OF THE FORMULA: (Sum(n=1 to #ofNeighbors) of (temp_n * p_n_m) / #ofNeighbors)
     * I bloated the formula with a comments because the formula can get confusing fast.
     * <p>
     * m represents each of the three base metals.
     * Cm is the heat constant for metal i.
     * #ofNeighbors is the total number of neighboring regions.
     * temp_n is the temperature of the neighboring region n.
     * p_n_m is the percentage of metal m in neighbor n.
     */
    void heatMetalAlloy(boolean isLeftPartition) {
//        for (int a = 0; a < 20; a++) {
        if (isLeftPartition) {
            // LEFT PARTITION - START FROM TOP LEFT AND MAKE YOUR WAY DOWN
            originalMetalAlloy = copyMetalAlloy(editedMetalAlloy);
            for (int i = 0; i < originalMetalAlloy.length; i++) {
                for (int j = 0; j < originalMetalAlloy[0].length; j++) {
                    double[] listOfTemperatures = new double[3];
                    for (int k = 0; k < 3; k++) {
                        // GETTING Cm [HEAT CONSTANT FOR METAL, THERE SHOULD BE THREE BECAUSE THERE ARE THREE METALS INSIDE A CELL]
                        double heatConstant_Cm = originalMetalAlloy[i][j].getHeatConstantPercentage(k);
                        // GETTING TempN * P_N_M [THE TEMP OF THE NEIGHBORS * THE PERCENT OF METAL IN THE NEIGHBOR ]
                        double surroundingTemperature_tempN = (getNeighboringTemperature(i, j, k, originalMetalAlloy));
                        // GETTING #ofNeighbors
                        int neighborCount_N = getNeighborCount(i, j, originalMetalAlloy);
                        // SOLVING THE LEFT SIDE OF THE EQUATION
                        double leftSideOfTheEquation = surroundingTemperature_tempN / neighborCount_N;
                        // SOLVING THE ENTIRE EQUATION (EXCLUDING SUMMATION)
                        double temp = heatConstant_Cm * leftSideOfTheEquation;
                        listOfTemperatures[k] = temp;
                    }
                    // ADDING THE SUMMATION TO THE CELL
                    editedMetalAlloy[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                }
            }
        } else {
            // RIGHT PARTITION - START FROM BOTTOM RIGHT AND MAKE YOUR WAY UP
            originalMetalAlloy = copyMetalAlloy(editedMetalAlloy);
            for (int i = originalMetalAlloy.length - 1; i >= 0; i--) {
                for (int j = originalMetalAlloy[0].length - 1; j >= 0; j--) {
                    double[] listOfTemperatures = new double[3];
                    for (int k = 0; k < 3; k++) {
                        // GETTING Cm [HEAT CONSTANT FOR METAL, THERE SHOULD BE THREE BECAUSE THERE ARE THREE METALS INSIDE A CELL]
                        double heatConstant_Cm = originalMetalAlloy[i][j].getHeatConstantPercentage(k);
                        // GETTING TempN * P_N_M [THE TEMP OF THE NEIGHBORS * THE PERCENT OF METAL IN THE NEIGHBOR ]
                        double surroundingTemperature_tempN = (getNeighboringTemperature(i, j, k, originalMetalAlloy));
                        // GETTING #ofNeighbors
                        int neighborCount_N = getNeighborCount(i, j, originalMetalAlloy);
                        // SOLVING THE LEFT SIDE OF THE EQUATION
                        double leftSideOfTheEquation = surroundingTemperature_tempN / neighborCount_N;
                        // SOLVING THE ENTIRE EQUATION (EXCLUDING SUMMATION)
                        double temp = heatConstant_Cm * leftSideOfTheEquation;
                        listOfTemperatures[k] = temp;
                    }
                    // ADDING THE SUMMATION TO THE CELL
                    editedMetalAlloy[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                }
            }
        }
        if (true) {
            System.out.println("Final Representation of Edited Metal Partition\n" + Arrays.deepToString(editedMetalAlloy)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        }
    }

    /**
     * Calculates Sum(n=1 to #ofNeighbors) of (temp_n * p_n_m)
     * Or, calculates the temperature of the neighbors with respect to metal M's heat constant
     *
     * @param x      - x coordinate of the cell in the 2d array
     * @param y      - y coordinate of the cell in the 2d array
     * @param metalM - metal which we are calculating the temperature for
     * @return temperature
     */
    double getNeighboringTemperature(int x, int y, int metalM, MetalCell[][] partition) {
        double northEastTemp = 0, northTemp = 0, northWestTemp = 0,
                westTemp = 0, temperature = 0, eastTemp = 0,
                southWestTemp = 0, southTemp = 0, southEastTemp = 0;
        double northEastMetalMPercentage = 0, northMetalMPercentage = 0, northWestMetalMPercentage = 0,
                westMetalMPercentage = 0, eastMetalMPercentage = 0,
                southWestMetalMPercentage = 0, southMetalMPercentage = 0, southEastMetalMPercentage = 0;


        try {
            eastTemp = partition[x + 1][y].getTemperature();
            eastMetalMPercentage = partition[x + 1][y].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southEastTemp = partition[x + 1][y + 1].getTemperature();
            southEastMetalMPercentage = partition[x + 1][y + 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southTemp = partition[x][y + 1].getTemperature();
            southMetalMPercentage = partition[x][y + 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southWestTemp = partition[x - 1][y + 1].getTemperature();
            southWestMetalMPercentage = partition[x - 1][y + 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            westTemp = partition[x - 1][y].getTemperature();
            westMetalMPercentage = partition[x - 1][y].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northWestTemp = partition[x - 1][y - 1].getTemperature();
            northWestMetalMPercentage = partition[x - 1][y - 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northTemp = partition[x][y - 1].getTemperature();
            northMetalMPercentage = partition[x][y - 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northEastTemp = partition[x + 1][y - 1].getTemperature();
            northEastMetalMPercentage = partition[x + 1][y - 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }
        // THIS IS A FAKE FIX. SOMEHOW SIMULATE HEATING UP METAL PLEASE.
        // FIX: NO IDEA.
        if (x == 0 && y == 0 || x == partition.length - 1 && y == partition[0].length - 1) {
            return partition[x][y].getTemperature();
        }

        temperature =
                eastTemp * eastMetalMPercentage +
                        southEastTemp * southEastMetalMPercentage +
                        southTemp * southMetalMPercentage +
                        southWestTemp * southWestMetalMPercentage +
                        westTemp * westMetalMPercentage +
                        northWestTemp * northWestMetalMPercentage +
                        northTemp * northMetalMPercentage +
                        northEastTemp * northEastMetalMPercentage;
        return temperature;
    }

    /**
     * Get the amount of neighbors a given integer has
     *
     * @param x - x coordinate of the cell in the 2d array
     * @param y - y coordinate of the cell in the 2d array
     * @return neighborCount
     */
    int getNeighborCount(int x, int y, MetalCell[][] partition) {
        int neighborCount = 8;
        if (x == 0 || x == partition.length - 1) {
            neighborCount -= 3;
        }
        if (y == 0 || y == partition[0].length - 1) {
            neighborCount -= 3;
        }
        if ((x == 0 && y == 0) || ((x == partition.length - 1) && (y == partition[0].length - 1))) {
            neighborCount++;
        }
        return neighborCount;
    }

    /**
     * A deep copy of MetalAlloy that creates new references to MetalCell.
     *
     * @param metalAlloy - the 2d array to be copied
     * @return copiedMetalAlloy - the copy of metalAlloy with new references
     */
    MetalCell[][] copyMetalAlloy(MetalCell[][] metalAlloy) {
        MetalCell[][] copiedMetalAlloy = Arrays.copyOf(metalAlloy, metalAlloy.length);
        for (int i = 0; i < copiedMetalAlloy.length; i++) {
            for (int j = 0; j < copiedMetalAlloy[i].length; j++) {
                copiedMetalAlloy[i][j] = new MetalCell(
                        metalAlloy[i][j].getHC1_PERCENTAGE(),
                        metalAlloy[i][j].getHC2_PERCENTAGE(),
                        metalAlloy[i][j].getHC3_PERCENTAGE());
                copiedMetalAlloy[i][j].setTemperature(metalAlloy[i][j].getTemperature());
            }
        }
        return copiedMetalAlloy;
    }
}
