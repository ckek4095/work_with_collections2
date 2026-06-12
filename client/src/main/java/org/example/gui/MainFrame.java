package org.example.gui;

import org.example.Request;
import org.example.gui.localization.GuiLocale;
import org.example.gui.localization.LocaleManager;
import org.example.gui.localization.ServerResponseLocalizer;
import org.example.gui.table.LabWorkTableModel;
import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;
import org.example.models.LabWork;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.Timer;
import java.io.File;
import java.util.List;

public class MainFrame extends JFrame {
    private final GuiClientService clientService;

    private LabWorkTableModel tableModel;
    private JTable table;
    private VisualizationPanel visualizationPanel;

    private JTextField filterField;
    private JComboBox<String> sortBox;
    private JComboBox<String> orderBox;
    private Timer autoRefreshTimer;
    private JLabel userLabel;
    private JLabel languageLabel;
    private JLabel filterLabel;
    private JLabel sortLabel;

    private JComboBox<GuiLocale> languageBox;
    private JButton clearFilterButton;
    private JButton applyButton;
    private List<JButton> commandButtons = new ArrayList<>();

    private List<LabWork> allLabWorks = new ArrayList<>();

    private SnowfallOverlay snowfallOverlay;
    private JCheckBox snowToggleButton;
    private final Set<String> executingScripts = new HashSet<>();

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

        SwingUtilities.invokeLater(this::initSnowfallOverlay);

        refreshCollection();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        root.add(createTopPanel(), BorderLayout.NORTH);
        root.add(createCenterPanel(), BorderLayout.CENTER);
        root.add(createButtonsPanel(), BorderLayout.SOUTH);

