package co.edu.unbosque.backcaoba.configuration;

import co.edu.unbosque.backcaoba.dto.RolDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.model.Rol;
import co.edu.unbosque.backcaoba.model.Usuario;
import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de ModelMapper para convertir entidades a DTOs.
 * <ul>
 * <li>Estrategia STRICT para que solo se mapeen propiedades con el mismo
 * nombre.</li>
 * <li>No toca colecciones lazy de Hibernate que no estén inicializadas.</li>
 * <li>Mapeo explícito de {@code Usuario.rol} (entidad) a
 * {@code UsuarioDTO.rol} (nombre del rol).</li>
 * </ul>
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(context -> {
                    if (context.getSource() instanceof PersistentCollection<?> coleccion) {
                        return coleccion.wasInitialized();
                    }
                    return true;
                });

        modelMapper.typeMap(Usuario.class, UsuarioDTO.class)
                .addMappings(m -> m.map(origen -> origen.getRol().getNombre(), UsuarioDTO::setRol));

        modelMapper.typeMap(Rol.class, RolDTO.class)
                .addMappings(m -> m.map(origen -> origen.getIdRol().intValue(), RolDTO::setIdRol));

        return modelMapper;
    }
}
