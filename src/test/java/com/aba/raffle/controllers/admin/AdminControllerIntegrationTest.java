//package com.aba.raffle.controllers.admin;
//
//import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
//import com.aba.raffle.proyecto.services.UserService;
//import com.aba.raffle.proyecto.services.PurchaseService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AdminControllerIntegrationTest { // 👈 PÚBLICA
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private PurchaseService purchaseService;
//
//    @Test
//    void testCrearUsuarioAdmin_Exito() throws Exception {
//        doNothing().when(userService).crearUserAdmin(any(UserAdminCreateDTO.class));
//
//        String jsonBody = """
//            {
//              "name": "Anderson Betancourt",
//              "email": "anderson.betancourta@uqvirtual.edu.co",
//              "password": "12345678"
//            }
//        """;
//
//        mockMvc.perform(post("/api/admin/crearUsuario")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.error").value(false))
//                .andExpect(jsonPath("$.respuesta").value("Usuario creado correctamente"));
//    }
//
//    @Test
//    void testCrearUsuarioAdmin_EmailInvalido() throws Exception {
//        String jsonBody = """
//            {
//              "name": "Anderson",
//              "email": "correo-no-valido",
//              "password": "12345678"
//            }
//        """;
//
//        mockMvc.perform(post("/api/admin/crearUsuario")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isBadRequest());
//    }
//}
