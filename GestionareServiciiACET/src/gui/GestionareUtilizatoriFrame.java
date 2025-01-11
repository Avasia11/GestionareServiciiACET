package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GestionareUtilizatoriFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public GestionareUtilizatoriFrame() {
        setTitle("Gestionare Utilizatori");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Tabel pentru utilizatori
        String[] columnNames = {"ID", "Nume", "Email", "Tip Utilizator"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Butoane
        JPanel buttonPanel = new JPanel();
        JButton btnAdauga = new JButton("Adaugă");
        JButton btnEditeaza = new JButton("Editează");
        JButton btnSterge = new JButton("Șterge");
        JButton btnInchide = new JButton("Închide"); // Adăugăm butonul de închidere

        buttonPanel.add(btnAdauga);
        buttonPanel.add(btnEditeaza);
        buttonPanel.add(btnSterge);
        buttonPanel.add(btnInchide); // Adăugăm butonul de închidere la panel

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Încărcare utilizatori
        incarcaUtilizatori();

        // Funcționalități butoane
        btnAdauga.addActionListener(e -> adaugaUtilizator());
        btnEditeaza.addActionListener(e -> editeazaUtilizator());
        btnSterge.addActionListener(e -> stergeUtilizator());
        btnInchide.addActionListener(e -> dispose()); // Funcționalitate buton Închide
    }

    private void incarcaUtilizatori() {
        String query = "SELECT * FROM utilizatori";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            tableModel.setRowCount(0); // Golește tabelul
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                String email = resultSet.getString("email");
                String tipUtilizator = resultSet.getString("tip_utilizator");

                tableModel.addRow(new Object[]{id, nume, email, tipUtilizator});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea utilizatorilor!");
            e.printStackTrace();
        }
    }

    private void adaugaUtilizator() {
        String nume = JOptionPane.showInputDialog(this, "Introduceți numele utilizatorului:");
        String email = JOptionPane.showInputDialog(this, "Introduceți email-ul utilizatorului:");
        String parola = JOptionPane.showInputDialog(this, "Introduceți parola utilizatorului:");
        String[] tipuri = {"admin", "client"};
        String tipUtilizator = (String) JOptionPane.showInputDialog(this, "Selectați tipul utilizatorului:", 
                "Tip Utilizator", JOptionPane.QUESTION_MESSAGE, null, tipuri, tipuri[1]);

        String query = "INSERT INTO utilizatori (nume, email, parola, tip_utilizator) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nume);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, parola);
            preparedStatement.setString(4, tipUtilizator);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Utilizator adăugat cu succes!");
            incarcaUtilizatori();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la adăugarea utilizatorului!");
            e.printStackTrace();
        }
    }

    private void editeazaUtilizator() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectați un utilizator pentru editare!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nume = (String) tableModel.getValueAt(selectedRow, 1);
        String email = (String) tableModel.getValueAt(selectedRow, 2);
        String tipUtilizator = (String) tableModel.getValueAt(selectedRow, 3);

        String nouNume = JOptionPane.showInputDialog(this, "Introduceți noul nume:", nume);
        String nouEmail = JOptionPane.showInputDialog(this, "Introduceți noul email:", email);
        String[] tipuri = {"admin", "client"};
        String nouTipUtilizator = (String) JOptionPane.showInputDialog(this, "Selectați tipul utilizatorului:", 
                "Tip Utilizator", JOptionPane.QUESTION_MESSAGE, null, tipuri, tipUtilizator);

        String query = "UPDATE utilizatori SET nume = ?, email = ?, tip_utilizator = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nouNume);
            preparedStatement.setString(2, nouEmail);
            preparedStatement.setString(3, nouTipUtilizator);
            preparedStatement.setInt(4, id);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Utilizator editat cu succes!");
            incarcaUtilizatori();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la editarea utilizatorului!");
            e.printStackTrace();
        }
    }

    private void stergeUtilizator() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectați un utilizator pentru ștergere!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM utilizatori WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Utilizator șters cu succes!");
            incarcaUtilizatori();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la ștergerea utilizatorului!");
            e.printStackTrace();
        }
    }
}
