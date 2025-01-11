package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MonitorizareConsumFrame extends JFrame {

    public MonitorizareConsumFrame() {
        setTitle("Monitorizare Consum");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        String[] columnConsum = {"ID Consum", "ID Utilizator", "Lună", "An", "Consum (m³)"};
        DefaultTableModel modelConsum = new DefaultTableModel(columnConsum, 0);
        JTable tableConsum = new JTable(modelConsum);
        panel.add(new JScrollPane(tableConsum), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        JButton btnStergeConsum = new JButton("Șterge Consum");
        buttonsPanel.add(btnStergeConsum);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        JButton btnInchide = new JButton("Închide");
    
        buttonsPanel.add(btnInchide);
        

        add(panel);

        // Funcționalitate: Închidere
        btnInchide.addActionListener(e -> dispose());
        
        add(panel);

        // Încărcare date consum
        incarcaConsum(modelConsum);

        // Funcționalitate: Ștergere consum
        btnStergeConsum.addActionListener(e -> {
            int selectedRow = tableConsum.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectați o înregistrare pentru ștergere!");
                return;
            }

            int idConsum = (int) modelConsum.getValueAt(selectedRow, 0);
            String query = "DELETE FROM consum WHERE id = ?";

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, idConsum);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Consum șters cu succes!");
                incarcaConsum(modelConsum);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Eroare la ștergerea consumului!");
                ex.printStackTrace();
            }
        });
    }

    private void incarcaConsum(DefaultTableModel modelConsum) {
        String query = "SELECT id, id_utilizator, luna, an, consum FROM consum";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            modelConsum.setRowCount(0);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idUtilizator = resultSet.getInt("id_utilizator");
                String luna = resultSet.getString("luna");
                int an = resultSet.getInt("an");
                double consum = resultSet.getDouble("consum");
                modelConsum.addRow(new Object[]{id, idUtilizator, luna, an, consum});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea consumului!");
            e.printStackTrace();
        }
    }
}
