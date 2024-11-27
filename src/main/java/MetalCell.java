public class MetalCell {
    int HC1_PERCENTAGE, HC2_PERCENTAGE, HC3_PERCENTAGE;
    double temperature;
    boolean border;

    public MetalCell(int HC1_PERCENTAGE, int HC2_PERCENTAGE, int HC3_PERCENTAGE) {
        this.HC1_PERCENTAGE = HC1_PERCENTAGE;
        this.HC2_PERCENTAGE = HC2_PERCENTAGE;
        this.HC3_PERCENTAGE = HC3_PERCENTAGE;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "" + HC1_PERCENTAGE + ";;" + HC2_PERCENTAGE + ";;" + HC3_PERCENTAGE;
    }
}
