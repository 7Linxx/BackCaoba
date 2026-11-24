package co.edu.unbosque.backcaoba.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.awt.Color;

public class PdfExporter {

    private PdfExporter() {
    }

    public static byte[] generar(String titulo, String[] columnas, List<String[]> filas) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document();
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16);
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);
            Font fuenteTexto = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);

            documento.add(new Paragraph(titulo, fuenteTitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(columnas.length);
            tabla.setWidthPercentage(100);

            for (String columna : columnas) {
                PdfPCell celda = new PdfPCell(new Paragraph(columna, fuenteEncabezado));
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setBackgroundColor(new Color(204, 255, 204));
                tabla.addCell(celda);
            }
            for (String[] datos : filas) {
                for (String dato : datos) {
                    tabla.addCell(new PdfPCell(new Paragraph(dato, fuenteTexto)));
                }
            }

            documento.add(tabla);
            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo generar el archivo");
        }
    }
}