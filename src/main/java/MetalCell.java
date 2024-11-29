public class MetalCell {
    double HC1_PERCENTAGE, HC2_PERCENTAGE, HC3_PERCENTAGE;
    double temperature;
    boolean border;

    public MetalCell(double HC1_PERCENTAGE, double HC2_PERCENTAGE, double HC3_PERCENTAGE) {
        this.HC1_PERCENTAGE = HC1_PERCENTAGE/100;
        this.HC2_PERCENTAGE = HC2_PERCENTAGE/100;
        this.HC3_PERCENTAGE = HC3_PERCENTAGE/100;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHeatConstantPercentage(int i) {
        switch (i) {
            case 0:
                return HC1_PERCENTAGE;
            case 1:
                return HC2_PERCENTAGE;
            case 2:
                return HC3_PERCENTAGE;
        }
        return 0;
    }

    @Override
    public String toString() {
//        return "" + HC1_PERCENTAGE + ";;" + HC2_PERCENTAGE + ";;" + HC3_PERCENTAGE;
        return temperature + "";
    }
}
