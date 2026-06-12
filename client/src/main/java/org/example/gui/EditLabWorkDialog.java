package org.example.gui;

import org.example.gui.localization.LocaleManager;
import org.example.gui.localization.ServerResponseLocalizer;
import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;
import org.example.models.LabWork;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditLabWorkDialog extends JDialog {
    private final GuiClientService clientService;
    private final Runnable refreshCallback;
    private final LabWork oldLabWork;

    private JTextField nameField;
    private JTextField xField;
    private JTextField yField;
    private JTextField minimalPointField;
    private JComboBox<Difficulty> difficultyBox;
    private JTextField disciplineNameField;
    private JTextField labsCountField;
    private JLabel errorLabel;

    public EditLabWorkDialog(JFrame parent,
                             GuiClientService clientService,
                             Runnable refreshCallback,
                             LabWork oldLabWork) {
        // Заменили жестко заданный заголовок окна
        super(parent, LocaleManager.get("dialog.edit.title"), true);
        this.clientService = clientService;
        this.refreshCallback = refreshCallback;
        this.oldLabWork = oldLabWork;

        setSize(560, 590);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        fillForm();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(24, 30, 24, 30));
        setContentPane(root);

        JLabel titleLabel = new JLabel(LocaleManager.get("dialog.edit.title"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel idLabel = new JLabel(LocaleManager.get("column.id") + ": " + oldLabWork.getId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        idLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel top = new JPanel();
        top.setBackground(Color.WHITE);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(titleLabel);
        top.add(Box.createVerticalStrut(12));
        top.add(idLabel);

        root.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new GridLayout(7, 2, 14, 16));
        form.setBorder(new EmptyBorder(30, 0, 20, 0));

        nameField = new JTextField();
        xField = new JTextField();
        yField = new JTextField();
        minimalPointField = new JTextField();
        difficultyBox = new JComboBox<>(Difficulty.values());
        disciplineNameField = new JTextField();
        labsCountField = new JTextField();

        form.add(createLabel(LocaleManager.get("field.name")));
        form.add(nameField);

        form.add(createLabel(LocaleManager.get("field.x")));
        form.add(xField);

        form.add(createLabel(LocaleManager.get("field.y")));
        form.add(yField);

        form.add(createLabel(LocaleManager.get("field.minimal.point")));
        form.add(minimalPointField);

        form.add(createLabel(LocaleManager.get("field.difficulty")));
        form.add(difficultyBox);

        form.add(createLabel(LocaleManager.get("field.discipline")));
        form.add(disciplineNameField);

        form.add(createLabel(LocaleManager.get("field.labs.count")));
        form.add(labsCountField);

        root.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(180, 40, 40));
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttons.setBackground(Color.WHITE);

        // Локализовали кнопки сохранения и отмены
        JButton saveButton = new JButton(LocaleManager.get("button.save"));
        JButton cancelButton = new JButton(LocaleManager.get("button.cancel"));

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> dispose());

        buttons.add(saveButton);
        buttons.add(cancelButton);

        bottom.add(errorLabel);
        bottom.add(Box.createVerticalStrut(16));
        bottom.add(buttons);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private void fillForm() {
        nameField.setText(oldLabWork.getName());

        if (oldLabWork.getCoordinates() != null) {
            xField.setText(String.valueOf(oldLabWork.getCoordinates().getX()));
            yField.setText(String.valueOf(oldLabWork.getCoordinates().getY()));
        }

        minimalPointField.setText(String.valueOf(oldLabWork.getMinimalPoint()));

        if (oldLabWork.getDifficulty() != null) {
            difficultyBox.setSelectedItem(oldLabWork.getDifficulty());
        }

        if (oldLabWork.getDiscipline() != null) {
            disciplineNameField.setText(oldLabWork.getDiscipline().getName());
            labsCountField.setText(String.valueOf(oldLabWork.getDiscipline().getLabsCount()));
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        return label;
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 15));
    }

    private void save() {
        try {
            LabWork updated = readLabWorkFromForm();

            String[] args = buildArgs(updated);

            var response = clientService.executeLabWorkCommand(
                    "update",
                    updated,
                    args
            );

            // Обрабатываем ответ через локализатор перед выводом
            String localizedMessage = ServerResponseLocalizer.localize(response);
            JOptionPane.showMessageDialog(this, localizedMessage);

            if (clientService.isSuccess(response)) {
                refreshCallback.run();
                dispose();
            }

        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText(LocaleManager.get("server.generic.error"));
        }
    }

    private LabWork readLabWorkFromForm() {
        String name = nameField.getText().trim();
        String xText = xField.getText().trim();
        String yText = yField.getText().trim();
        String minimalPointText = minimalPointField.getText().trim();
        String disciplineName = disciplineNameField.getText().trim();
        String labsCountText = labsCountField.getText().trim();

        if (name.isEmpty()
                || xText.isEmpty()
                || yText.isEmpty()
                || minimalPointText.isEmpty()
                || disciplineName.isEmpty()
                || labsCountText.isEmpty()) {
            throw new IllegalArgumentException(LocaleManager.get("message.fill.all"));
        }

        int x;
        int y;
        float minimalPoint;
        int labsCount;

        try {
            x = Integer.parseInt(xText);
            y = Integer.parseInt(yText);
            minimalPoint = Float.parseFloat(minimalPointText.replace(",", "."));
            labsCount = Integer.parseInt(labsCountText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(LocaleManager.get("message.number.error"));
        }

        if (minimalPoint <= 0) {
            throw new IllegalArgumentException("Минимальный балл должен быть больше 0");
        }

        if (labsCount <= 0) {
            throw new IllegalArgumentException("Количество лабораторных должно быть больше 0");
        }

        Difficulty difficulty = (Difficulty) difficultyBox.getSelectedItem();

        LabWork labWork = new LabWork(
                name,
                new Coordinates(x, y),
                minimalPoint,
                difficulty,
                new Discipline(disciplineName, labsCount),
                oldLabWork.getOwnerId()
        );

        labWork.setId(oldLabWork.getId());
        labWork.setCreationDate(oldLabWork.getCreationDate());

        return labWork;
    }

    private String[] buildArgs(LabWork labWork) {
        return new String[]{
                String.valueOf(oldLabWork.getId()),
                labWork.getName(),
                String.valueOf(labWork.getCoordinates().getX()),
                String.valueOf(labWork.getCoordinates().getY()),
                String.valueOf(labWork.getMinimalPoint()),
                labWork.getDifficulty().name(),
                labWork.getDiscipline().getName(),
                String.valueOf(labWork.getDiscipline().getLabsCount())
        };
    }
}