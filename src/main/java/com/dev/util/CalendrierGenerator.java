package com.dev.util;


import com.dev.models.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class CalendrierGenerator {

    public static void genererCalendrier(Surveillant surveillant, List<Examen> examens) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document,
                    new FileOutputStream("calendrier_" + surveillant.getNom() + ".pdf"));

            document.open();

            // En-tête
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Calendrier de surveillance", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Surveillant : " + surveillant.getNom() +
                    " " + surveillant.getPrenom()));
            document.add(new Paragraph("Département : " + surveillant.getDepartement()));
            document.add(new Paragraph("\n"));

            // Tableau des surveillances
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            // En-têtes du tableau
            table.addCell("Date");
            table.addCell("Module");
            table.addCell("Heure début");
            table.addCell("Heure fin");
            table.addCell("Local");

            // Données
            for (Examen examen : examens) {
                if (examen.getSurveillants().contains(surveillant)) {
                    table.addCell(examen.getDate().toString());
                    table.addCell(examen.getModule().getNom());
                    table.addCell(examen.getHeureDebut());
                    table.addCell(examen.getHeureFin());
                    table.addCell(examen.getLocaux().get(0).getNom());
                }
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}