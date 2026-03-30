package org.example.elems;

public class Coordinates {
    private int x; //Значение поля должно быть больше -222
    private Integer y; //Поле не может быть null

    public Coordinates(int x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
