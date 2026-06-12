package org.example.gui;

import org.example.Request;
import org.example.gui.localization.GuiLocale;
import org.example.gui.localization.LocaleManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final GuiClientService clientService;

    private JTextField loginField;
    private JPasswordField passwordField;
    private JComboBox<GuiLocale> languageBox;
    private JLabel errorLabel;
    private JLabel titleLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel languageLabel;

    private JButton loginButton;
    private JButton registerButton;

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

        titleLabel = new JLabel();
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

        loginLabel = new JLabel();
        passwordLabel = new JLabel();

        centerPanel.add(createFieldRow(loginLabel, loginField));
        centerPanel.add(Box.createVerticalStrut(22));
        centerPanel.add(createFieldRow(passwordLabel, passwordField));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonsPanel.setBackground(Color.WHITE);

        loginButton = new JButton();
        registerButton = new JButton();

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

        languageLabel = new JLabel();
        languageLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        languageBox = new JComboBox<>(GuiLocale.values());
        languageBox.setSelectedItem(LocaleManager.getCurrentGuiLocale());
        languageBox.addActionListener(e -> {
            GuiLocale selected = (GuiLocale) languageBox.getSelectedItem();
            LocaleManager.setCurrentLocale(selected);
        });
        languageBox.setPreferredSize(new Dimension(250, 36));

        languagePanel.add(languageLabel);
        languagePanel.add(languageBox);

        centerPanel.add(languagePanel);

        root.add(centerPanel, BorderLayout.CENTER);

        updateTexts();
        LocaleManager.addLocaleChangeListener(this::updateTexts);
    }

    private JPanel createFieldRow(JLabel label, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        row.setBackground(Color.WHITE);

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
            errorLabel.setText(LocaleManager.get("login.empty"));
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
                // Используем локализацию
                String localizedMessage = clientService.getResponseText(response);
                errorLabel.setText(localizedMessage);
            }

        } catch (Exception ex) {
            errorLabel.setText(LocaleManager.get("login.connection.error"));
        }
    }

    private String localizeServerMessage(String serverMessage) {
        if (serverMessage == null || serverMessage.isEmpty()) {
            return serverMessage;
        }

        if (serverMessage.contains("Ошибка авторизации: неверный логин или пароль")){
            return LocaleManager.get("server.invalid.credentials");
        }

        if (serverMessage.contains("Время ожидания ответа истекло")) {
            return LocaleManager.get("server.timeout.error");
        }

        return serverMessage;
    }

    private void openRegisterWindow() {
        RegisterFrame registerFrame = new RegisterFrame(clientService, this);
        registerFrame.setVisible(true);
        setVisible(false);
    }

    private void updateTexts() {
        setTitle(LocaleManager.get("app.title"));

        if (titleLabel != null) {
            titleLabel.setText(LocaleManager.get("login.title"));
        }

        if (loginLabel != null) {
            loginLabel.setText(LocaleManager.get("login.login"));
        }

        if (passwordLabel != null) {
            passwordLabel.setText(LocaleManager.get("login.password"));
        }

        if (loginButton != null) {
            loginButton.setText(LocaleManager.get("login.button"));
        }

        if (registerButton != null) {
            registerButton.setText(LocaleManager.get("login.register"));
        }

        if (languageLabel != null) {
            languageLabel.setText(LocaleManager.get("login.language"));
        }
    }
}