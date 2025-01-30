package com.dev.utils;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateLabelFormatter extends DateFormatter {
    public DateLabelFormatter() {
        super(new SimpleDateFormat("dd/MM/yyyy"));
    }
}
