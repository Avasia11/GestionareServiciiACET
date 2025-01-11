package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VizualizareRapoarteFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public VizualizareRapoarteFrame() {
        setTitle("Vizualizare Rapoarte");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Tabel pentru rapoarte
        String[] columnNames = {"ID Raport", "ID Utilizator", "Prioritate", "Raport"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Butoane
        JPanel buttonPanel = new JPanel();
        JButton btnStergeRaport = new JButton("Șterge Raport");
        JButton btnInchide = new JButton("Închide");

        buttonPanel.add(btnStergeRaport);
        buttonPanel.add(btnInchide);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Încărcare rapoarte
        incarcaRapoarte();

        // Funcționalități butoane
        btnStergeRaport.addActionListener(e -> stergeRaport());
        btnInchide.addActionListener(e -> dispose());
    }

    private void incarcaRapoarte() {
        String query = "SELECT * FROM rapoarte";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            tableModel.setRowCount(0); // Golește tabelul
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idUtilizator = resultSet.getInt("id_utilizator");
                String prioritate = resultSet.getString("prioritate");
                String raport = resultSet.getString("raport");

                tableModel.addRow(new Object[]{id, idUtilizator, prioritate, raport});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea rapoartelor!");
            e.printStackTrace();
        }
    }

    private void stergeRaport() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectați un raport pentru ștergere!");
            return;
        }

        int idRaport = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM rapoarte WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idRaport);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Raport șters cu succes!");
            incarcaRapoarte(); // Reîncarcă tabelul după ștergere

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la ștergerea raportului!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VizualizareRapoarteFrame frame = new VizualizareRapoarteFrame();
            frame.setVisible(true);
        });
    }
}
