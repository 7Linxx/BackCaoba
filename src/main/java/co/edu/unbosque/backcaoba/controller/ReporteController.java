package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.ConteoPeriodoDTO;
import co.edu.unbosque.backcaoba.dto.ConteoRolDTO;
import co.edu.unbosque.backcaoba.service.ReporteService;
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
}
