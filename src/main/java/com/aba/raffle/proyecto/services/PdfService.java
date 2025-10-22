package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.WinnerDTO;
import com.aba.raffle.proyecto.model.entities.SorteoActa;
import com.aba.raffle.proyecto.model.entities.WinnerEmbeddable;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generarActaPdf(SorteoActa acta, List<WinnerDTO> ganadores) throws Exception {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);

        doc.open();

        doc.add(new Paragraph("ACTA DEL SORTEO"));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Rifa ID: " + acta.getRaffleId()));
        doc.add(new Paragraph("Fecha de ejecución: " + acta.getFechaEjecucion()));
        doc.add(new Paragraph("Semilla: " + acta.getSemilla()));
        doc.add(new Paragraph("Hash generado: " + acta.getHashGenerado()));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Ganadores:"));
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell("Número");
        table.addCell("Nombre");
        table.addCell("Apellido");
        table.addCell("Email");
        table.addCell("Teléfono");

        for (WinnerEmbeddable w : acta.getGanadores()) {
            table.addCell(w.getNumero());
            table.addCell(w.getNombre());
            table.addCell(w.getApellido());
            table.addCell(w.getEmail());
            table.addCell(w.getTelefono());
        }


        doc.add(table);
        doc.close();

        return out.toByteArray();
    }
}
