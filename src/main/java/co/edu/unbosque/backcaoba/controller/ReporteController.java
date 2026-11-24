package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.ConteoPeriodoDTO;
import co.edu.unbosque.backcaoba.dto.ConteoRolDTO;
import co.edu.unbosque.backcaoba.service.ReporteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reporte")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/usuariosPorRol")
    public ResponseEntity<List<ConteoRolDTO>> usuariosPorRol() {
        return ResponseEntity.ok(reporteService.contarUsuariosPorRol());
    }

    @GetMapping("/usuariosUltimoMes")
    public ResponseEntity<List<ConteoPeriodoDTO>> usuariosUltimoMes() {
        return ResponseEntity.ok(reporteService.contarUsuariosUltimoMes());
    }

    @GetMapping("/usuariosUltimoAnio")
    public ResponseEntity<List<ConteoPeriodoDTO>> usuariosUltimoAnio() {
        return ResponseEntity.ok(reporteService.contarUsuariosUltimoAnio());
    }
    
    @GetMapping("/{reporte}/excel")
    public ResponseEntity<byte[]> descargarExcel(@PathVariable String reporte) {
        byte[] archivo = reporteService.exportarExcel(reporte);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reporte + ".xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(archivo);
    }

    @GetMapping("/{reporte}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable String reporte) {
        byte[] archivo = reporteService.exportarPdf(reporte);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reporte + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }
}
