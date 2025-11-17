package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.JWT_COOKIE_NAME;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.LoginRequest;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.UserService;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;

@RestController
@RequestMapping("user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PlaylistService playlistService;

    public UserController(AuthService authService, UserService userService, JwtUtils jwtUtils, PlaylistService playlistService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.playlistService = playlistService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUsuario(@RequestBody UsuarioDTO usuarioDTO){
        authService.register(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
    }

    // TODO Quitar toda lógica de Controller
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUsuario(@RequestBody LoginRequest loginRequest){
        try {
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            
            String role = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Crear cookie HTTP-Only con el token
            ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                    .httpOnly(true)  // No accesible desde JavaScript
                    .secure(false)   //TODO Cambiar a true en producción con HTTPS
                    .path("/")
                    .maxAge(24L * 60 * 60)
                    .sameSite("Lax")
                    .build();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("role", role);
            response.put("userId", userId);
            response.put("email", loginRequest.getEmail());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(jakarta.servlet.http.HttpServletRequest request) {
        try {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o no proporcionado"));
            }
            
            String email = jwtUtils.getEmailFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", email);
            userInfo.put("role", role);
            userInfo.put("userId", userId);
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información del usuario"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Crear cookie con maxAge 0 para eliminarla
        ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logout exitoso"));
    }

    // TODO llevar lógica a un servicio
    /**
     * Endpoint temporal para verificar el contenido del token JWT
     * @param token Token JWT a decodificar
     * @return Información contenida en el token
     */
    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, String>> verifyToken(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token no proporcionado"));
            }

            Map<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("email", jwtUtils.getEmailFromToken(token));
            tokenInfo.put("role", jwtUtils.getRoleFromToken(token));
            tokenInfo.put("userId", jwtUtils.getUserIdFromToken(token));
            tokenInfo.put("valid", String.valueOf(jwtUtils.validateToken(token)));

            return ResponseEntity.ok(tokenInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido: " + e.getMessage()));
        }
    }

    @PostMapping("/create-playlist")
    public ResponseEntity<Map<String, String>> createPlaylist(
            @RequestBody PlaylistDTO playlistDTO,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // Extraer token de la cookie
            String token = extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            // Obtener el rol y userId del token
            String userRole = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Validar que no sea ADMIN
            if ("ADMIN".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Los administradores no pueden crear playlists"));
            }
            
            // Si es USER, forzar playlist privada
            if ("USER".equals(userRole)) {
                playlistDTO.setPublic(false);
            }
            
            // Asignar el ownerId
            playlistDTO.setOwnerId(userId);
            
            playlistService.createPlaylist(playlistDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Playlist creada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
}