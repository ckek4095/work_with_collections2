package org.example.gui;

import org.example.Request;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final GuiClientService clientService;

    private JTextField loginField;
    private JPasswordField passwordField;
    private JComboBox<String> languageBox;
    private JLabel errorLabel;

    public LoginFrame(GuiClientService clientService) {
        this.clientService = clientService;

        setTitle("LabWork Manager");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(70, 0, 40, 0));

        JLabel titleLabel = new JLabel("Вход в систему");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(460, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(18));
        centerPanel.add(separator);
        centerPanel.add(Box.createVerticalStrut(45));

        loginField = new JTextField();
        passwordField = new JPasswordField();

        centerPanel.add(createFieldRow("Логин:", loginField));
        centerPanel.add(Box.createVerticalStrut(22));
        centerPanel.add(createFieldRow("Пароль:", passwordField));
        centerPanel.add(Box.createVerticalStrut(35));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonsPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Войти");
        JButton registerButton = new JButton("Зарегистрироваться");

        styleMainButton(loginButton);
        styleSecondaryButton(registerButton);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegisterWindow());

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);

        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalStrut(25));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(180, 40, 40));
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(errorLabel);

        centerPanel.add(Box.createVerticalStrut(35));

        JSeparator bottomSeparator = new JSeparator();
        bottomSeparator.setMaximumSize(new Dimension(460, 1));
        bottomSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(bottomSeparator);
        centerPanel.add(Box.createVerticalStrut(25));

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
        label.setPreferredSize(new Dimension(120, 42));

        field.setPreferredSize(new Dimension(360, 42));
        field.setFont(new Font("Arial", Font.PLAIN, 16));

        row.add(label);
        row.add(field);

        return row;
    }

    private void styleMainButton(JButton button) {
        button.setPreferredSize(new Dimension(190, 46));
        button.setFont(new Font("Arial", Font.PLAIN, 17));
        button.setBackground(new Color(30, 105, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void styleSecondaryButton(JButton button) {
        button.setPreferredSize(new Dimension(230, 46));
        button.setFont(new Font("Arial", Font.PLAIN, 17));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(30, 105, 200));
        button.setFocusPainted(false);
    }

    private void login() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введите логин и пароль");
            return;
        }

        try {
            Request response = clientService.login(login, password);

            if (clientService.isSuccess(response)) {
                errorLabel.setText(" ");

                MainFrame mainFrame = new MainFrame(clientService);
                mainFrame.setVisible(true);
                dispose();

            } else {
                errorLabel.setText(clientService.getResponseText(response));
            }

        } catch (Exception ex) {
            errorLabel.setText("Ошибка подключения к серверу");
        }
    }

    private void openRegisterWindow() {
        RegisterFrame registerFrame = new RegisterFrame(clientService, this);
        registerFrame.setVisible(true);
        setVisible(false);
    }
}