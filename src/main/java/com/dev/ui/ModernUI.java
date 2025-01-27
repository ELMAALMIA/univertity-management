package com.dev.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class ModernUI {
    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Personnalisation des couleurs
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);

            // Couleurs personnalisées
            UIManager.put("Button.background", new Color(0, 120, 212));
            UIManager.put("Button.foreground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}