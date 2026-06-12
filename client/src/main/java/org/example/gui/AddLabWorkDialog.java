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

public class AddLabWorkDialog extends JDialog {
    private final GuiClientService clientService;
    private final Runnable refreshCallback;
    private final String commandName;

    private JTextField nameField;
    private JTextField xField;
    private JTextField yField;
    private JTextField minimalPointField;
    private JComboBox<Difficulty> difficultyBox;
    private JTextField disciplineNameField;
    private JTextField labsCountField;
    private JLabel errorLabel;

    public AddLabWorkDialog(JFrame parent,
                            GuiClientService clientService,
                            Runnable refreshCallback,
                            String commandName,
                            String title) {
        super(parent, title, true);
        this.clientService = clientService;
        this.refreshCallback = refreshCallback;
        this.commandName = commandName;

        setSize(560, 560);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents(title);
    }

    private void initComponents(String title) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(24, 30, 24, 30));
        setContentPane(root);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        root.add(titleLabel, BorderLayout.NORTH);

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

        // Локализация кнопок
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

    private LabWork readLabWorkFromForm() {
        String name = nameField.getText().trim();
        String xText = xField.getText().trim();
        String yText = yField.getText().trim();
        String minimalPointText = minimalPointField.getText().trim();
        String disciplineName = disciplineNameField.getText().trim();
        String labsCountText = labsCountField.getText().trim();

        if (name.isEmpty() || xText.isEmpty() || yText.isEmpty() ||
                minimalPointText.isEmpty() || disciplineName.isEmpty() || labsCountText.isEmpty()) {
            throw new IllegalArgumentException(LocaleManager.get("message.fill.all"));
        }

        int x, y, labsCount;
        float minimalPoint;

        try {
            x = Integer.parseInt(xText);
            y = Integer.parseInt(yText);
            minimalPoint = Float.parseFloat(minimalPointText.replace(",", "."));
            labsCount = Integer.parseInt(labsCountText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(LocaleManager.get("message.number.error"));
        }

        if (minimalPoint <= 0) {
            throw new IllegalArgumentException(LocaleManager.get("validation.minimal.point.positive"));
        }

        if (labsCount <= 0) {
            throw new IllegalArgumentException(LocaleManager.get("validation.labs.count.positive"));
        }

        Difficulty difficulty = (Difficulty) difficultyBox.getSelectedItem();

        return new LabWork(
                name,
                new Coordinates(x, y),
                minimalPoint,
                difficulty,
                new Discipline(disciplineName, labsCount),
                null
        );
    }

    private void save() {
        try {
            LabWork labWork = readLabWorkFromForm();
            String[] args = buildArgs(labWork);

            var response = clientService.executeLabWorkCommand(commandName, labWork, args);

            // Локализованное сообщение от сервера
            String localizedMessage = clientService.getResponseText(response);
            JOptionPane.showMessageDialog(this, localizedMessage);

            if (clientService.isSuccess(response)) {
                refreshCallback.run();
                dispose();
            }

        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage()); // ex.getMessage() уже локализован
        } catch (Exception ex) {
            errorLabel.setText(LocaleManager.get("login.connection.error"));
        }
    }

    private String[] buildArgs(LabWork labWork) {
        return new String[]{
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