// package edu.uclm.esi.esimedia.be_esimedia;

// import org.junit.jupiter.api.Disabled;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;

// import edu.uclm.esi.esimedia.be_esimedia.http.PasswordResetController;
// import edu.uclm.esi.esimedia.be_esimedia.services.TokenService;
// import edu.uclm.esi.esimedia.be_esimedia.services.UserService;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.doThrow;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// //TODO ARREGLAR ESTO PARA FUTUROS TESTS
// @Disabled("Tests deshabilitados temporalmente - problema con contexto de Spring Security en pipeline")
// @WebMvcTest(PasswordResetController.class)
// @DisplayName("PasswordResetController Tests")
// class PasswordResetControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockitoBean
//     private UserService userService;

//     @MockitoBean
//     private TokenService tokenService;

//     // Forgot Password Tests
//     @Test
//     @DisplayName("Debe procesar forgotPassword con email válido")
//     void testForgotPassword_emailValido() throws Exception {
//         doNothing().when(userService).startPasswordReset(anyString(), any(), any(), any());

//         mockMvc.perform(post("/auth/forgotPassword")
//                 .param("email", "email@valido.com"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Si el correo existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña."));
//     }

//     @Test
//     @DisplayName("Debe procesar forgotPassword con email inválido")
//     void testForgotPassword_emailInvalido() throws Exception {
//         doNothing().when(userService).startPasswordReset(anyString(), any(), any(), any());

//         mockMvc.perform(post("/auth/forgotPassword")
//                 .param("email", "email@invalido.com"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Si el correo existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña."));
//     }

//     @Test
//     @DisplayName("Debe manejar email vacío en forgotPassword")
//     void testForgotPassword_emailVacio() throws Exception {
//         mockMvc.perform(post("/auth/forgotPassword")
//                 .param("email", ""))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     @DisplayName("Debe manejar email nulo en forgotPassword")
//     void testForgotPassword_emailNulo() throws Exception {
//         mockMvc.perform(post("/auth/forgotPassword"))
//                 .andExpect(status().isBadRequest());
//     }

//     // Validate Token Tests
//     @Test
//     @DisplayName("Debe validar token correctamente")
//     void testValidateToken_tokenValido() throws Exception {
//         doNothing().when(tokenService).validatePasswordResetToken(anyString());

//         mockMvc.perform(get("/auth/resetPassword/validate")
//                 .param("token", "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Token válido"));
//     }

//     @Test
//     @DisplayName("Debe retornar BadRequest cuando token es inválido")
//     void testValidateToken_tokenInvalido() throws Exception {
//         doThrow(new RuntimeException("Token expirado")).when(tokenService).validatePasswordResetToken(anyString());

//         mockMvc.perform(get("/auth/resetPassword/validate")
//                 .param("token", "invalid-token"))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("Token inválido o expirado: Token expirado"));
//     }

//     @Test
//     @DisplayName("Debe manejar token vacío en validateToken")
//     void testValidateToken_tokenVacio() throws Exception {
//         doThrow(new RuntimeException("Token vacío")).when(tokenService).validatePasswordResetToken("");

//         mockMvc.perform(get("/auth/resetPassword/validate")
//                 .param("token", ""))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     @DisplayName("Debe manejar token nulo en validateToken")
//     void testValidateToken_tokenNulo() throws Exception {
//         mockMvc.perform(get("/auth/resetPassword/validate"))
//                 .andExpect(status().isBadRequest());
//     }

//     // Reset Password Tests
//     @Test
//     @DisplayName("Debe resetear contraseña exitosamente")
//     void testResetPassword_success() throws Exception {
//         doNothing().when(userService).resetPassword(anyString(), anyString(), any());

//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("application/json")
//                 .content("{\"token\":\"eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ\",\"newPassword\":\"newPassword123\"}"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Contraseña cambiada correctamente."));
//     }

//     @Test
//     @DisplayName("Debe retornar BadRequest cuando resetPassword falla")
//     void testResetPassword_failure() throws Exception {
//         doThrow(new RuntimeException("Token inválido")).when(userService).resetPassword(anyString(), anyString(), any());

//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("application/json")
//                 .content("{\"token\":\"invalid-token\",\"newPassword\":\"newPassword123\"}"))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("Error al restablecer la contraseña: Token inválido"));
//     }

//     @Test
//     @DisplayName("Debe retornar error cuando nueva contraseña está vacía")
//     void testResetPassword_passwordVacia() throws Exception {
//         doThrow(new RuntimeException("Contraseña vacía")).when(userService).resetPassword(anyString(), anyString(), any());

//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("application/json")
//                 .content("{\"token\":\"eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ\",\"newPassword\":\"\"}"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     @DisplayName("Debe retornar error cuando falta el campo newPassword")
//     void testResetPassword_sinPassword() throws Exception {
//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("application/json")
//                 .content("{\"token\":\"eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ\"}"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     @DisplayName("Debe retornar error cuando el JSON está malformado")
//     void testResetPassword_jsonMalformado() throws Exception {
//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("application/json")
//                 .content("{\"token\":\"yJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ\",\"newPassword123\":"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     @DisplayName("Debe retornar error cuando Content-Type no es JSON")
//     void testResetPassword_contentTypeInvalido() throws Exception {
//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("text/plain")
//                 .content("{\"token\":\"yJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ\",\"newPassword\":\"newPassword123\"}"))
//                 .andExpect(status().isUnsupportedMediaType());
//     }

//     @Test
//     @DisplayName("Debe manejar contraseña débil en resetPassword")
//     void testResetPassword_passwordDebil() throws Exception {
//         doThrow(new RuntimeException("Contraseña muy débil")).when(userService).resetPassword(anyString(), anyString(), any());

//         mockMvc.perform(post("/auth/resetPassword")
//                 .contentType("application/json")
//                 .content("{\"token\":\"yJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYW5kcmVzLmc5OWxvcGV6QGdtYWlsLmNvbSIsImV4cCI6MTc2MzA1Mjk1NSwicHVycG9zZSI6InBhc3N3b3JkUmVzZXQiLCJpYXQiOjE3NjMwNDkzNTV9.YRfSqDL1GnW0On2VcGAkcJbVSlv_n9RsVGEJk8XlDlwter_cElG-gzxmgyRA7rHx1Ksj46AD_b-JGP_EldFWKEAJOMVPRouHHHH5BovLBTDPiM8IIGtQfng_6XtaL4cgRGoKkt2iTmoS9cwE2X1taYOkTLyjlmxbU1fItSoHB9Bv8qLBzuAK6EDDQBwq_srj7erTm182BBJtrv0r0axzbdFzVgE8c1Y6p06n89Q1xgyul-BUKS3H_dUwMSMt1pTx5CRaeS-v9brgTorS5E-GDQBXRRvmtLqLZLOsUF7bOb74dbcR8Dt3Uc6ccA2bBswBKRDsidJGB-itwngEd74uOQ\",\"newPassword\":\"123\"}"))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("Error al restablecer la contraseña: Contraseña muy débil"));
//     }
// }