package com.dev.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ModernTable extends JTable {
    public ModernTable(DefaultTableModel model) {
        super(model);

        // Style moderne
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setRowHeight(30);
        getTableHeader().setBackground(new Color(240, 240, 240));
        setSelectionBackground(new Color(232, 240, 254));

        // Renderer personnalisé
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                if (row % 2 == 0 && !isSelected) {
                    setBackground(new Color(250, 250, 250));
                } else if (!isSelected) {
                    setBackground(Color.WHITE);
                }

                return this;
            }
        });
    }
}