        updateTexts();
        LocaleManager.addLocaleChangeListener(this::updateTexts);
    }

    private JPanel createTopPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(Color.WHITE);

        userLabel = new JLabel();
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        languageLabel = new JLabel();
        languageBox = new JComboBox<>(GuiLocale.values());
        languageBox.setSelectedItem(LocaleManager.getCurrentGuiLocale());
        languageBox.addActionListener(e -> {
            GuiLocale selected = (GuiLocale) languageBox.getSelectedItem();
            LocaleManager.setCurrentLocale(selected);
        });
        languageBox.setPreferredSize(new Dimension(260, 34));

        snowToggleButton = new JCheckBox(LocaleManager.get("main.snowfall"));
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

        filterLabel = new JLabel();
        filterField = new JTextField();
        filterField.setPreferredSize(new Dimension(330, 36));

        clearFilterButton = new JButton();
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

        sortLabel = new JLabel();

        sortBox = new JComboBox<>(new String[]{
                "ID",
                LocaleManager.get("column.name"),
                "X",
                "Y",
                LocaleManager.get("column.minimal.point"),
                LocaleManager.get("column.difficulty"),
                LocaleManager.get("column.discipline"),
                LocaleManager.get("column.labs.count"),
                LocaleManager.get("column.owner.id"),
                LocaleManager.get("column.creation.date")
        });
        sortBox.setPreferredSize(new Dimension(190, 36));

        orderBox = new JComboBox<>(new String[]{
                LocaleManager.get("main.asc"),
                LocaleManager.get("main.desc")
        });
        orderBox.setPreferredSize(new Dimension(190, 36));

        applyButton = new JButton();
        applyButton.setPreferredSize(new Dimension(120, 36));
        applyButton.addActionListener(e -> applyFilterAndSort());

        sortPanel.add(sortLabel);
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
        tableScroll.setBorder(BorderFactory.createTitledBorder(LocaleManager.get("main.table.title")));

        visualizationPanel = new VisualizationPanel();
        visualizationPanel.setClickHandler(this::showLabWorkInfo);

        center.add(tableScroll);
        center.add(visualizationPanel);

        return center;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 7, 12, 12)); // Изменено с 2x8 на 2x7
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Очищаем список кнопок перед созданием
        commandButtons.clear();

        // Создаем кнопки и сохраняем их (без Show и Help)
        JButton refreshBtn = createCommandButton(LocaleManager.get("button.refresh") + "\n(Refresh)", this::refreshCollection);
        JButton addBtn = createCommandButton(LocaleManager.get("button.add") + "\n(Add)", this::openAddDialog);
        JButton addIfMaxBtn = createCommandButton(LocaleManager.get("button.add.if.max") + "\n(AddIfMax)", this::openAddIfMaxDialog);
        JButton addIfMinBtn = createCommandButton(LocaleManager.get("button.add.if.min") + "\n(AddIfMin)", this::openAddIfMinDialog);
        JButton updateBtn = createCommandButton(LocaleManager.get("button.update") + "\n(UpdateById)", this::openEditDialog);
        JButton removeBtn = createCommandButton(LocaleManager.get("button.remove") + "\n(RemoveById)", this::removeSelectedById);
        JButton removeAllBtn = createCommandButton(LocaleManager.get("button.remove.all") + "\n(RemoveAllBy)", this::removeAllByMinimalPoint);
        JButton clearBtn = createCommandButton(LocaleManager.get("button.clear") + "\n(Clear)", this::clearCollection);

        JButton filterDisciplineBtn = createCommandButton(LocaleManager.get("button.filter.discipline") + "\n(FilterByDiscipline)", this::filterByDisciplineCommand);
        JButton filterNameBtn = createCommandButton(LocaleManager.get("button.filter.name") + "\n(FilterStartsWith)", this::filterStartsWithCommand);
        JButton historyBtn = createCommandButton(LocaleManager.get("button.history") + "\n(History)", () -> executeTextCommand("history"));
        JButton scriptBtn = createCommandButton(LocaleManager.get("button.script") + "\n(ExecuteScript)", this::executeScriptFromFile);
        JButton infoBtn = createCommandButton(LocaleManager.get("button.info") + "\n(Info)", () -> executeTextCommand("info"));
        JButton exitBtn = createCommandButton(LocaleManager.get("button.exit") + "\n(Exit)", this::exitApplication);

        // Добавляем все кнопки в список (14 кнопок -> 2 ряда по 7)
        commandButtons.addAll(Arrays.asList(
                refreshBtn, addBtn, addIfMaxBtn, addIfMinBtn, updateBtn,
                removeBtn, removeAllBtn, clearBtn,
                filterDisciplineBtn, filterNameBtn, historyBtn,
                scriptBtn, infoBtn, exitBtn
        ));

        // Добавляем кнопки на панель
        for (JButton btn : commandButtons) {
            panel.add(btn);
        }

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

    private void initSnowfallOverlay() {
        snowfallOverlay = new SnowfallOverlay(getWidth(), getHeight());
        snowfallOverlay.setOpaque(false);

        getLayeredPane().add(snowfallOverlay, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (snowfallOverlay != null) {
                    snowfallOverlay.updateSize(getWidth(), getHeight());
                    snowfallOverlay.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        });

        snowfallOverlay.setBounds(0, 0, getWidth(), getHeight());
    }

    private void toggleSnowfall(boolean enable) {
        if (snowfallOverlay != null) {
            if (enable) {
                if (!snowfallOverlay.isVisible()) {
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
                String localizedMessage = clientService.getResponseText(response);
                showMessage(localizedMessage);
                return;
            }

            String text = clientService.getResponseText(response);

            if (text == null || text.isBlank() || text.contains("Пусто") || text.contains("empty")) {
                showMessage(LocaleManager.get("server.empty.collection"));
                allLabWorks = new ArrayList<>();
            } else {
                allLabWorks = parseLabWorksFromShow(text);
            }

            if (tableModel != null) {
                tableModel.setLabWorks(allLabWorks);
            }
            if (visualizationPanel != null) {
                visualizationPanel.setLabWorks(allLabWorks);
            }

        } catch (Exception e) {
            showMessage(LocaleManager.get("message.error.refresh") + ":\n" + e.getMessage());
        }
    }

    private void applyFilterAndSort() {
        if (tableModel == null) {
            return;
        }

        String filter = filterField == null ? "" : filterField.getText().trim().toLowerCase();
        String sortColumn = sortBox == null ? "ID" : (String) sortBox.getSelectedItem();
        boolean desc = orderBox != null && orderBox.getSelectedIndex() == 1;

        List<LabWork> result = allLabWorks.stream()
                .filter(lab -> filter.isEmpty() || labToSearchString(lab).contains(filter))
                .sorted(getComparator(sortColumn, desc))
                .toList();

        tableModel.setLabWorks(result);
        if (visualizationPanel != null) {
            visualizationPanel.setLabWorks(result);
        }
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
            String localizedMessage = clientService.getResponseText(response);
            showMessage(localizedMessage);
        } catch (Exception e) {
            showMessage(LocaleManager.get("login.connection.error") + ":\n" + e.getMessage());
        }
    }

    private void removeSelectedById() {
        LabWork selected = getSelectedLabWork();
        if (selected == null) {
            showMessage(LocaleManager.get("message.select.object"));
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                LocaleManager.get("message.confirm.remove") + " " + selected.getId() + "?",
                LocaleManager.get("dialog.confirm.title"),
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

            String localizedMessage = clientService.getResponseText(response);
            showMessage(localizedMessage);
            refreshCollection();

        } catch (Exception e) {
            showMessage(LocaleManager.get("login.connection.error") + ":\n" + e.getMessage());
        }
    }

    private void clearCollection() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                LocaleManager.get("message.confirm.clear"),
                LocaleManager.get("dialog.confirm.title"),
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand("clear");
            String localizedMessage = clientService.getResponseText(response);
            showMessage(localizedMessage);
            refreshCollection();
        } catch (Exception e) {
            showMessage(LocaleManager.get("login.connection.error") + ":\n" + e.getMessage());
        }
    }

    private void removeAllByMinimalPoint() {
        String value = JOptionPane.showInputDialog(
                this,
                LocaleManager.get("dialog.enter.minimal.point"),
                LocaleManager.get("button.remove.all"),
                JOptionPane.PLAIN_MESSAGE
        );

        if (value == null || value.isBlank()) {
            return;
        }

        try {
            Request response = clientService.executeSimpleCommand(
                    "remove_all_by_minimal_point",
                    value.trim()
            );
            String localizedMessage = ServerResponseLocalizer.localize(response);
            showMessage(localizedMessage);
            refreshCollection();
        } catch (Exception e) {
            showMessage(LocaleManager.get("login.connection.error") + ":\n" + e.getMessage());
        }
    }

    private void filterByDisciplineCommand() {
        String value = JOptionPane.showInputDialog(
                this,
                LocaleManager.get("dialog.enter.discipline.name"),
                LocaleManager.get("button.filter.discipline"),
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
            String localizedMessage = ServerResponseLocalizer.localize(response);
            showMessage(localizedMessage);
        } catch (Exception e) {
            showMessage(LocaleManager.get("login.connection.error") + ":\n" + e.getMessage());
        }
    }

    private void filterStartsWithCommand() {
        String value = JOptionPane.showInputDialog(
                this,
                LocaleManager.get("dialog.enter.name.start"),
                LocaleManager.get("button.filter.name"),
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
            String localizedMessage = ServerResponseLocalizer.localize(response);
            showMessage(localizedMessage);
        } catch (Exception e) {
            showMessage(LocaleManager.get("login.connection.error") + ":\n" + e.getMessage());
        }
    }

    private LabWork getSelectedLabWork() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || tableModel == null) {
            return null;
        }

        return tableModel.getLabWorkAt(selectedRow);
    }

    private void showLabWorkInfo(LabWork labWork) {
        String text = String.format("""
                %s: %d
                %s: %s
                %s: %d
                %s: %d
                %s: %.2f
                %s: %s
                %s: %s
                %s: %d
                %s: %d
                %s: %s
                """,
                LocaleManager.get("column.id"), labWork.getId(),
                LocaleManager.get("column.name"), labWork.getName(),
                "X", labWork.getCoordinates().getX(),
                "Y", labWork.getCoordinates().getY(),
                LocaleManager.get("column.minimal.point"), labWork.getMinimalPoint(),
                LocaleManager.get("column.difficulty"), labWork.getDifficulty(),
                LocaleManager.get("column.discipline"), labWork.getDiscipline().getName(),
                LocaleManager.get("column.labs.count"), labWork.getDiscipline().getLabsCount(),
                LocaleManager.get("column.owner.id"), labWork.getOwnerId(),
                LocaleManager.get("column.creation.date"), labWork.getCreationDate()
        );

        JOptionPane.showMessageDialog(this, text, LocaleManager.get("dialog.object.info.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAddDialog() {
        AddLabWorkDialog dialog = new AddLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                "add",
                LocaleManager.get("dialog.add.title")
        );
        dialog.setVisible(true);
    }

    private void openAddIfMaxDialog() {
        AddLabWorkDialog dialog = new AddLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                "add_if_max",
                LocaleManager.get("button.add.if.max")
        );
        dialog.setVisible(true);
    }

    private void openAddIfMinDialog() {
        AddLabWorkDialog dialog = new AddLabWorkDialog(
                this,
                clientService,
                this::refreshCollection,
                "add_if_min",
                LocaleManager.get("button.add.if.min")
        );
        dialog.setVisible(true);
    }

    private void openEditDialog() {
        LabWork selected = getSelectedLabWork();

        if (selected == null) {
            showMessage(LocaleManager.get("message.select.object"));
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

    private void executeScript(File file) {
        if (file == null || !file.exists()) {
            showMessage(LocaleManager.get("message.file.not.found"));
            return;
        }

        try {
            String scriptText = Files.readString(file.toPath());

            Request response = clientService.executeSimpleCommand(
                    "execute_script",
                    scriptText
            );

            if (response != null) {
                showMessage(LocaleManager.get("message.script.completed") + "\n" + LocaleManager.get("message.errors") + ": " + response.getData());
                refreshCollection();
            } else {
                showMessage(LocaleManager.get("message.server.no.response"));
            }

        } catch (Exception e) {
            showMessage(LocaleManager.get("message.script.error") + ":\n" + e.getMessage());
        }
    }

    private void executeScriptFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LocaleManager.get("dialog.select.script.file"));

        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        executeScript(file);
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

    private void updateButtonsText() {
        // Обновленный список ключей для кнопок (без show и help)
        String[] buttonKeys = {
                "button.refresh", "button.add", "button.add.if.max", "button.add.if.min",
                "button.update", "button.remove", "button.remove.all", "button.clear",
                "button.filter.discipline", "button.filter.name", "button.history",
                "button.script", "button.info", "button.exit"
        };

        // Английские подписи для кнопок
        String[] englishLabels = {
                "Refresh", "Add", "AddIfMax", "AddIfMin",
                "UpdateById", "RemoveById", "RemoveAllBy", "Clear",
                "FilterByDiscipline", "FilterStartsWith", "History",
                "ExecuteScript", "Info", "Exit"
        };

        for (int i = 0; i < commandButtons.size() && i < buttonKeys.length; i++) {
            JButton btn = commandButtons.get(i);
            String localizedText = LocaleManager.get(buttonKeys[i]);
            btn.setText("<html><center>" + localizedText + "\n(" + englishLabels[i] + ")</center></html>");
        }
    }

    private void updateTexts() {
        setTitle(LocaleManager.get("app.title"));

        if (userLabel != null) {
            userLabel.setText(LocaleManager.get("main.user") + "   " + clientService.getLogin());
        }

        if (languageLabel != null) {
            languageLabel.setText(LocaleManager.get("main.language"));
        }

        if (snowToggleButton != null) {
            snowToggleButton.setText(LocaleManager.get("main.snowfall"));
        }

        if (filterLabel != null) {
            filterLabel.setText(LocaleManager.get("main.filter"));
        }

        if (clearFilterButton != null) {
            clearFilterButton.setText(LocaleManager.get("main.clear.filter"));
        }

        if (sortLabel != null) {
            sortLabel.setText(LocaleManager.get("main.sort"));
        }

        if (applyButton != null) {
            applyButton.setText(LocaleManager.get("main.apply"));
        }

        if (orderBox != null) {
            int selected = orderBox.getSelectedIndex();
            orderBox.removeAllItems();
            orderBox.addItem(LocaleManager.get("main.asc"));
            orderBox.addItem(LocaleManager.get("main.desc"));
            orderBox.setSelectedIndex(Math.max(0, selected));
        }

        if (sortBox != null) {
            String selected = (String) sortBox.getSelectedItem();
            sortBox.removeAllItems();
            sortBox.addItem("ID");
            sortBox.addItem(LocaleManager.get("column.name"));
            sortBox.addItem("X");
            sortBox.addItem("Y");
            sortBox.addItem(LocaleManager.get("column.minimal.point"));
            sortBox.addItem(LocaleManager.get("column.difficulty"));
            sortBox.addItem(LocaleManager.get("column.discipline"));
            sortBox.addItem(LocaleManager.get("column.labs.count"));
            sortBox.addItem(LocaleManager.get("column.owner.id"));
            sortBox.addItem(LocaleManager.get("column.creation.date"));
            if (selected != null) {
                sortBox.setSelectedItem(selected);
            }
        }

        if (tableModel != null) {
            tableModel.updateLocale();
        }

        if (visualizationPanel != null) {
            visualizationPanel.updateLocale();
        }

        updateButtonsText();

        applyFilterAndSort();
    }
}