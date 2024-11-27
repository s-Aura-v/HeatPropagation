public class MetalCell {
    double heat_constant;
    double temperature;
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }


    @Override
    public String toString() {
        return heat_constant + "";
    }
}
