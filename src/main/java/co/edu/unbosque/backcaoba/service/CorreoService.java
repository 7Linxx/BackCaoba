package co.edu.unbosque.backcaoba.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

@Service
public class CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

    private final JavaMailSender mailSender;
    private final String remitente;
    private final String nombreRemitente;

    public CorreoService(JavaMailSender mailSender,
            @Value("${mail.from}") String remitente,
            @Value("${mail.from-name}") String nombreRemitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.nombreRemitente = nombreRemitente;
    }

    public void enviarRecuperacion(String correoDestino, String nombre, String codigo, long minutosVigencia) {
        String nombreSeguro = HtmlUtils.htmlEscape(nombre);

        String texto = """
                Hola %s,

                Recibimos una solicitud para restablecer la contraseña de tu cuenta en Caoba.
                Tu código de verificación es:

                %s

                Escríbelo en la pantalla de recuperación. Vence en %d minutos y solo se puede usar una vez.

                Si no solicitaste el cambio, ignora este correo: tu contraseña actual sigue funcionando.
                """.formatted(nombre, codigo, minutosVigencia);

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;color:#24332a">
                  <h2 style="color:#285b45;margin-bottom:4px">CAOBA</h2>
                  <p style="color:#82937f;margin-top:0;font-size:12px;letter-spacing:2px">RECUPERAR ACCESO</p>
                  <p>Hola <strong>%s</strong>,</p>
                  <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta en Caoba. Tu código de verificación es:</p>
                  <p style="text-align:center;margin:28px 0">
                    <span style="display:inline-block;background:#eef3ee;color:#285b45;padding:14px 26px;border-radius:8px;font-size:32px;font-weight:bold;letter-spacing:8px">%s</span>
                  </p>
                  <p style="font-size:13px">Escríbelo en la pantalla de recuperación. Vence en <strong>%d minutos</strong> y solo se puede usar una vez.</p>
                  <p style="font-size:12px;color:#7c8980">Si no solicitaste el cambio, ignora este correo: tu contraseña actual sigue funcionando.</p>
                </div>
                """
                .formatted(nombreSeguro, codigo, minutosVigencia);

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, StandardCharsets.UTF_8.name());
            helper.setFrom(remitente, nombreRemitente);
            helper.setTo(correoDestino);
            helper.setSubject("Caoba | Restablece tu contraseña");
            helper.setText(texto, html);
            mailSender.send(mensaje);
        } catch (MailException | jakarta.mail.MessagingException | java.io.UnsupportedEncodingException ex) {
            if (log.isErrorEnabled()) {
                log.error("No se pudo enviar el correo de recuperación.", ex);
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible enviar el correo de recuperación. Inténtalo de nuevo en unos minutos.", ex);
        }
    }
}
