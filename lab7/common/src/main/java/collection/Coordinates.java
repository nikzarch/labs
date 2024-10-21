package collection;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private Double x; //Поле не может быть null
    private int y; //Значение поля должно быть больше -733

    public Coordinates() {
    }

    public Coordinates(Double x, int y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}