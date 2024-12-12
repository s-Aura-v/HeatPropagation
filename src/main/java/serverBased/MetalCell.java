package serverBased;

import java.io.Serializable;

public class MetalCell implements Serializable {
    public static final int BUFFER_SIZE = Double.BYTES * 8;

    double HC1_PERCENTAGE, HC2_PERCENTAGE, HC3_PERCENTAGE;
    double HC1_CONSTANT, HC2_CONSTANT, HC3_CONSTANT;
    double temperature;

    public MetalCell(double HC1_PERCENTAGE, double HC2_PERCENTAGE, double HC3_PERCENTAGE, double HC1_CONSTANT, double HC2_CONSTANT, double HC3_CONSTANT) {
        this.HC1_PERCENTAGE = HC1_PERCENTAGE;
        this.HC2_PERCENTAGE = HC2_PERCENTAGE;
        this.HC3_PERCENTAGE = HC3_PERCENTAGE;
        this.HC1_CONSTANT = HC1_CONSTANT;
        this.HC2_CONSTANT = HC2_CONSTANT;
        this.HC3_CONSTANT = HC3_CONSTANT;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHC1_PERCENTAGE() {
        return HC1_PERCENTAGE;
    }

    public double getHC2_PERCENTAGE() {
        return HC2_PERCENTAGE;
    }

    public double getHC3_PERCENTAGE() {
        return HC3_PERCENTAGE;
    }

    public double getHC1_CONSTANT() {
        return HC1_CONSTANT;
    }

    public double getHC2_CONSTANT() {
        return HC2_CONSTANT;
    }

    public double getHC3_CONSTANT() {
        return HC3_CONSTANT;
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

    public double getHeatConstantValues(int i) {
        switch (i) {
            case 0:
                return HC1_CONSTANT;
            case 1:
                return HC2_CONSTANT;
            case 2:
                return HC3_CONSTANT;
        }
        return 0;
    }

    @Override
    public String toString() {
        return temperature + "";
    }
}
