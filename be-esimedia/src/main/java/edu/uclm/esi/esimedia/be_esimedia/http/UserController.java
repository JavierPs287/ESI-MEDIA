package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.constants.Constants;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.JWT_COOKIE_NAME;
import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.PlaylistDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.LoginRequest;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.PlaylistService;
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
    public ResponseEntity<Map<String, Object>> loginUsuario(@RequestBody LoginRequest loginRequest){
        try {
            User user = userService.findByEmail(loginRequest.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas"));
            }
            // Validar contraseña y bloqueo
            authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            boolean has2FA = user.isTwoFaEnabled();
            Map<String, Object> response = new HashMap<>();
            response.put("role", user.getRole());
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            if (has2FA) {
                response.put("2faRequired", true);
                response.put("message", "2FA requerido");
                // No enviar token ni cookie
                return ResponseEntity.ok(response);
            } else {
                // Generar token y cookie normalmente
                String token = authService.generateJwtToken(user);
                ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(24L * 60 * 60)
                        .sameSite("Lax")
                        .build();
                response.put("message", "Login exitoso");
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(Constants.ERROR_KEY, e.getMessage()));
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<UsuarioDTO> updateProfile(@RequestBody UsuarioDTO usuarioDTO, jakarta.servlet.http.HttpServletRequest request) {
        UsuarioDTO updatedUsuario = userService.updateProfile(usuarioDTO, request);
        return ResponseEntity.ok(updatedUsuario);
    }

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

    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, String>> verifyToken(
        @CookieValue(name = JWT_COOKIE_NAME, required = false) String token) {
    
        Map<String, String> tokenInfo = new HashMap<>();
    
        // Si no hay cookie, devolver inválido
        if (token == null || token.isEmpty()) {
            tokenInfo.put("valid", "false");
            return ResponseEntity.status(HttpStatus.OK).body(tokenInfo);
        }
    
        try {
            // Validar el token
            boolean isValid = jwtUtils.validateToken(token);
            tokenInfo.put("valid", String.valueOf(isValid));
            
            if (isValid) {
                // Extraer información del token
                String email = jwtUtils.getEmailFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                String userId = jwtUtils.getUserIdFromToken(token);
                
                tokenInfo.put("email", email);
                tokenInfo.put("role", role);
                tokenInfo.put("userId", userId);
            }
            
            return ResponseEntity.status(HttpStatus.OK).body(tokenInfo);
        } catch (Exception e) {
            tokenInfo.put("valid", "false");
            return ResponseEntity.status(HttpStatus.OK).body(tokenInfo);
        }
    }        /**
         * Endpoint para emitir el token tras verificación TOTP
         */
        //TODO eliminar codigo duplicado con login y mover a service
        @PostMapping("/2fa/token")
        public ResponseEntity<Map<String, Object>> issueTokenAfterTotp(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            if (email == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email requerido"));
            }
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
            }
            String token = authService.generateJwtToken(user);
            ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24L * 60 * 60)
                    .sameSite("Lax")
                    .build();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token emitido tras 2FA");
            response.put("role", user.getRole());
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        }
    /**
     * Endpoint para verificar código TOTP
     */
    @PostMapping("/2fa/verify")
    public ResponseEntity<Map<String, Object>> verifyTotp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Email y código requeridos"));
        }
        boolean valid = authService.verifyTotpCode(email, code);
        User user = userService.findByEmail(email);
        boolean has3FA = user.isThreeFaEnabled();
        Map<String, Object> response = new HashMap<>();
        response.put("role", user.getRole());
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        if (has3FA){
            response.put("3faRequired", true);
            response.put("message", "2FA requerido");
                // No enviar token ni cookie
            return ResponseEntity.ok(response);
        }
        if (valid) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "Código TOTP incorrecto"));
        }
    }
        /**
     * Endpoint para actualizar el estado de 2FA de un usuario
     */
    @PostMapping("/2fa")
    public ResponseEntity<Map<String, String>> update2FA(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        Boolean enable2FA = (Boolean) body.get("enable2FA");
        if (email == null || enable2FA == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y enable2FA requeridos"));
        }
        boolean updated = userService.update2FA(email, enable2FA);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "2FA actualizado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No se pudo actualizar 2FA"));
        }
    }
    //TODO eliminar lógica de los controllers y moverla a servicios
    /**
     * Endpoint para activar 2FA TOTP y devolver QR y secreto
     */
    @PostMapping("/2fa/activate")
    public ResponseEntity<Map<String, String>> activar2FA(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("userId");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email no proporcionado"));
            }
            Map<String, String> result = authService.activar2FA(email);
            if (result == null || result.isEmpty() || !result.containsKey("qrUrl")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No se pudo generar el QR de 2FA"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Error al activar 2FA: " + e.getMessage()));
        }
    }
        /**
         * Endpoint para enviar el código de 3FA por email
         */
        @PostMapping("/send-3fa-code")
        public ResponseEntity<Map<String, String>> sendThreeFactorCode(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email requerido"));
            }
            authService.sendThreeFactorCode(email);
            return ResponseEntity.ok(Map.of("message", "Código enviado por email"));
        }

        /**
         * Endpoint para verificar el código de 3FA
         */
        @PostMapping("/verify-3fa-code")
        public ResponseEntity<Map<String, Object>> verifyThreeFactorCode(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            String code = body.get("code");
            if (email == null || code == null || email.isEmpty() || code.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Email y código requeridos"));
            }
            boolean valid = authService.verifyThreeFactorCode(email, code);
            if (valid) {
                User user = userService.findByEmail(email);
                String token = authService.generateJwtToken(user);
                ResponseCookie cookie = ResponseCookie.from(Constants.JWT_COOKIE_NAME, token)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(24L * 60 * 60)
                        .sameSite("Lax")
                        .build();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("role", user.getRole());
                response.put("userId", user.getId());
                response.put("email", user.getEmail());
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "Código incorrecto o expirado"));
            }
        }
    
            /**
             * Endpoint para emitir el token tras verificación 3FA
             */
            @PostMapping("/3fa/token")
            public ResponseEntity<Map<String, Object>> issueTokenAfterThreeFa(@RequestBody Map<String, String> body) {
                String email = body.get("email");
                if (email == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Email requerido"));
                }
                User user = userService.findByEmail(email);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
                }
                String token = authService.generateJwtToken(user);
                ResponseCookie cookie = ResponseCookie.from(Constants.JWT_COOKIE_NAME, token)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(24L * 60 * 60)
                        .sameSite("Lax")
                        .build();
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Token emitido tras 3FA");
                response.put("role", user.getRole());
                response.put("userId", user.getId());
                response.put("email", user.getEmail());
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(response);
            }
}