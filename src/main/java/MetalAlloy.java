import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

public class MetalAlloy {
    MetalCell[][] originalMetalAlloy;
    MetalCell[][] finalMetalAlloy;
    MetalCell[][] leftPartition;
    MetalCell[][] rightPartition;
    int topLeftTemperature_S;
    int bottomRightTemperature_T;

    public MetalAlloy(MetalCell[][] metalAlloy, int topLeftTemperature_S, int bottomRightTemperature_T) {
        this.originalMetalAlloy = metalAlloy;
        this.finalMetalAlloy = copyMetalAlloy(metalAlloy);
        this.leftPartition = copyMetalAlloy(metalAlloy);
        this.rightPartition = copyMetalAlloy(metalAlloy);
        this.topLeftTemperature_S = topLeftTemperature_S;
        this.bottomRightTemperature_T = bottomRightTemperature_T;
        debug(true);
    }

    void debug(boolean initial) {
        if (initial) {
            System.out.println("Original Representation of Left Partition\n" + Arrays.deepToString(leftPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
            System.out.println("Original Representation of Right Partition\n" + Arrays.deepToString(rightPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        } else {
            System.out.println("Final Representation of MetalDecomposition.finalMetalAlloy\n" + Arrays.deepToString(MetalDecomposition.finalMetalAlloy)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
            System.out.println("Final Representation of Left Partition\n" + Arrays.deepToString(leftPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
            System.out.println("Final Representation of Right Partition\n" + Arrays.deepToString(rightPartition)
                    .replace("],", "\n").replace(",", "\t| ")
                    .replaceAll("[\\[\\]]", " "));
        }

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
    void heatLeftPartition(int partitionWidth) {
        heatRightPartition(partitionWidth);
        for (int a = 0; a < 5; a++) {
            originalMetalAlloy = copyMetalAlloy(finalMetalAlloy);
            for (int i = 0; i < originalMetalAlloy.length; i++) {
                for (int j = 0; j < partitionWidth; j++) {
                    double[] listOfTemperatures = new double[3];
                    for (int k = 0; k < 3; k++) {
                        // DO NOT CHANGE THE TOP LEFT TEMPERATURE; IT'S CONSTANT BECAUSE IT IS HEATING UP THE REST OF THE METAL
                        if (i == 0 && j == 0) {
                            continue;
                        }
                        // GETTING Cm [HEAT CONSTANT FOR METAL, THERE SHOULD BE THREE BECAUSE THERE ARE THREE METALS INSIDE A CELL]
                        double heatConstant_Cm = originalMetalAlloy[i][j].getHeatConstantValues(k);
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
                    if ((i == 0 && j == 0)) {
                        leftPartition[i][j].setTemperature(topLeftTemperature_S);
                        finalMetalAlloy[i][j].setTemperature(topLeftTemperature_S);
                    } else {
                        leftPartition[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                        finalMetalAlloy[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                    }
                }
            }
        }
        debug(false);
    }

    void heatRightPartition(int partitionWidth) {
        for (int a = 0; a < 5; a++) {
            originalMetalAlloy = copyMetalAlloy(finalMetalAlloy);
            for (int i = originalMetalAlloy.length - 1; i >= 0; i--) {
                for (int j = originalMetalAlloy[0].length - 1; j >= partitionWidth; j--) {
                    double[] listOfTemperatures = new double[3];
                    for (int k = 0; k < 3; k++) {
                        // DO NOT CHANGE THE TOP LEFT TEMPERATURE; IT'S CONSTANT BECAUSE IT IS HEATING UP THE REST OF THE METAL
                        if (i == originalMetalAlloy.length - 1 && j == originalMetalAlloy[0].length - 1) {
                            System.out.println(originalMetalAlloy[i][j].temperature);
                            continue;
                        }
                        // GETTING Cm [HEAT CONSTANT FOR METAL, THERE SHOULD BE THREE BECAUSE THERE ARE THREE METALS INSIDE A CELL]
                        double heatConstant_Cm = originalMetalAlloy[i][j].getHeatConstantValues(k);
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
                    if ((i == rightPartition.length - 1 && j == rightPartition[0].length - 1)) {
                        rightPartition[i][j].setTemperature(bottomRightTemperature_T);
                        finalMetalAlloy[i][j].setTemperature(bottomRightTemperature_T);
                    } else {
                        rightPartition[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                        finalMetalAlloy[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                    }
                }
            }
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
        if ((x == 0 && y == 0) || ((x == partition.length - 1) && (y == partition[0].length - 1))
                || (x == 0 && y == partition[0].length - 1) || (y == 0 && x == partition.length - 1)) {
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

}
