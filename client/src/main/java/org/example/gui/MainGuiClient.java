package org.example.gui;

import javax.swing.*;

public class MainGuiClient {
    private static final String HOST = "localhost";
    private static final int PORT = 60000;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GuiClientService clientService = new GuiClientService(HOST, PORT);
                LoginFrame loginFrame = new LoginFrame(clientService);
                loginFrame.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Не удалось запустить GUI-клиент:\n" + e.getMessage(),
                        "Ошибка запуска",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}