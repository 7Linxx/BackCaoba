package co.edu.unbosque.backcaoba.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

	private ExcelExporter() {
	}

	public static byte[] generar(String titulo, String[] columnas, List<String[]> filas) {
		try (XSSFWorkbook libro = new XSSFWorkbook()) {
			ByteArrayOutputStream salida = new ByteArrayOutputStream();
			
			Sheet hoja = libro.createSheet(nombreHoja(titulo));

			CellStyle estiloEncabezado = libro.createCellStyle();
			estiloEncabezado.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row encabezado = hoja.createRow(0);
			for (int i = 0; i < columnas.length; i++) {
				encabezado.createCell(i).setCellValue(columnas[i]);
				encabezado.getCell(i).setCellStyle(estiloEncabezado);
			}

			int numeroFila = 1;
			for (String[] datos : filas) {
				Row fila = hoja.createRow(numeroFila++);
				for (int i = 0; i < datos.length; i++) {
					fila.createCell(i).setCellValue(datos[i]);
				}
			}

			for (int i = 0; i < columnas.length; i++) {
				hoja.autoSizeColumn(i);
			}

			libro.write(salida);
			return salida.toByteArray();
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el archivo excel");
		}
	}

	// validacion para el excel
	private static String nombreHoja(String titulo) {
		String limpio = titulo.replaceAll("[:\\\\/?*\\[\\]]", " ");
		return limpio.length() > 31 ? limpio.substring(0, 31) : limpio;
	}
}