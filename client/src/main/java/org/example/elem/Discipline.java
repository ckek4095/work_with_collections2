package org.example.elem;

public class Discipline {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private int labsCount;

    public Discipline(String name, int labsCount) {
        this.name = name;
        this.labsCount = labsCount;
    }

    public String getName() {
        return name;
    }

    public int getLabsCount() {
        return labsCount;
    }

    public void setLabsCount(int labsCount) {
        this.labsCount = labsCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
