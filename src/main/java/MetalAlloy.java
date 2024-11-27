import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MetalAlloy extends RecursiveTask<Double> {
    MetalCell[][] metalAlloy;
    MetalCell[][] leftPartition;
    MetalCell[][] rightPartition;
    int topLeftTemperature_S;
    int bottomRightTemperature_T;

    public MetalAlloy(MetalCell[][] leftPartition, MetalCell[][] rightPartition, int topLeftTemperature_S, int bottomRightTemperature_T) {
        this.leftPartition = leftPartition;
        this.rightPartition = rightPartition;
        this.topLeftTemperature_S = topLeftTemperature_S;
        this.bottomRightTemperature_T = bottomRightTemperature_T;
    }

    @Override
    protected Double compute() {
        return 0.0;
    }

    /**
     * Formula: Sum(m=1 to 3) of Cm * (Sum(n=1 to #ofNeighbors) of (temp_n * p_n_m) / #ofNeighbors)
     * <p>
     * m represents each of the three base metals.
     * Cm is the heat constant for metal i.
     * #ofNeighbors is the total number of neighboring regions.
     * temp_n is the temperature of the neighboring region n.
     * p_n_m is the percentage of metal m in neighbor n.
     *
     * @param topLeft
     */
    void heatMetalAlloy(boolean topLeft) {
        double[] listOfTemperatures = new double[3];
//        MetalAlloy rightPartition = new MetalAlloy(leftPartition, leftPartition, topLeftTemperature_S, bottomRightTemperature_T);
//        rightPartition.fork();
//        if (topLeft) {
        leftPartition[0][0].setTemperature(topLeftTemperature_S);
        rightPartition[rightPartition.length - 1][rightPartition[0].length - 1].setTemperature(bottomRightTemperature_T);
            for (int i = 0; i < leftPartition.length; i++) {
                for (int j = 0; j < leftPartition[0].length; j++) {
                    for (int k = 0; k < 3; k++) {
                        double heatConstant_Cm = leftPartition[i][j].getHeatConstantPercentage(i);
                        double surroundingTemperature = (getNeighboringTemperature(i, j, k));
                        int neighborCount_N = getNeighborCount(i, j);
                        listOfTemperatures[k] = (heatConstant_Cm * surroundingTemperature)/neighborCount_N;
                    }
                    leftPartition[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                }
            }
//        } else {
            for (int i = 0; i < this.rightPartition.length; i++) {
                for (int j = 0; j < rightPartition[0].length; j++) {
                    for (int k = 0; k < 3; k++) {
                        double heatConstant_Cm = rightPartition[i][j].getHeatConstantPercentage(i);
                        double surroundingTemperature = (getNeighboringTemperature(i, j, k));
                        int neighborCount_N = getNeighborCount(i, j);
                        listOfTemperatures[k] = (heatConstant_Cm * surroundingTemperature)/neighborCount_N;
                    }
                    rightPartition[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                }
            }
//        }
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
    double getNeighboringTemperature(int x, int y, int metalM) {
        double northEastTemp = 0, northTemp = 0, northWestTemp = 0,
                westTemp = 0, temperature = 0, eastTemp = 0,
                southWestTemp = 0, southTemp = 0, southEastTemp = 0;
        double northEastMetalMPercentage = 0, northMetalMPercentage = 0, northWestMetalMPercentage = 0,
                westMetalMPercentage = 0, eastMetalMPercentage = 0,
                southWestMetalMPercentage = 0, southMetalMPercentage = 0, southEastMetalMPercentage = 0;


        try {
            eastTemp = leftPartition[x + 1][y].getTemperature();
            eastMetalMPercentage = leftPartition[x + 1][y].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southEastTemp = leftPartition[x + 1][y + 1].getTemperature();
            southEastMetalMPercentage = leftPartition[x + 1][y + 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southTemp = leftPartition[x][y + 1].getTemperature();
            southMetalMPercentage = leftPartition[x][y + 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southWestTemp = leftPartition[x - 1][y + 1].getTemperature();
            southWestMetalMPercentage = leftPartition[x - 1][y + 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            westTemp = leftPartition[x - 1][y].getTemperature();
            westMetalMPercentage = leftPartition[x - 1][y].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northWestTemp = leftPartition[x - 1][y - 1].getTemperature();
            northWestMetalMPercentage = leftPartition[x - 1][y - 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northTemp = leftPartition[x][y - 1].getTemperature();
            northMetalMPercentage = leftPartition[x][y - 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northEastTemp = leftPartition[x + 1][y - 1].getTemperature();
            northEastMetalMPercentage = leftPartition[x + 1][y - 1].getHeatConstantPercentage(metalM);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }
        temperature = eastTemp * eastMetalMPercentage +
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
     * @param x - x coordinate of the cell in the 2d array
     * @param y - y coordinate of the cell in the 2d array
     * @return neighborCount
     */
    int getNeighborCount(int x, int y) {
        int neighborCount = 8;
        if (x == 0 || x == leftPartition.length - 1) {
            neighborCount -= 3;
        }
        if (y == 0 || y == leftPartition[0].length - 1) {
            neighborCount -= 3;
        }
        if ((x == 0 && y == 0) || (x == rightPartition.length - 1 && y == rightPartition[0].length - 1)) {
            neighborCount++;
        }
        return neighborCount;
    }
}
