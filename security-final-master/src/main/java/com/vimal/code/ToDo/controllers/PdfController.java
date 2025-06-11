package com.vimal.code.ToDo.controllers;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/generate-pdf")
@CrossOrigin(origins = "http://localhost:3000") // Adjust to your frontend port
public class PdfController {

    @PostMapping("/admin")
    public void generatePdf(@RequestBody Map<String, Object> stats, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title
        Text title = new Text("Tableau de Bord Admin").setBold();
        document.add(new Paragraph(title).setFontSize(20).setMarginBottom(10));
        document.add(new Paragraph("Date: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .setFontSize(12).setMarginBottom(20));

        // Add stats
        document.add(new Paragraph("Total Citoyens: " + stats.getOrDefault("totalCitizens", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Événements Total: " + stats.getOrDefault("totalEvents", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Réclamations Totales: " + stats.getOrDefault("totalReclamations", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Réclamations Résolues: " + stats.getOrDefault("resolvedReclamations", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Réclamations Non Résolues: " + stats.getOrDefault("unresolvedReclamations", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Taux de Participation: " + stats.getOrDefault("participationRate", 0) + "%")
                .setFontSize(12).setMarginBottom(20));

        // Close the document
        document.close();

        byte[] pdfBytes = baos.toByteArray();
        response.setContentType("application/pdf");
        response.setContentLength(pdfBytes.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"admin-dashboard-" + java.time.LocalDate.now() + ".pdf\"");
        response.getOutputStream().write(pdfBytes);
    }
    @PostMapping("/agent")
    public void generatePdfAgent(@RequestBody Map<String, Object> stats, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title
        Text title = new Text("Tableau de Bord Agent").setBold();
        document.add(new Paragraph(title).setFontSize(20).setMarginBottom(10));
        document.add(new Paragraph("Date: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .setFontSize(12).setMarginBottom(20));

        // Add stats
        document.add(new Paragraph("Réclamations Totales: " + stats.getOrDefault("totalAssignedReclamations", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("En cours: " + stats.getOrDefault("pendingReclamations", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Résolues: " + stats.getOrDefault("resolvedReclamations", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Événements Total: " + stats.getOrDefault("totalEvents", 0))
                .setFontSize(12).setMarginBottom(10));
        document.add(new Paragraph("Taux de Participation: " + stats.getOrDefault("participationRate", 0) + "%")
                .setFontSize(12).setMarginBottom(20));

        // Close the document
        document.close();

        byte[] pdfBytes = baos.toByteArray();
        response.setContentType("application/pdf");
        response.setContentLength(pdfBytes.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"tableau-de-bord-agent-" + java.time.LocalDate.now() + ".pdf\"");
        response.getOutputStream().write(pdfBytes);
    }
}