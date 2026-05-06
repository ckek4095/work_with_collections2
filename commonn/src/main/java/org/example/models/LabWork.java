package org.example.models;

/**
 * Класс LabWork, описывающий элемент коллекции
 */
public class LabWork {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private java.time.LocalDateTime creationDate;
    private Float minimalPoint;
    private Difficulty difficulty;
    private Discipline discipline;
    private Integer ownerId;

    public LabWork() {
        this.id = 0L;
        this.name = "";
        this.coordinates = new Coordinates(0, 0);
        this.creationDate = java.time.LocalDateTime.now();
        this.minimalPoint = 0F;
        this.difficulty = Difficulty.EASY;
        this.discipline = new Discipline("", 0);
        this.ownerId = null;
    }

    public LabWork(LabWork lab) {
        this.id = lab.getId();
        this.name = lab.getName();
        this.coordinates = lab.getCoordinates();
        this.creationDate = lab.getCreationDate();
        this.minimalPoint = lab.getMinimalPoint();
        this.difficulty = lab.getDifficulty();
        this.discipline = lab.getDiscipline();
        this.ownerId = lab.getOwnerId();
    }

    // добавление в бд без ид(?)
    public LabWork(String name, Coordinates coordinates, Float minimalPoint, Difficulty difficulty, Discipline discipline, Integer ownerId) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = java.time.LocalDateTime.now();
        this.minimalPoint = minimalPoint;
        this.difficulty = difficulty;
        this.discipline = discipline;
        this.ownerId = ownerId;
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

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public java.time.LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.time.LocalDateTime creationDate) {
        this.creationDate = creationDate != null ? creationDate : java.time.LocalDateTime.now();
    }

    public Float getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Float minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return String.format(
                "LabWork{id=%d, name='%s', minimalPoint=%.1f, difficulty=%s}",
                id, name, minimalPoint, difficulty
        );
    }
}