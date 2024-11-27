import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class MetalAlloy {
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

    void heatMetalAlloy(boolean topLeft) {
        ExecutorService executorService = new ForkJoinPool();
        if (topLeft) {
            for (int i = 0; i < leftPartition.length; i++) {
                for (int j = 0; j < leftPartition[0].length; j++) {
//                    leftPartition[i][j].setTemperature(topLeftTemperature_S);
                    System.out.println(calculateTemperature(i, j));
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
    double calculateTemperature(int x, int y) {
        double temperature = 0;
        if (x - 1 < 0 || y - 1 < 0 || x + 1 >= leftPartition.length || y + 1 >= rightPartition[0].length) {
            // Do Nothing
        }
        double eastTemp = 0, southEastTemp = 0, southTemp = 0, southWestTemp = 0, westTemp = 0, northWestTemp = 0, northTemp = 0, northEastTemp = 0;
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
