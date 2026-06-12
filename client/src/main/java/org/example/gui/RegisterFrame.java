package org.example.gui;

import org.example.Request;
import org.example.gui.localization.GuiLocale;
import org.example.gui.localization.LocaleManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final GuiClientService clientService;
    private final LoginFrame loginFrame;

    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JLabel errorLabel;
    private JLabel titleLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel repeatPasswordLabel;
    private JLabel languageLabel;

    private JButton registerButton;
    private JButton cancelButton;

    private JComboBox<GuiLocale> languageBox;

    private JLabel passwordRequirementsLabel;
    private JLabel passwordRule1Label;
    private JLabel passwordRule2Label;
    private JLabel passwordRule3Label;

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

        titleLabel = new JLabel();
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

        loginLabel = new JLabel();
        passwordLabel = new JLabel();
        repeatPasswordLabel = new JLabel();

        centerPanel.add(createFieldRow(loginLabel, loginField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createFieldRow(passwordLabel, passwordField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createFieldRow(repeatPasswordLabel, repeatPasswordField));

        JPanel requirementsPanel = createRequirementsPanel();
        requirementsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(requirementsPanel);

        centerPanel.add(Box.createVerticalStrut(28));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonsPanel.setBackground(Color.WHITE);

        registerButton = new JButton();
        cancelButton = new JButton();

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

        passwordRequirementsLabel = new JLabel();
        passwordRequirementsLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        passwordRule1Label = new JLabel();
        passwordRule2Label = new JLabel();
        passwordRule3Label = new JLabel();

        passwordRequirementsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordRule1Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordRule2Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordRule3Label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(passwordRequirementsLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(passwordRule1Label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordRule2Label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordRule3Label);

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
            errorLabel.setText(LocaleManager.get("register.empty"));
            return;
        }

        if (!password.equals(repeatPassword)) {
            errorLabel.setText(LocaleManager.get("register.passwords.not.match"));
            return;
        }

        try {
            Request response = clientService.register(login, password);

            if (clientService.isSuccess(response)) {
                JOptionPane.showMessageDialog(
                        this,
                        LocaleManager.get("register.success"),
                        LocaleManager.get("dialog.success.title"),
                        JOptionPane.INFORMATION_MESSAGE
                );
                backToLogin();
            } else {
                // Используем локализацию
                String localizedMessage = clientService.getResponseText(response);
                errorLabel.setText(localizedMessage);
            }

        } catch (Exception ex) {
            errorLabel.setText(LocaleManager.get("login.connection.error"));
        }
    }

    private void backToLogin() {
        loginFrame.setVisible(true);
        dispose();
    }

    private void updateTexts() {
        setTitle(LocaleManager.get("app.title"));

        if (titleLabel != null) {
            titleLabel.setText(LocaleManager.get("register.title"));
        }

        if (loginLabel != null) {
            loginLabel.setText(LocaleManager.get("login.login"));
        }

        if (passwordLabel != null) {
            passwordLabel.setText(LocaleManager.get("login.password"));
        }

        if (repeatPasswordLabel != null) {
            repeatPasswordLabel.setText(LocaleManager.get("register.repeat.password"));
        }

        if (registerButton != null) {
            registerButton.setText(LocaleManager.get("register.button"));
        }

        if (cancelButton != null) {
            cancelButton.setText(LocaleManager.get("register.cancel"));
        }

        if (languageLabel != null) {
            languageLabel.setText(LocaleManager.get("login.language"));
        }

        if (passwordRequirementsLabel != null) {
            passwordRequirementsLabel.setText(LocaleManager.get("password_requirements"));
        }

        if (passwordRule1Label != null) {
            passwordRule1Label.setText(LocaleManager.get("password_rule_1"));
        }

        if (passwordRule2Label != null) {
            passwordRule2Label.setText(LocaleManager.get("password_rule_2"));
        }

        if (passwordRule3Label != null) {
            passwordRule3Label.setText(LocaleManager.get("password_rule_3"));
        }
    }

    private String localizeServerMessage(String serverMessage) {
        if (serverMessage == null || serverMessage.isEmpty()) {
            return serverMessage;
        }

        // Проверка на неверные учетные данные
        if (serverMessage.contains("Ошибка авторизации: неверный логин или пароль")){
            return LocaleManager.get("server.invalid.credentials");
        }

        // Проверка на ошибку подкл
        if (serverMessage.contains("Время ожидания ответа истекло")) {
            return LocaleManager.get("server.timeout.error");
        }

        if (serverMessage.contains("Ошибка: пароль должен содержать не менее 4 символов")) {
            return LocaleManager.get("server.password.error");
        }

        if (serverMessage.contains("Ошибка: логин должен содержать не менее 3 символов")) {
            return LocaleManager.get("server.login.error");
        }


        return serverMessage;
    }
}