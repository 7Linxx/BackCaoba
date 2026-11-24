package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.ConteoPeriodoDTO;
import co.edu.unbosque.backcaoba.dto.ConteoRolDTO;
import co.edu.unbosque.backcaoba.dto.TablaReporteDTO;
import co.edu.unbosque.backcaoba.model.Rol;
import co.edu.unbosque.backcaoba.repository.RolRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import co.edu.unbosque.backcaoba.util.ExcelExporter;
import co.edu.unbosque.backcaoba.util.PdfExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReporteService {

    private static final int DIAS_ULTIMO_MES = 30;
    private static final int MESES_ULTIMO_ANIO = 12;
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_MES =
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("es-CO"));

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // Cantidad y porcentaje de usuarios de cada rol (si el rol no tiene usuario muestra 0) 
    public List<ConteoRolDTO> contarUsuariosPorRol() {
        long total = usuarioRepository.count();
        List<ConteoRolDTO> lista = new ArrayList<>();
        for (Rol rol : rolRepository.findAll(Sort.by("nombre"))) {
            long cantidad = usuarioRepository.countByRolNombre(rol.getNombre());
            double porcentaje = total == 0 ? 0.0 : Math.round(cantidad * 1000.0 / total) / 10.0;
            lista.add(new ConteoRolDTO(rol.getNombre(), cantidad, porcentaje));
        }
        return lista;
    }

    // Usuarios registrados cada día de los últimos 30 días incluyendo hoy. 
    public List<ConteoPeriodoDTO> contarUsuariosUltimoMes() {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy.minusDays(DIAS_ULTIMO_MES - 1L);

        Map<LocalDate, Long> porDia = new HashMap<>();
        for (LocalDateTime fecha : usuarioRepository.findFechasRegistroDesde(desde.atStartOfDay())) {
            porDia.merge(fecha.toLocalDate(), 1L, Long::sum);
        }

        List<ConteoPeriodoDTO> lista = new ArrayList<>();
        for (int i = DIAS_ULTIMO_MES - 1; i >= 0; i--) {
            LocalDate dia = hoy.minusDays(i);
            lista.add(new ConteoPeriodoDTO(dia.toString(), dia.format(FORMATO_DIA),
                    porDia.getOrDefault(dia, 0L)));
        }
        return lista;
    }

    // Usuarios registrados cada mes de los últimos 12 meses incluyendo el ultimo mes. 

    public List<ConteoPeriodoDTO> contarUsuariosUltimoAnio() {
        YearMonth mesActual = YearMonth.now();
        YearMonth desde = mesActual.minusMonths(MESES_ULTIMO_ANIO - 1L);

        Map<YearMonth, Long> porMes = new HashMap<>();
        for (LocalDateTime fecha : usuarioRepository.findFechasRegistroDesde(desde.atDay(1).atStartOfDay())) {
            porMes.merge(YearMonth.from(fecha), 1L, Long::sum);
        }

        List<ConteoPeriodoDTO> lista = new ArrayList<>();
        for (int i = MESES_ULTIMO_ANIO - 1; i >= 0; i--) {
            YearMonth mes = mesActual.minusMonths(i);
            lista.add(new ConteoPeriodoDTO(mes.toString(), mes.format(FORMATO_MES),
                    porMes.getOrDefault(mes, 0L)));
        }
        return lista;
    }
 
    
    @Transactional(readOnly = true)
    public byte[] exportarExcel(String reporte) {
        TablaReporteDTO tabla = armarTabla(reporte);
        return ExcelExporter.generar(tabla.getTitulo(), tabla.getColumnas(), tabla.getFilas());
    }

    @Transactional(readOnly = true)
    public byte[] exportarPdf(String reporte) {
        TablaReporteDTO tabla = armarTabla(reporte);
        return PdfExporter.generar(tabla.getTitulo(), tabla.getColumnas(), tabla.getFilas());
    }

  
    private TablaReporteDTO armarTabla(String reporte) {
        String clave = reporte == null ? "" : reporte.trim();

        switch (clave) {
            case "usuariosPorRol" -> {
                List<String[]> filas = new ArrayList<>();
                for (ConteoRolDTO d : contarUsuariosPorRol()) {
                    filas.add(new String[] { d.getRol(), String.valueOf(d.getCantidad()),
                            String.format(Locale.US, "%.1f%%", d.getPorcentaje()) });
                }
                return new TablaReporteDTO("Usuarios por rol",
                        new String[] { "Rol", "Cantidad", "Porcentaje" }, filas);
            }
            case "usuariosUltimoMes" -> {
                List<String[]> filas = new ArrayList<>();
                for (ConteoPeriodoDTO d : contarUsuariosUltimoMes()) {
                    filas.add(new String[] { d.getEtiqueta(), String.valueOf(d.getCantidad()) });
                }
                return new TablaReporteDTO("Usuarios registrados en el último mes",
                        new String[] { "Día", "Cantidad" }, filas);
            }
            case "usuariosUltimoAnio" -> {
                List<String[]> filas = new ArrayList<>();
                for (ConteoPeriodoDTO d : contarUsuariosUltimoAnio()) {
                    filas.add(new String[] { d.getEtiqueta(), String.valueOf(d.getCantidad()) });
                }
                return new TablaReporteDTO("Usuarios registrados en el último año",
                        new String[] { "Mes", "Cantidad" }, filas);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Reporte no válido.");
        }
    }
    
    
}
