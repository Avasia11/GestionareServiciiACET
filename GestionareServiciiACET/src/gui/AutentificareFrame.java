package gui;

import model.Utilizator;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AutentificareFrame extends JFrame {

    public AutentificareFrame() {
        setTitle("Autentificare");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new GridLayout(3, 2));

        // Elemente UI
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel parolaLabel = new JLabel("Parola:");
        JPasswordField parolaField = new JPasswordField();
        JButton loginButton = new JButton("Autentificare");

        add(emailLabel);
        add(emailField);
        add(parolaLabel);
        add(parolaField);
        add(new JLabel());
        add(loginButton);

        // Acțiune la apăsarea butonului
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String parola = new String(parolaField.getPassword());
            autentificare(email, parola);
        });
    }

    private void autentificare(String email, String parola) {
        String query = "SELECT * FROM utilizatori WHERE email = ? AND parola = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, parola);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                String tipUtilizator = resultSet.getString("tip_utilizator");

                // Creăm un obiect Utilizator
                Utilizator utilizator = new Utilizator(id, nume, email, tipUtilizator);

                // Mesaj de succes
                JOptionPane.showMessageDialog(this, "Autentificare reușită!");

                // Deschidere panou pe baza tipului de utilizator
                if (tipUtilizator.equals("admin")) {
                    JFrame adminFrame = new AdminFrame();
                    adminFrame.setVisible(true);
                } else if (tipUtilizator.equals("client")) {
                    JFrame clientFrame = new ClientFrame(id);
                    clientFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Tip de utilizator necunoscut!");
                }

                // Închide fereastra curentă după autentificare
                dispose();
            } else {
                // Mesaj pentru autentificare eșuată
                JOptionPane.showMessageDialog(this, "Email sau parolă incorectă!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la conectarea bazei de date!");
            e.printStackTrace();
        }
    }
}
