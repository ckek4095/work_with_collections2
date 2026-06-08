package org.example.gui.table;

import org.example.models.LabWork;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LabWorkTableModel extends AbstractTableModel {
    private final String[] columns = {
            "ID",
            "Название",
            "X",
            "Y",
            "Мин. балл",
            "Сложность",
            "Дисциплина",
            "Кол-во лаб.",
            "Владелец (ID)",
            "Дата создания"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private List<LabWork> labWorks = new ArrayList<>();

    public void setLabWorks(List<LabWork> labWorks) {
        this.labWorks = labWorks == null ? new ArrayList<>() : new ArrayList<>(labWorks);
        fireTableDataChanged();
    }

    public List<LabWork> getLabWorks() {
        return new ArrayList<>(labWorks);
    }

    public LabWork getLabWorkAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= labWorks.size()) {
            return null;
        }
        return labWorks.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return labWorks.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LabWork labWork = labWorks.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> labWork.getId();
            case 1 -> labWork.getName();
            case 2 -> labWork.getCoordinates() == null ? "" : labWork.getCoordinates().getX();
            case 3 -> labWork.getCoordinates() == null ? "" : labWork.getCoordinates().getY();
            case 4 -> labWork.getMinimalPoint();
            case 5 -> labWork.getDifficulty();
            case 6 -> labWork.getDiscipline() == null ? "" : labWork.getDiscipline().getName();
            case 7 -> labWork.getDiscipline() == null ? "" : labWork.getDiscipline().getLabsCount();
            case 8 -> labWork.getOwnerId();
            case 9 -> labWork.getCreationDate() == null ? "" : labWork.getCreationDate().format(dateFormatter);
            default -> "";
        };
    }
}