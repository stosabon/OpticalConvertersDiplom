package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by pro on 12.04.2020.
 */
public class TableModel {

    private SimpleStringProperty name;
    private SimpleDoubleProperty distance;
    private SimpleDoubleProperty sensitivity;

    public TableModel(String name, Double distance, Double sensitivity) {
        this.name = new SimpleStringProperty(name);
        this.distance = new SimpleDoubleProperty(distance);
        this.sensitivity = new SimpleDoubleProperty(sensitivity);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Double getDistance() {
        return distance.get();
    }

    public void setDistance(Double distance) {
        this.distance.set(distance);
    }

    public double getSensitivity() {
        return sensitivity.get();
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity.set(sensitivity);
    }
}
