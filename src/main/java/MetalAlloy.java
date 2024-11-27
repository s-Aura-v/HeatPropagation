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

    void heatMetalAlloy(boolean topLeft) {
        MetalAlloy rightPartition = new MetalAlloy(leftPartition, leftPartition, topLeftTemperature_S, bottomRightTemperature_T);
        rightPartition.fork();
        if (topLeft) {
            for (int i = 0; i < leftPartition.length; i++) {
                for (int j = 0; j < leftPartition[0].length; j++) {
                    double surroundingTemperature_tempN = (getTemperature(i, j));
//                    double heatConstant_Cm = leftPartition[i][j].getHeat_constant();
//                    double percentageOf
                }
            }

        } else {

        }
    }

    /**
     * Calculate the temperature of the neighbors of the given cell
     * @param x - x coordinate of the cell in the 2d array
     * @param y - y coordinate of the cell in the 2d array
     * @return temperature - total temperature
     */
    double getTemperature(int x, int y) {
        double northEastTemp = 0, northTemp = 0, northWestTemp = 0,
                westTemp = 0, temperature = 0, eastTemp = 0,
                southWestTemp = 0, southTemp = 0, southEastTemp = 0;

        try {
            eastTemp = leftPartition[x + 1][y].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southEastTemp = leftPartition[x + 1][y + 1].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southTemp = leftPartition[x][y + 1].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            southWestTemp = leftPartition[x - 1][y + 1].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            westTemp = leftPartition[x - 1][y].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northWestTemp = leftPartition[x - 1][y - 1].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northTemp = leftPartition[x][y - 1].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }

        try {
            northEastTemp = leftPartition[x + 1][y - 1].getTemperature();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Move on
        }
        temperature = eastTemp + southEastTemp + southTemp + southWestTemp + westTemp + northWestTemp + northTemp + northEastTemp;
        return temperature;
    }


}
