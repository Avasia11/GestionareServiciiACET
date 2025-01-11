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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ClientFrame extends JFrame {

    private int idUtilizator;

    public ClientFrame(int idUtilizator) {
        this.idUtilizator = idUtilizator;

        setTitle("Panou Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Test conexiune bază de date
        if (!testConexiune()) {
            JOptionPane.showMessageDialog(this, "Eroare: Nu s-a putut face conexiunea la baza de date!");
            dispose();
            return;
        }

        // Tab-uri pentru funcționalități
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab pentru vizualizare consum
        JPanel consumPanel = new JPanel(new BorderLayout());
        String[] columnConsum = {"Lună", "An", "Consum (m³)"};
        DefaultTableModel modelConsum = new DefaultTableModel(columnConsum, 0);
        JTable tableConsum = new JTable(modelConsum);
        consumPanel.add(new JScrollPane(tableConsum), BorderLayout.CENTER);
        tabbedPane.add("Consum", consumPanel);

        // Tab pentru vizualizare facturi
        JPanel facturiPanel = new JPanel(new BorderLayout());
        String[] columnFacturi = {"Perioadă", "Sumă (lei)"};
        DefaultTableModel modelFacturi = new DefaultTableModel(columnFacturi, 0);
        JTable tableFacturi = new JTable(modelFacturi);
        JButton btnGenereazaPDF = new JButton("Generează PDF Factură");
        facturiPanel.add(new JScrollPane(tableFacturi), BorderLayout.CENTER);
        facturiPanel.add(btnGenereazaPDF, BorderLayout.SOUTH);
        tabbedPane.add("Facturi", facturiPanel);

        // Tab pentru introducerea citirii contorului
        JPanel citireContorPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        JLabel lblCitireCurenta = new JLabel("Introduceți citirea curentă (m³):");
        JTextField txtCitireCurenta = new JTextField();
        JLabel lblLuna = new JLabel("Lună:");
        JComboBox<String> cmbLuna = new JComboBox<>(new String[]{
                "Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie",
                "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"});
        JLabel lblAn = new JLabel("An:");
        JTextField txtAn = new JTextField();
        JButton btnTrimiteCitire = new JButton("Trimite Citirea");

        citireContorPanel.add(lblCitireCurenta);
        citireContorPanel.add(txtCitireCurenta);
        citireContorPanel.add(lblLuna);
        citireContorPanel.add(cmbLuna);
        citireContorPanel.add(lblAn);
        citireContorPanel.add(txtAn);
        citireContorPanel.add(new JLabel()); // Spațiu liber
        citireContorPanel.add(btnTrimiteCitire);
        tabbedPane.add("Citire Contor", citireContorPanel);

        // Tab pentru raportare probleme
        JPanel raportarePanel = new JPanel(new BorderLayout());
        JTextArea raportareText = new JTextArea();
        JComboBox<String> prioritateCombo = new JComboBox<>(new String[]{"Scăzută", "Mediu", "Urgentă"});
        JButton btnTrimiteRaport = new JButton("Trimite Raport");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Prioritate:"), BorderLayout.WEST);
        inputPanel.add(prioritateCombo, BorderLayout.CENTER);

        raportarePanel.add(new JScrollPane(raportareText), BorderLayout.CENTER);
        raportarePanel.add(inputPanel, BorderLayout.NORTH);
        raportarePanel.add(btnTrimiteRaport, BorderLayout.SOUTH);
        tabbedPane.add("Raportare Probleme", raportarePanel);

        // Tab pentru vizualizare rapoarte trimise
        JPanel rapoartePanel = new JPanel(new BorderLayout());
        String[] columnRapoarte = {"ID Raport", "Prioritate", "Raport"};
        DefaultTableModel modelRapoarte = new DefaultTableModel(columnRapoarte, 0);
        JTable tableRapoarte = new JTable(modelRapoarte);
        rapoartePanel.add(new JScrollPane(tableRapoarte), BorderLayout.CENTER);
        tabbedPane.add("Rapoarte Trimise", rapoartePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Încărcare date
        incarcaConsum(modelConsum);
        incarcaFacturi(modelFacturi);
        incarcaRapoarte(modelRapoarte);

        // Funcționalitate pentru trimiterea citirii contorului
        btnTrimiteCitire.addActionListener(e -> {
            String citireText = txtCitireCurenta.getText();
            String luna = cmbLuna.getSelectedItem().toString();
            String anText = txtAn.getText();

            if (citireText.isEmpty() || anText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduceți toate câmpurile!");
                return;
            }

            try {
                double citireCurenta = Double.parseDouble(citireText);
                int an = Integer.parseInt(anText);
                double tarif = 5.0; // Exemplu de tarif
                trimiteCitireContor(citireCurenta, luna, an, tarif);
                txtCitireCurenta.setText("");
                txtAn.setText("");
                incarcaConsum(modelConsum);
                incarcaFacturi(modelFacturi);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduceți valori numerice valide pentru consum și an!");
            }
        });

        // Funcționalitate pentru generarea PDF-ului facturii
        btnGenereazaPDF.addActionListener(e -> genereazaPDFFactura(tableFacturi));

        // Funcționalitate pentru trimiterea raportului
        btnTrimiteRaport.addActionListener(e -> {
            String raport = raportareText.getText();
            if (raport.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduceți textul raportului!");
                return;
            }
            String prioritate = prioritateCombo.getSelectedItem().toString();
            trimiteRaport(prioritate, raport);
            raportareText.setText("");
            incarcaRapoarte(modelRapoarte);
        });
    }

    private boolean testConexiune() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "")) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void incarcaConsum(DefaultTableModel modelConsum) {
        String query = "SELECT luna, an, consum FROM consum WHERE id_utilizator = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idUtilizator);
            ResultSet resultSet = preparedStatement.executeQuery();

            modelConsum.setRowCount(0);
            while (resultSet.next()) {
                String luna = resultSet.getString("luna");
                int an = resultSet.getInt("an");
                double consum = resultSet.getDouble("consum");
                modelConsum.addRow(new Object[]{luna, an, consum});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea consumului!");
            e.printStackTrace();
        }
    }

    private void incarcaFacturi(DefaultTableModel modelFacturi) {
        String query = "SELECT perioada, suma FROM facturi WHERE id_utilizator = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idUtilizator);
            ResultSet resultSet = preparedStatement.executeQuery();

            modelFacturi.setRowCount(0);
            while (resultSet.next()) {
                String perioada = resultSet.getString("perioada");
                double suma = resultSet.getDouble("suma");
                modelFacturi.addRow(new Object[]{perioada, suma});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea facturilor!");
            e.printStackTrace();
        }
    }

    private void incarcaRapoarte(DefaultTableModel modelRapoarte) {
        String query = "SELECT id, prioritate, raport FROM rapoarte WHERE id_utilizator = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idUtilizator);
            ResultSet resultSet = preparedStatement.executeQuery();

            modelRapoarte.setRowCount(0);
            while (resultSet.next()) {
                int idRaport = resultSet.getInt("id");
                String prioritate = resultSet.getString("prioritate");
                String raport = resultSet.getString("raport");
                modelRapoarte.addRow(new Object[]{idRaport, prioritate, raport});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea rapoartelor!");
            e.printStackTrace();
        }
    }

   private void genereazaPDFFactura(JTable tableFacturi) {
    int selectedRow = tableFacturi.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Selectați o factură pentru a genera PDF!");
        return;
    }

    String perioada = tableFacturi.getValueAt(selectedRow, 0).toString();
    double suma = (double) tableFacturi.getValueAt(selectedRow, 1);

    String query = "SELECT utilizatori.nume, utilizatori.email, consum.luna, consum.an, consum.consum " +
            "FROM utilizatori " +
            "JOIN consum ON utilizatori.id = consum.id_utilizator " +
            "WHERE utilizatori.id = ? AND CONCAT(consum.luna, ' ', consum.an) = ?";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setInt(1, idUtilizator);
        preparedStatement.setString(2, perioada);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String nume = resultSet.getString("nume");
            String email = resultSet.getString("email");
            String luna = resultSet.getString("luna");
            int an = resultSet.getInt("an");
            double consum = resultSet.getDouble("consum");

            // Afișează dialog pentru salvarea fișierului
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvați factura ca PDF");
            fileChooser.setSelectedFile(new java.io.File("Factura_" + perioada + ".pdf"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                // Generează PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));

                document.open();
                document.add(new Paragraph("Factura"));
                document.add(new Paragraph("Nume: " + nume));
                document.add(new Paragraph("Email: " + email));
                document.add(new Paragraph("Luna: " + luna));
                document.add(new Paragraph("An: " + an));
                document.add(new Paragraph("Consum: " + consum + " m³"));
                document.add(new Paragraph("Suma: " + suma + " lei"));
                document.close();

                JOptionPane.showMessageDialog(this, "Factura a fost salvată cu succes la: " + filePath);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nu s-au găsit detalii pentru factura selectată!");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Eroare la generarea PDF-ului!");
        e.printStackTrace();
    }
}


    private void trimiteCitireContor(double citireCurenta, String luna, int an, double tarif) {
        String queryConsum = "INSERT INTO consum (id_utilizator, luna, an, consum) VALUES (?, ?, ?, ?)";
        String queryFactura = "INSERT INTO facturi (id_utilizator, perioada, suma) VALUES (?, ?, ?)";

        String perioada = luna + " " + an;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatementConsum = connection.prepareStatement(queryConsum);
             PreparedStatement preparedStatementFactura = connection.prepareStatement(queryFactura)) {

            preparedStatementConsum.setInt(1, idUtilizator);
            preparedStatementConsum.setString(2, luna);
            preparedStatementConsum.setInt(3, an);
            preparedStatementConsum.setDouble(4, citireCurenta);
            preparedStatementConsum.executeUpdate();

            double suma = citireCurenta * tarif;
            preparedStatementFactura.setInt(1, idUtilizator);
            preparedStatementFactura.setString(2, perioada);
            preparedStatementFactura.setDouble(3, suma);
            preparedStatementFactura.executeUpdate();

            JOptionPane.showMessageDialog(this, "Citirea și factura au fost salvate cu succes!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la salvarea citirii sau facturii!");
            e.printStackTrace();
        }
    }

    private void trimiteRaport(String prioritate, String raport) {
        String query = "INSERT INTO rapoarte (id_utilizator, prioritate, raport) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/acet_suceava", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idUtilizator);
            preparedStatement.setString(2, prioritate);
            preparedStatement.setString(3, raport);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Raport trimis cu succes!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la trimiterea raportului!");
            e.printStackTrace();
        }
    }
}
