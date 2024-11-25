public class MetalAlloy implements Runnable {
//    int dimensionFactor = 8;
    int height;
    int width;
    MetalCell[][] metalAlloy;
    // dimension of the neighbors :)

    public MetalAlloy(int height) {
        this.height = height;
        this.width = 4*height;
        metalAlloy = new MetalCell[height][width];
    }

    void heatMetal(int topLeftTemperature, int topRightTemperature) {
        int partitionedWidth = ((width)/2);
        MetalCell[][] leftPartition = new MetalCell[height][partitionedWidth];
        MetalCell[][] rightPartition = new MetalCell[height][partitionedWidth];
    }

    public static void main(String[] args) {
        MetalAlloy metalAlloy = new MetalAlloy(3);
        metalAlloy.metalAlloy[1][1].getTemperature();
    }

    @Override
    public void run() {

    }
}
