package gui;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    public AdminFrame() {
        setTitle("Panou Administrator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        // Butoane pentru funcționalități
        JButton btnGestionareUtilizatori = new JButton("Gestionare Utilizatori");
        JButton btnMonitorizareConsum = new JButton("Monitorizare Consum");
        JButton btnGenerareFacturi = new JButton("Generare Facturi");
        JButton btnVizualizareRapoarte = new JButton("Vizualizare Rapoarte");

        panel.add(btnGestionareUtilizatori);
        panel.add(btnMonitorizareConsum);
        panel.add(btnGenerareFacturi);
        panel.add(btnVizualizareRapoarte);

        add(panel);

        // Funcționalitate butoane
        btnGestionareUtilizatori.addActionListener(e -> new GestionareUtilizatoriFrame().setVisible(true));
        btnMonitorizareConsum.addActionListener(e -> new MonitorizareConsumFrame().setVisible(true));
        btnGenerareFacturi.addActionListener(e -> new GenerareFacturiFrame().setVisible(true));
        btnVizualizareRapoarte.addActionListener(e -> new VizualizareRapoarteFrame().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminFrame().setVisible(true));
    }
}
