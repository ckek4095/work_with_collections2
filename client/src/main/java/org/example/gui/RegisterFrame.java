package org.example.gui;

import org.example.Request;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final GuiClientService clientService;
    private final LoginFrame loginFrame;

    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JComboBox<String> languageBox;
    private JLabel errorLabel;

    public RegisterFrame(GuiClientService clientService, LoginFrame loginFrame) {
        this.clientService = clientService;
        this.loginFrame = loginFrame;

        setTitle("LabWork Manager");
        setSize(900, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 600));

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(55, 0, 35, 0));

        JLabel titleLabel = new JLabel("Регистрация");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(460, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(18));
        centerPanel.add(separator);
        centerPanel.add(Box.createVerticalStrut(40));

        loginField = new JTextField();
        passwordField = new JPasswordField();
        repeatPasswordField = new JPasswordField();

        centerPanel.add(createFieldRow("Логин:", loginField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createFieldRow("Пароль:", passwordField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createFieldRow("Повторите пароль:", repeatPasswordField));
        centerPanel.add(Box.createVerticalStrut(30));

        JPanel requirementsPanel = createRequirementsPanel();
        requirementsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(requirementsPanel);

        centerPanel.add(Box.createVerticalStrut(28));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonsPanel.setBackground(Color.WHITE);

        JButton registerButton = new JButton("Зарегистрироваться");
        JButton cancelButton = new JButton("Отмена");

        styleMainButton(registerButton);
        styleSecondaryButton(cancelButton);

        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> backToLogin());

        buttonsPanel.add(registerButton);
        buttonsPanel.add(cancelButton);

        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalStrut(22));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(180, 40, 40));
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(errorLabel);

        centerPanel.add(Box.createVerticalStrut(25));

        JSeparator bottomSeparator = new JSeparator();
        bottomSeparator.setMaximumSize(new Dimension(460, 1));
        bottomSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(bottomSeparator);
        centerPanel.add(Box.createVerticalStrut(22));

        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        languagePanel.setBackground(Color.WHITE);

        JLabel languageLabel = new JLabel("Язык:");
        languageLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        languageBox = new JComboBox<>(new String[]{
                "Русский (Россия)",
                "Белорусский",
                "Латышский",
                "English (Ireland)"
        });
        languageBox.setPreferredSize(new Dimension(250, 36));

        languagePanel.add(languageLabel);
        languagePanel.add(languageBox);

        centerPanel.add(languagePanel);

        root.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFieldRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        row.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 17));
        label.setPreferredSize(new Dimension(170, 42));

        field.setPreferredSize(new Dimension(360, 42));
        field.setFont(new Font("Arial", Font.PLAIN, 16));

        row.add(label);
        row.add(field);

        return row;
    }

    private JPanel createRequirementsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 250, 253));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 205, 225)),
                new EmptyBorder(18, 22, 18, 22)
        ));
        panel.setMaximumSize(new Dimension(530, 120));

        JLabel title = new JLabel("Требования к паролю:");
        title.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel r1 = new JLabel("• не менее 4 символов");
        JLabel r2 = new JLabel("• логин не менее 3 символов");
        JLabel r3 = new JLabel("• пароль не должен быть пустым");

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        r1.setAlignmentX(Component.LEFT_ALIGNMENT);
        r2.setAlignmentX(Component.LEFT_ALIGNMENT);
        r3.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(r1);
        panel.add(Box.createVerticalStrut(5));
        panel.add(r2);
        panel.add(Box.createVerticalStrut(5));
        panel.add(r3);

        return panel;
    }

    private void styleMainButton(JButton button) {
        button.setPreferredSize(new Dimension(220, 46));
        button.setFont(new Font("Arial", Font.PLAIN, 17));
        button.setBackground(new Color(30, 105, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void styleSecondaryButton(JButton button) {
        button.setPreferredSize(new Dimension(180, 46));
        button.setFont(new Font("Arial", Font.PLAIN, 17));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
    }

    private void register() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());
        String repeatPassword = new String(repeatPasswordField.getPassword());

        if (login.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        if (!password.equals(repeatPassword)) {
            errorLabel.setText("Пароли не совпадают");
            return;
        }

        try {
            Request response = clientService.register(login, password);

            if (clientService.isSuccess(response)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Регистрация выполнена успешно",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE
                );
                backToLogin();
            } else {
                errorLabel.setText(clientService.getResponseText(response));
            }

        } catch (Exception ex) {
            errorLabel.setText("Ошибка подключения к серверу");
        }
    }

    private void backToLogin() {
        loginFrame.setVisible(true);
        dispose();
    }
}