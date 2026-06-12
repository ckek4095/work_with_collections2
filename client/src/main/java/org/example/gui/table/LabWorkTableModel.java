package org.example.gui.table;

import org.example.gui.localization.LocaleManager;
import org.example.models.LabWork;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LabWorkTableModel extends AbstractTableModel {
    private String[] getColumns() {
        return new String[]{
                LocaleManager.get("column.id"),
                LocaleManager.get("column.name"),
                LocaleManager.get("column.x"),
                LocaleManager.get("column.y"),
                LocaleManager.get("column.minimal.point"),
                LocaleManager.get("column.difficulty"),
                LocaleManager.get("column.discipline"),
                LocaleManager.get("column.labs.count"),
                LocaleManager.get("column.owner.id"),
                LocaleManager.get("column.creation.date")
        };
    }
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private List<LabWork> labWorks = new ArrayList<>();

    public void setLabWorks(List<LabWork> labWorks) {
        this.labWorks = labWorks == null ? new ArrayList<>() : new ArrayList<>(labWorks);
        fireTableDataChanged();
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
        return getColumns().length;
    }

    @Override
    public String getColumnName(int column) {
        return getColumns()[column];
    }

    public void updateLocale() {
        fireTableStructureChanged();
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