package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

public class GenerareFacturiFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public GenerareFacturiFrame() {
        setTitle("Generare Facturi");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Tabel pentru facturi
        String[] columnNames = {"ID", "ID Utilizator", "Perioadă", "Sumă"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Butoane
        JPanel buttonPanel = new JPanel();
        JButton btnGenereazaFactura = new JButton("Generează Factură");
        JButton btnStergeFactura = new JButton("Șterge Factură");
        JButton btnGenereazaPDF = new JButton("Generează PDF");
        JButton btnInchide = new JButton("Închide");

        buttonPanel.add(btnGenereazaFactura);
        buttonPanel.add(btnStergeFactura);
        buttonPanel.add(btnGenereazaPDF);
        buttonPanel.add(btnInchide);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Încărcare facturi inițiale
        incarcaFacturi();

        // Funcționalități butoane
        btnGenereazaFactura.addActionListener(e -> genereazaFactura());
        btnStergeFactura.addActionListener(e -> stergeFactura());
        btnGenereazaPDF.addActionListener(e -> genereazaPDF());
        btnInchide.addActionListener(e -> dispose());
    }

    private void incarcaFacturi() {
        String query = "SELECT * FROM facturi";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            tableModel.setRowCount(0); // Golește tabelul
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idUtilizator = resultSet.getInt("id_utilizator");
                String perioada = resultSet.getString("perioada");
                double suma = resultSet.getDouble("suma");

                tableModel.addRow(new Object[]{id, idUtilizator, perioada, suma});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea facturilor!");
            e.printStackTrace();
        }
    }

    private void genereazaFactura() {
        try {
            // Introducere date manual
            int idUtilizator = Integer.parseInt(JOptionPane.showInputDialog(this, "Introduceți ID-ul utilizatorului:"));
            String luna = JOptionPane.showInputDialog(this, "Introduceți luna (ex: Ianuarie):");
            String an = JOptionPane.showInputDialog(this, "Introduceți anul (ex: 2023):");
            
            
            double consum = Double.parseDouble(JOptionPane.showInputDialog(this, "Introduceți consumul (m³):"));

            // Validare intrare
            if (an.isEmpty() || luna.isEmpty() || consum <= 0) {
                JOptionPane.showMessageDialog(this, "Toate câmpurile sunt obligatorii!");
                return;
            }

            // Perioadă în format "2023 Ianuarie"
            String perioada = luna + " " + an;

            // Calcul sumă
            double suma = consum * 5.0;

            // Adăugare factură în tabelul facturi
            String insertFactura = "INSERT INTO facturi (id_utilizator, perioada, suma) VALUES (?, ?, ?)";
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
                 PreparedStatement preparedStatementFactura = connection.prepareStatement(insertFactura)) {

                preparedStatementFactura.setInt(1, idUtilizator);
                preparedStatementFactura.setString(2, perioada);
                preparedStatementFactura.setDouble(3, suma);
                preparedStatementFactura.executeUpdate();
            }

            // Adăugare date în tabelul consum
            String insertConsum = "INSERT INTO consum (id_utilizator, luna, an, consum) VALUES (?, ?, ?, ?)";
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
                 PreparedStatement preparedStatementConsum = connection.prepareStatement(insertConsum)) {

                preparedStatementConsum.setInt(1, idUtilizator);
                preparedStatementConsum.setString(2, luna);
                preparedStatementConsum.setInt(3, Integer.parseInt(an));
                preparedStatementConsum.setDouble(4, consum);
                preparedStatementConsum.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Factura generată cu succes!\nSuma: " + suma + " lei");

            // Reîncărcare tabele
            incarcaFacturi();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la generarea facturii! Verificați datele introduse.");
            e.printStackTrace();
        }
    }

    private void stergeFactura() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectați o factură pentru ștergere!");
            return;
        }

        int idFactura = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM facturi WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idFactura);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Factură ștearsă cu succes!");
            incarcaFacturi(); // Reîncarcă tabelul facturi

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la ștergerea facturii!");
            e.printStackTrace();
        }
    }

 private void genereazaPDF() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Selectați o factură pentru generare PDF!");
        return;
    }

    int idFactura = (int) tableModel.getValueAt(selectedRow, 0);
    int idUtilizator = (int) tableModel.getValueAt(selectedRow, 1);
    String perioada = tableModel.getValueAt(selectedRow, 2).toString();
    double suma = (double) tableModel.getValueAt(selectedRow, 3);

    // Separăm perioada în lună și an (format "Ianuarie 2023")
    String[] perioadaParts = perioada.split(" ");
    if (perioadaParts.length != 2) {
        JOptionPane.showMessageDialog(this, "Formatul perioadei este incorect!");
        return;
    }
    String luna = perioadaParts[0]; // Ex. Ianuarie
    int an = Integer.parseInt(perioadaParts[1]); // Ex. 2023

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Salvează factura ca PDF");
    fileChooser.setSelectedFile(new java.io.File("Factura_" + idFactura + ".pdf"));

    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection != JFileChooser.APPROVE_OPTION) {
        return;
    }

    String filePath = fileChooser.getSelectedFile().getAbsolutePath();

    String query = "SELECT utilizatori.nume, utilizatori.email, consum.consum " +
                   "FROM utilizatori " +
                   "JOIN consum ON utilizatori.id = consum.id_utilizator " +
                   "WHERE utilizatori.id = ? AND consum.luna = ? AND consum.an = ?";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setInt(1, idUtilizator);
        preparedStatement.setString(2, luna);
        preparedStatement.setInt(3, an);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String nume = resultSet.getString("nume");
            String email = resultSet.getString("email");
            double consum = resultSet.getDouble("consum");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            document.open();
            document.add(new Paragraph("Factura #" + idFactura));
            document.add(new Paragraph("Nume Utilizator: " + nume));
            document.add(new Paragraph("Email: " + email));
            document.add(new Paragraph("Lună: " + luna));
            document.add(new Paragraph("An: " + an));
            document.add(new Paragraph("Consum (m³): " + consum));
            document.add(new Paragraph("Nota de plată: " + suma + " lei"));
            document.close();

            JOptionPane.showMessageDialog(this, "Factura salvată cu succes la: " + filePath);
        } else {
            JOptionPane.showMessageDialog(this, "Nu s-au găsit detalii pentru utilizator și consum!");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Eroare la generarea PDF-ului!");
        e.printStackTrace();
    }
}



    
}
