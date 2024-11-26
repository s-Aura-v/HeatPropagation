public class MetalCell {
    double heat_constant;
    int numberOfNeighbors;
    int temperature;
    boolean border;

    public MetalCell(double heat_constant) {
        this.heat_constant = heat_constant;
    }

    public double getHeat_constant() {
        return heat_constant;
    }

    public void setHeat_constant(int heat_constant) {
        this.heat_constant = heat_constant;
    }

    public int getNumberOfNeighbors() {
        return numberOfNeighbors;
    }

    public void setNumberOfNeighbors(int numberOfNeighbors) {
        this.numberOfNeighbors = numberOfNeighbors;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return heat_constant + "";
    }
}
