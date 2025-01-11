package gestionareserviciiacet;


import gui.AutentificareFrame;
import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;


public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarculaLaf());


 // Aplică tema FlatLaf
        } catch (Exception e) {
            System.err.println("Nu s-a putut seta tema FlatLaf!");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new AutentificareFrame().setVisible(true); // Pornește aplicația
        });
    }
}


