package org.example.elem;

/**
 * Класс LabWork, описывающий элемент сета
 */

public class LabWork {

    private String id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float minimalPoint; //Поле может быть null, Значение поля должно быть больше 0
    private Difficulty difficulty; //Поле может быть null
    private Discipline discipline; //Поле не может быть null

    public LabWork() {
        this.id = "0";
        this.name = "";
        this.coordinates = new Coordinates(0, null);
        this.creationDate = null;
        this.minimalPoint = -1F;
        this.difficulty = Difficulty.EASY;
        this.discipline = new Discipline("", -1);
    }

    public LabWork(LabWork lab) {
        this.id = lab.getId();
        this.name = lab.getName();
        this.coordinates = lab.getCoordinates();
        this.creationDate = lab.getCreationDate();
        this.minimalPoint = lab.getMinimalPoint();
        this.difficulty = lab.getDifficulty();
        this.discipline = lab.getDiscipline();
    }

    public LabWork(String id, String name, Coordinates coordinates, java.time.LocalDateTime creationDate, Float minimalPoint, Difficulty difficulty, Discipline discipline) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.difficulty = difficulty;
        this.discipline = discipline;

        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут отсутствовать");
        }
        if (creationDate == null) {
            this.creationDate = java.time.LocalDateTime.now();
        }
    }

    public void update(LabWork lab) {
        this.id = lab.getId();
        this.name = lab.getName();
        this.coordinates = lab.getCoordinates();
        this.creationDate = lab.getCreationDate();
        this.minimalPoint = lab.getMinimalPoint();
        this.difficulty = lab.getDifficulty();
        this.discipline = lab.getDiscipline();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.time.LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Float getMinimalPoint() {
        return minimalPoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(java.time.LocalDateTime creationDate) {
        if (creationDate == null) {
            this.creationDate = java.time.LocalDateTime.now();
        } else {
            this.creationDate = creationDate;
        }
    }

    public void setMinimalPoint(Float minimalPoint) {

        this.minimalPoint = minimalPoint;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }
}
