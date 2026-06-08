package org.example.gui;

import org.example.Request;
import org.example.gui.table.LabWorkTableModel;
import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;
import org.example.models.LabWork;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.Timer;

public class MainFrame extends JFrame {
    private final GuiClientService clientService;

    private LabWorkTableModel tableModel;
    private JTable table;
    private VisualizationPanel visualizationPanel;

    private JTextField filterField;
    private JComboBox<String> sortBox;
    private JComboBox<String> orderBox;
    private Timer autoRefreshTimer;
    private boolean autoRefreshEnabled = true;

    private List<LabWork> allLabWorks = new ArrayList<>();

    // Снегопад
    private SnowfallOverlay snowfallOverlay;
    private JCheckBox snowToggleButton;

    public MainFrame(GuiClientService clientService) {
        this.clientService = clientService;

        setTitle("LabWork Manager");
        setSize(1500, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopAutoRefresh();
                if (snowfallOverlay != null) {
                    snowfallOverlay.stopSnowfall();
                }
            }
        });
        setMinimumSize(new Dimension(1200, 760));

        initComponents();

        // Инициализация снегопада после создания всех компонентов
        SwingUtilities.invokeLater(() -> {
            initSnowfallOverlay();
        });

        refreshCollection();
        startAutoRefresh();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        root.add(createTopPanel(), BorderLayout.NORTH);
        root.add(createCenterPanel(), BorderLayout.CENTER);
        root.add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Пользователь:   " + clientService.getLogin());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel languageLabel = new JLabel("Язык:");
        JComboBox<String> languageBox = new JComboBox<>(new String[]{
                "Русский (Россия)",
                "Белорусский",
                "Латышский",
                "English (Ireland)"
        });
        languageBox.setPreferredSize(new Dimension(260, 34));

        // Кнопка включения/выключения снегопада
        snowToggleButton = new JCheckBox("Снегопад");
        snowToggleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        snowToggleButton.setBackground(Color.WHITE);
        snowToggleButton.setSelected(true);
        snowToggleButton.addActionListener(e -> toggleSnowfall(snowToggleButton.isSelected()));

        rightPanel.add(languageLabel);
        rightPanel.add(languageBox);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(snowToggleButton);

        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(rightPanel, BorderLayout.EAST);

        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Фильтр (по любой колонке):");
        filterField = new JTextField();
        filterField.setPreferredSize(new Dimension(330, 36));

        JButton clearFilterButton = new JButton("Очистить");
        clearFilterButton.setPreferredSize(new Dimension(110, 36));
        clearFilterButton.addActionListener(e -> {
            filterField.setText("");
            applyFilterAndSort();
        });

        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(clearFilterButton);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        sortPanel.setBackground(Color.WHITE);

        sortBox = new JComboBox<>(new String[]{
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
        });
        sortBox.setPreferredSize(new Dimension(190, 36));

        orderBox = new JComboBox<>(new String[]{
                "По возрастанию",
                "По убыванию"
        });
        orderBox.setPreferredSize(new Dimension(190, 36));

        JButton applyButton = new JButton("Применить");
        applyButton.setPreferredSize(new Dimension(120, 36));
        applyButton.addActionListener(e -> applyFilterAndSort());

        sortPanel.add(new JLabel("Сортировать по:"));
        sortPanel.add(sortBox);
        sortPanel.add(orderBox);
        sortPanel.add(applyButton);

        controlsPanel.add(filterPanel, BorderLayout.WEST);
        controlsPanel.add(sortPanel, BorderLayout.EAST);

        wrapper.add(userPanel);
        wrapper.add(new JSeparator());
        wrapper.add(controlsPanel);

        return wrapper;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setBackground(Color.WHITE);

        tableModel = new LabWorkTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Таблица объектов"));

        visualizationPanel = new VisualizationPanel();
        visualizationPanel.setClickHandler(this::showLabWorkInfo);

        center.add(tableScroll);
        center.add(visualizationPanel);

        return center;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 8, 12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        panel.add(createCommandButton("Обновить\n(Refresh)", this::refreshCollection));
        panel.add(createCommandButton("Добавить\n(Add)", this::openAddDialog));
        panel.add(createCommandButton("Добавить если макс\n(AddIfMax)", this::openAddIfMaxDialog));
        panel.add(createCommandButton("Добавить если мин\n(AddIfMin)", this::openAddIfMinDialog));
        panel.add(createCommandButton("Обновить по ID\n(UpdateById)", this::openEditDialog));
        panel.add(createCommandButton("Удалить по ID\n(RemoveById)", this::removeSelectedById));
        panel.add(createCommandButton("Удалить всё\n(RemoveAllBy)", this::removeAllByMinimalPoint));
        panel.add(createCommandButton("Показать все\n(Show)", this::refreshCollection));
        panel.add(createCommandButton("Очистить\n(Clear)", this::clearCollection));

        panel.add(createCommandButton("Фильтр по дисциплине\n(FilterByDiscipline)", this::filterByDisciplineCommand));
        panel.add(createCommandButton("Фильтр начинается с\n(FilterStartsWith)", this::filterStartsWithCommand));
        panel.add(createCommandButton("История\n(History)", () -> executeTextCommand("history")));
        panel.add(createCommandButton("Сохранить историю\n(SaveHistory)", () -> showMessage("Если SaveHistory есть только на клиенте, её сделаем отдельно")));
        panel.add(createCommandButton("Выполнить скрипт\n(ExecuteScript)", () -> showMessage("ExecuteScript для GUI сделаем отдельно")));
        panel.add(createCommandButton("Информация\n(Info)", () -> executeTextCommand("info")));
        panel.add(createCommandButton("Справка\n(Help)", () -> executeTextCommand("help")));
        panel.add(createCommandButton("Выход\n(Exit)", this::exitApplication));

        return panel;
    }

    private JButton createCommandButton(String text, Runnable action) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(e -> action.run());
        return button;
    }

    // Инициализация снегопада
    private void initSnowfallOverlay() {
        snowfallOverlay = new SnowfallOverlay(getWidth(), getHeight());
        snowfallOverlay.setOpaque(false);

        // Добавляем в самый верхний слой
        getLayeredPane().add(snowfallOverlay, JLayeredPane.PALETTE_LAYER);

        // Обновляем размер при изменении окна
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (snowfallOverlay != null) {
                    snowfallOverlay.updateSize(getWidth(), getHeight());
                    snowfallOverlay.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        });

        // Устанавливаем начальные границы
        snowfallOverlay.setBounds(0, 0, getWidth(), getHeight());
    }

    // Включение/выключение снегопада
    private void toggleSnowfall(boolean enable) {
        if (snowfallOverlay != null) {
            if (enable) {
                if (!snowfallOverlay.isVisible()) {
                    // Пересоздаем для возобновления
                    getLayeredPane().remove(snowfallOverlay);
                    snowfallOverlay = new SnowfallOverlay(getWidth(), getHeight());
                    snowfallOverlay.setBounds(0, 0, getWidth(), getHeight());
                    getLayeredPane().add(snowfallOverlay, JLayeredPane.PALETTE_LAYER);
                    revalidate();
                    repaint();
                }
            } else {
                snowfallOverlay.stopSnowfall();
                snowfallOverlay.setVisible(false);
            }
        }
    }

    private void refreshCollection() {
        try {
            Request response = clientService.executeSimpleCommand("show");

            if (!clientService.isSuccess(response)) {
                showMessage(clientService.getResponseText(response));
                return;
            }

            String text = clientService.getResponseText(response);
            allLabWorks = parseLabWorksFromShow(text);

            tableModel.setLabWorks(allLabWorks);
            visualizationPanel.setLabWorks(allLabWorks);

        } catch (Exception e) {
            showMessage("Ошибка обновления коллекции:\n" + e.getMessage());
        }
    }

    private void applyFilterAndSort() {
        String filter = filterField.getText().trim().toLowerCase();
        String sortColumn = (String) sortBox.getSelectedItem();
        boolean desc = orderBox.getSelectedIndex() == 1;

        List<LabWork> result = allLabWorks.stream()
                .filter(lab -> filter.isEmpty() || labToSearchString(lab).contains(filter))
                .sorted(getComparator(sortColumn, desc))
                .toList();

        tableModel.setLabWorks(result);
        visualizationPanel.setLabWorks(result);
    }

    private String labToSearchString(LabWork lab) {
        return (
                lab.getId() + " " +
                        lab.getName() + " " +
                        lab.getCoordinates().getX() + " " +
                        lab.getCoordinates().getY() + " " +
                        lab.getMinimalPoint() + " " +
                        lab.getDifficulty() + " " +
                        lab.getDiscipline().getName() + " " +
                        lab.getDiscipline().getLabsCount() + " " +
                        lab.getOwnerId() + " " +
                        lab.getCreationDate()
        ).toLowerCase();
    }

    private Comparator<LabWork> getComparator(String column, boolean desc) {
        Comparator<LabWork> comparator = switch (column) {
            case "Название" -> Comparator.comparing(LabWork::getName, String.CASE_INSENSITIVE_ORDER);
            case "X" -> Comparator.comparing(lab -> lab.getCoordinates().getX());
            case "Y" -> Comparator.comparing(lab -> lab.getCoordinates().getY());
            case "Мин. балл" -> Comparator.comparing(LabWork::getMinimalPoint);
            case "Сложность" -> Comparator.comparing(lab -> lab.getDifficulty().name());
            case "Дисциплина" -> Comparator.comparing(lab -> lab.getDiscipline().getName(), String.CASE_INSENSITIVE_ORDER);
            case "Кол-во лаб." -> Comparator.comparing(lab -> lab.getDiscipline().getLabsCount());
            case "Владелец (ID)" -> Comparator.comparing(LabWork::getOwnerId);
            case "Дата создания" -> Comparator.comparing(LabWork::getCreationDate);
            default -> Comparator.comparing(LabWork::getId);
        };

        return desc ? comparator.reversed() : comparator;
    }

    private void executeTextCommand(String command) {
        try {
            Request response = clientService.executeSimpleCommand(command);
            showMessage(clientService.getResponseText(response));
        } catch (Exception e) {
            showMessage("Ошибка выполнения команды:\n" + e.getMessage());
        }
    }

    private void removeSelectedById() {
        LabWork selected = getSelectedLabWork();
        if (selected == null) {
            showMessage("Выберите объект в таблице");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Удалить объект с ID " + selected.getId() + "?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand(
                    "remove_by_id",
                    String.valueOf(selected.getId())
            );

            showMessage(clientService.getResponseText(response));
            refreshCollection();

        } catch (Exception e) {
            showMessage("Ошибка удаления:\n" + e.getMessage());
        }
    }

    private void clearCollection() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Очистить свои объекты?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand("clear");
            showMessage(clientService.getResponseText(response));
            refreshCollection();
        } catch (Exception e) {
            showMessage("Ошибка очистки:\n" + e.getMessage());
        }
    }

    private void removeAllByMinimalPoint() {
        String value = JOptionPane.showInputDialog(
                this,
                "Введите минимальный балл:",
                "RemoveAllBy",
                JOptionPane.PLAIN_MESSAGE
        );

        if (value == null || value.isBlank()) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand(
                    "remove_all_by_minimal_points",
                    value.trim()
            );
            showMessage(clientService.getResponseText(response));
            refreshCollection();
        } catch (Exception e) {
            showMessage("Ошибка удаления:\n" + e.getMessage());
        }
    }

    private void filterByDisciplineCommand() {
        String value = JOptionPane.showInputDialog(
                this,
                "Введите название дисциплины:",
                "FilterByDiscipline",
                JOptionPane.PLAIN_MESSAGE
        );

        if (value == null || value.isBlank()) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand(
                    "filter_by_discipline",
                    value.trim()
            );
            showMessage(clientService.getResponseText(response));
        } catch (Exception e) {
            showMessage("Ошибка фильтрации:\n" + e.getMessage());
        }
    }

    private void filterStartsWithCommand() {
        String value = JOptionPane.showInputDialog(
                this,
                "Введите начало названия:",
                "FilterStartsWith",
                JOptionPane.PLAIN_MESSAGE
        );

        if (value == null || value.isBlank()) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand(
                    "filter_starts_with_name",
                    value.trim()
            );
            showMessage(clientService.getResponseText(response));
        } catch (Exception e) {
            showMessage("Ошибка фильтрации:\n" + e.getMessage());
        }
    }

    private LabWork getSelectedLabWork() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        return tableModel.getLabWorkAt(selectedRow);
    }

    private void showLabWorkInfo(LabWork labWork) {
        String text = """
                ID: %s
                Название: %s
                Координата X: %s
                Координата Y: %s
                Минимальный балл: %s
                Сложность: %s
                Дисциплина: %s
                Количество лабораторных: %s
                Владелец (ID): %s
                Дата создания: %s
                """.formatted(
                labWork.getId(),
                labWork.getName(),
                labWork.getCoordinates().getX(),
                labWork.getCoordinates().getY(),
                labWork.getMinimalPoint(),
                labWork.getDifficulty(),
                labWork.getDiscipline().getName(),
                labWork.getDiscipline().getLabsCount(),
                labWork.getOwnerId(),
                labWork.getCreationDate()
        );

        JOptionPane.showMessageDialog(this, text, "Информация об объекте", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAddDialog() {
        AddLabWorkDialog dialog = new AddLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                "add",
                "Добавление объекта"
        );
        dialog.setVisible(true);
    }

    private void openAddIfMaxDialog() {
        AddLabWorkDialog dialog = new AddLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                "add_if_max",
                "Добавить, если максимальный"
        );
        dialog.setVisible(true);
    }

    private void openAddIfMinDialog() {
        AddLabWorkDialog dialog = new AddLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                "add_if_min",
                "Добавить, если минимальный"
        );
        dialog.setVisible(true);
    }

    private void openEditDialog() {
        LabWork selected = getSelectedLabWork();

        if (selected == null) {
            showMessage("Выберите объект в таблице");
            return;
        }

        EditLabWorkDialog dialog = new EditLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                selected
        );
        dialog.setVisible(true);
    }

    private void exitApplication() {
        dispose();
        System.exit(0);
    }

    private void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private List<LabWork> parseLabWorksFromShow(String text) {
        List<LabWork> result = new ArrayList<>();

        if (text == null || text.isBlank() || text.contains("Пусто")) {
            return result;
        }

        String[] blocks = text.split("----------------------------------------------------------");

        for (String block : blocks) {
            if (!block.contains("ID:")) {
                continue;
            }

            try {
                LabWork labWork = new LabWork();

                labWork.setId(Long.parseLong(extract(block, "ID:", "\n").trim()));
                labWork.setName(extract(block, "Имя:", "\n").trim());

                String coordinatesLine = extract(block, "Координаты:", "\n");
                int x = Integer.parseInt(extract(coordinatesLine, "x =", ";").trim());
                int y = Integer.parseInt(extract(coordinatesLine, "y =", "\n").trim());
                labWork.setCoordinates(new Coordinates(x, y));

                String dateText = extract(block, "Дата создания:", "\n").trim();
                labWork.setCreationDate(LocalDateTime.parse(dateText));

                labWork.setMinimalPoint(Float.parseFloat(extract(block, "Минимальная оценка:", "\n").trim()));
                labWork.setDifficulty(Difficulty.valueOf(extract(block, "Сложность:", "\n").trim()));

                String disciplineLine = extract(block, "Дисциплина:", "\n");
                String disciplineName = extract(disciplineLine, "Наименование - \"", "\";").trim();
                int labsCount = Integer.parseInt(extract(disciplineLine, "Количество лабораторных:", "\n").trim());
                labWork.setDiscipline(new Discipline(disciplineName, labsCount));

                labWork.setOwnerId(Integer.parseInt(extract(block, "ID владельца:", "\n").trim()));

                result.add(labWork);

            } catch (Exception ignored) {
            }
        }

        return result;
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(3000, e -> {
            if (autoRefreshEnabled) {
                refreshCollectionSilently();
            }
        });

        autoRefreshTimer.start();
    }

    private void refreshCollectionSilently() {
        try {
            Request response = clientService.executeSimpleCommand("show");

            if (!clientService.isSuccess(response)) {
                return;
            }

            String text = clientService.getResponseText(response);
            List<LabWork> updatedLabWorks = parseLabWorksFromShow(text);

            if (!isSameCollection(allLabWorks, updatedLabWorks)) {
                allLabWorks = updatedLabWorks;
                applyFilterAndSort();
            }

        } catch (Exception ignored) {
        }
    }

    private boolean isSameCollection(List<LabWork> oldList, List<LabWork> newList) {
        if (oldList == null && newList == null) {
            return true;
        }

        if (oldList == null || newList == null) {
            return false;
        }

        if (oldList.size() != newList.size()) {
            return false;
        }

        for (int i = 0; i < oldList.size(); i++) {
            LabWork oldLab = oldList.get(i);
            LabWork newLab = newList.get(i);

            if (!String.valueOf(oldLab.getId()).equals(String.valueOf(newLab.getId()))) {
                return false;
            }

            if (!String.valueOf(oldLab.getName()).equals(String.valueOf(newLab.getName()))) {
                return false;
            }

            if (!String.valueOf(oldLab.getMinimalPoint()).equals(String.valueOf(newLab.getMinimalPoint()))) {
                return false;
            }

            if (!String.valueOf(oldLab.getDifficulty()).equals(String.valueOf(newLab.getDifficulty()))) {
                return false;
            }

            if (!String.valueOf(oldLab.getOwnerId()).equals(String.valueOf(newLab.getOwnerId()))) {
                return false;
            }

            if (oldLab.getCoordinates() != null && newLab.getCoordinates() != null) {
                if (oldLab.getCoordinates().getX() != newLab.getCoordinates().getX()) {
                    return false;
                }

                if (!String.valueOf(oldLab.getCoordinates().getY()).equals(String.valueOf(newLab.getCoordinates().getY()))) {
                    return false;
                }
            }

            if (oldLab.getDiscipline() != null && newLab.getDiscipline() != null) {
                if (!String.valueOf(oldLab.getDiscipline().getName()).equals(String.valueOf(newLab.getDiscipline().getName()))) {
                    return false;
                }

                if (oldLab.getDiscipline().getLabsCount() != newLab.getDiscipline().getLabsCount()) {
                    return false;
                }
            }
        }

        return true;
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }

    private String extract(String text, String from, String to) {
        int start = text.indexOf(from);
        if (start < 0) {
            return "";
        }

        start += from.length();

        int end = text.indexOf(to, start);
        if (end < 0) {
            end = text.length();
        }

        return text.substring(start, end);
    }
}