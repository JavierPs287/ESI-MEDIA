package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final CreadorRepository creadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValidateService validateService;
    public AdminService(AdminRepository adminRepository, CreadorRepository creadorRepository, UsuarioRepository usuarioRepository, ValidateService validateService) {
        this.adminRepository = adminRepository;
        this.creadorRepository = creadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.validateService = validateService;
    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Admin registerAdmin(AdminDTO adminDTO) {
        // Convertir DTO a entidad
        Admin admin = new Admin();
        admin.setNombre(adminDTO.getNombre());
        admin.setApellidos(adminDTO.getApellidos());
        admin.setEmail(adminDTO.getEmail());
        admin.setContrasena(adminDTO.getContrasena());
        admin.setFoto(adminDTO.getFoto());
        admin.setDepartamento(adminDTO.getDepartamento());
        registerComun(admin);
        return registerAdminInternal(admin);
    }

    private void registerComun(User user) {
        if (validateService.isRequiredFieldEmpty(user.getNombre(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(user.getApellidos(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(user.getEmail(), 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        if (!validateService.isEmailValid(user.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (validateService.isRequiredFieldEmpty(user.getContrasena(),8, 128)) {
            throw new IllegalArgumentException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        if (!validateService.isPasswordSecure(user.getContrasena())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }

        // Verificar email duplicado en administradores, creadores y usuarios
        if (adminRepository.existsByEmail(user.getEmail()) || 
            creadorRepository.existsByEmail(user.getEmail()) || 
            usuarioRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Establecer foto por defecto si no se proporciona
        if (validateService.isRequiredFieldEmpty(String.valueOf(user.getFoto()), 1, 10)) {
            user.setFoto(0);
        }
        // Encriptar contraseña
        user.setContrasena(passwordEncoder.encode(user.getContrasena()));
    }

    private Admin registerAdminInternal(Admin admin) {
        if (!validateService.isEnumValid(admin.getDepartamento())) {
            throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        }
        // Guardar administrador
        return adminRepository.save(admin);
    }

    public Creador registerCreador(CreadorDTO creadorDTO) {
        // Convertir DTO a entidad
        Creador creador = new Creador();
        creador.setNombre(creadorDTO.getNombre());
        creador.setApellidos(creadorDTO.getApellidos());
        creador.setEmail(creadorDTO.getEmail());
        creador.setAlias(creadorDTO.getAlias());
        creador.setDescripcion(creadorDTO.getDescripcion());
        creador.setCampo(creadorDTO.getCampo());
        creador.setTipo(creadorDTO.getTipo());
        creador.setFoto(creadorDTO.getFoto()); 
        creador.setContrasena(creadorDTO.getContrasena());
        registerComun(creador);
        return registerCreadorInternal(creador);
    }

    private Creador registerCreadorInternal(Creador creador) {
        // Validar alias (opcional, pero si se proporciona debe cumplir requisitos)
        if (creador.getAlias() != null && !creador.getAlias().isEmpty()) {
            if (creador.getAlias().length() < 2 || creador.getAlias().length() > 20) {
                throw new IllegalArgumentException("El alias debe tener entre 2 y 20 caracteres");
            }
            if (creadorRepository.existsByAlias(creador.getAlias())) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }
        
        // Descripción validar longitud
        if (creador.getDescripcion() != null && creador.getDescripcion().length() > 500) {
            throw new IllegalArgumentException("La descripción no puede tener más de 500 caracteres");
        }
        
        if (!validateService.isEnumValid(creador.getCampo())) {
            throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        }
        if (!validateService.isEnumValid(creador.getTipo())) {
            throw new IllegalArgumentException("El tipo es obligatorio y debe ser un valor válido (AUDIO, VIDEO)");
        }
        // Guardar creador
        return creadorRepository.save(creador);
    }

    public void setUserBlocked(String email, boolean blocked) {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("Usuario no encontrado");
        }
        user.setBloqueado(blocked);
        usuarioRepository.save(user);
    }
}
