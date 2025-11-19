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
import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.LoginRequest;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.UserService;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;

// TODO: Corregir errores del sonar relacinados con las constantes de Strings

@RestController
@RequestMapping("user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(AuthService authService, UserService userService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
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
            
            // Extraer información del token para enviarla en la respuesta
            String role = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Crear cookie HTTP-Only con el token
            ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                    .httpOnly(true)  // No accesible desde JavaScript
                    .secure(false)   //TODO Cambiar a true en producción con HTTPS
                    .path("/")
                    .maxAge(24L * 60 * 60) // 24 horas
                    .sameSite("Lax")
                    .build();
            
            // Preparar respuesta con información del usuario
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

    /**
     * Endpoint para obtener la información del usuario actual desde el token
     * Requiere estar autenticado (cookie con token válido)
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(jakarta.servlet.http.HttpServletRequest request) {
        UserDTO userDTO = userService.getCurrentUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para cerrar sesión (eliminar cookie)
     */
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
}