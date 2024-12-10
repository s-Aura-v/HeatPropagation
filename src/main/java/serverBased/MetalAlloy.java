package serverBased;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;

public class MetalAlloy implements Serializable {
    MetalCell[][] originalMetalAlloy;
    MetalCell[][] editedMetalAlloy;
    double topLeftTemperature_S;
    double bottomRightTemperature_T;
    int partitionWidth;
    boolean shouldComputeLeft;
    double[] edges;

    public MetalAlloy(MetalCell[][] metalAlloy, double topLeftTemperature_S, double bottomRightTemperature_T, boolean shouldComputeLeft) {
        this.originalMetalAlloy = metalAlloy;
        this.editedMetalAlloy = copyMetalAlloy(metalAlloy);
        this.topLeftTemperature_S = topLeftTemperature_S;
        this.bottomRightTemperature_T = bottomRightTemperature_T;
        this.shouldComputeLeft = shouldComputeLeft;
        this.partitionWidth = metalAlloy[0].length / 2;
        this.edges = new double[originalMetalAlloy.length];
    }

    /**
     * Used to calculate the left partition locally.
     * @return MetalCell[][] - Sent back to the main method to be merged with RightPartition
     */
    public MetalCell[][] callLeftPartition() {
        MetalCell[][] metalAlloy = new MetalCell[0][0];
        metalAlloy = heatLeftPartition(copyMetalAlloy(originalMetalAlloy));
        return metalAlloy;
    }

    /**
     * Used to calculate the right partition remotely through a server.
     * @return MetalCell[][] - Sent back to the main method to be merged with Right Partition
     */
    public MetalCell[][] callRightPartition() {
        MetalCell[][] metalAlloy;
        metalAlloy = heatRightPartition(copyMetalAlloy(originalMetalAlloy));
        return metalAlloy;
    }

    /**
     * Sends the right partition to the server so it can heat it up, which is sent back locally, to be merged with Left Partition.
     * @return MetalCell[][] - Sent back to the main method to be merged with Left Partition
     */
    public MetalCell[][] callServer() {
        MetalCell[][] serverFinalMetal = new MetalCell[0][0];
        edges = new double[originalMetalAlloy.length];
        try {
            Socket socket = new Socket(MetalDecomposition.SERVER_HOST, 1998);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            // DURING THE FIRST ITERATION, SEND THE ENTIRE OBJECT SO IT CAN CONSTRUCT THE METALALLOY
            if (edges == null) {
                outputStream.writeObject(editedMetalAlloy);
            }
            // AFTERWARDS, ONLY SEND THE EDGES
            getEdges(false);
            outputStream.writeObject(edges);
//            System.out.println("Object Written to Server");

            serverFinalMetal = (MetalCell[][]) inputStream.readObject();
//            System.out.println("Waiting for Server Output");

            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return serverFinalMetal;
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
    MetalCell[][] heatLeftPartition(MetalCell[][] metalAlloyCopy) {
        for (int i = 0; i < metalAlloyCopy.length; i++) {
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
                    editedMetalAlloy[i][j].setTemperature(topLeftTemperature_S);
                } else {
                    editedMetalAlloy[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                }
                saveEdges(true);
            }
        }
        return editedMetalAlloy;
    }

    /**
     * Heat Right Partition - Bottom right to center
     * Same logic as the method above [heatLeftPartition]
     */
    MetalCell[][] heatRightPartition(MetalCell[][] rightPartitionCopy) {
        for (int i = originalMetalAlloy.length - 1; i >= 0; i--) {
            for (int j = originalMetalAlloy[0].length - 1; j >= partitionWidth; j--) {
                double[] listOfTemperatures = new double[3];
                for (int k = 0; k < 3; k++) {
                    // DO NOT CHANGE THE TOP LEFT TEMPERATURE; IT'S CONSTANT BECAUSE IT IS HEATING UP THE REST OF THE METAL
                    if (i == originalMetalAlloy.length - 1 && j == originalMetalAlloy[0].length - 1) {
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
                if ((i == rightPartitionCopy.length - 1 && j == rightPartitionCopy[0].length - 1)) {
                    rightPartitionCopy[i][j].setTemperature(bottomRightTemperature_T);
                } else {
                    rightPartitionCopy[i][j].setTemperature(Arrays.stream(listOfTemperatures).sum());
                }
                saveEdges(false);
            }
        }
        return rightPartitionCopy;
    }

    /**
     * Fetch the edges so that it can be sent to the server and retrieved from the server
     *
     * @param shouldComputeLeft - if true, grab left partition edges; else grab right partition edges
     */
    void saveEdges(boolean shouldComputeLeft) {
        if (shouldComputeLeft) {
            int x = partitionWidth - 1;
            for (int y = 0; y < originalMetalAlloy.length; y++) {
                edges[y] = editedMetalAlloy[y][x].getTemperature();
            }
        } else {
            for (int y = 0; y < originalMetalAlloy.length; y++) {
                edges[y] = editedMetalAlloy[y][partitionWidth].getTemperature();
            }
        }
    }

    /** Once the edges have been fetched, add it to the correct partition so that it can calculate the temperature correctly
     *
     * @param shouldComputeLeft - if true, merge edges with left partition locally; else merge edges right partition in server
     */
    void getEdges(boolean shouldComputeLeft) {
        if (shouldComputeLeft) {
            int x = partitionWidth - 1;
            for (int y = 0; y < originalMetalAlloy.length; y++) {
                editedMetalAlloy[y][x].setTemperature(edges[y]);
            }
        } else {
            for (int y = 0; y < originalMetalAlloy.length; y++) {
                editedMetalAlloy[y][partitionWidth].setTemperature(edges[y]);
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

    /**
     * Merge left and right partition into one merged partition.
     */
    MetalCell[][] mergePartitions(MetalCell[][] leftPartition, MetalCell[][] rightPartition) {
        MetalCell[][] merged = new MetalCell[leftPartition.length][leftPartition[0].length];
        int midpoint = leftPartition[0].length / 2;
        for (int i = 0; i < leftPartition.length; i++) {
            for (int j = 0; j < midpoint; j++) {
                merged[i][j] = leftPartition[i][j];
                merged[i][j + midpoint] = rightPartition[i][j + midpoint];
            }
        }
        return merged;
    }


}
