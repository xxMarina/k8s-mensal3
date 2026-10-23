package com.marmitech.Marmitech.Controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marmitech.Marmitech.DTO.RequestDTO.UsuarioRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.UsuarioResponseDTO;
import com.marmitech.Marmitech.Entity.Usuario;
import com.marmitech.Marmitech.Services.UsuarioService;

import com.marmitech.Marmitech.Security.JwtAuthFilter;
import com.marmitech.Marmitech.Security.JwUtil;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UsuarioControllerTest {

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        private UsuarioService usuarioService;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private JwtAuthFilter jwtAuthFilter;

        @MockitoBean
        private JwUtil jwUtil;

        UsuarioResponseDTO usuarioResponse;

        UsuarioRequestDTO usuarioRequest;

        @BeforeEach
        void setUp() {
                usuarioResponse = new UsuarioResponseDTO(
                                1,
                                "Marmitech",
                                "marmitech@gmail.com",
                                "Caixa",
                                LocalDate.now());

                usuarioRequest = new UsuarioRequestDTO(
                                "Marmitech",
                                "marmitech@gmail.com",
                                "123456",
                                "Caixa");
        }

        @Test
        @DisplayName("01 - POST /save - Deve criar um novo usuario com sucesso")
        @WithMockUser(roles = "ADMIN")
        void cenario01() throws Exception {
                Mockito.when(usuarioService.save(any(UsuarioRequestDTO.class)))
                                .thenReturn(usuarioResponse);

                String usuarioJson = objectMapper.writeValueAsString(usuarioRequest);

                mockMvc.perform(post("/api/usuario/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usuarioJson))
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nome").value("Marmitech"))
                                .andExpect(jsonPath("$.email").value("marmitech@gmail.com"))
                                .andExpect(jsonPath("$.cargo").value("Caixa"))
                                .andExpect(jsonPath("$.dataCriacao").value(LocalDate.now().toString()));
        }

        @Test
        @DisplayName("02 - GET /findAll - Deve retornar a lista de usuario cadastrados")
        void cenario02() throws Exception {
                Mockito.when(usuarioService.findAll())
                                .thenReturn(List.of(usuarioResponse));

                mockMvc.perform(get("/api/usuario/findAll"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].nome").value("Marmitech"))
                                .andExpect(jsonPath("$[0].email").value("marmitech@gmail.com"))
                                .andExpect(jsonPath("$[0].cargo").value("Caixa"))
                                .andExpect(jsonPath("$[0].dataCriacao").value(LocalDate.now().toString()));
        }

        @Test
        @DisplayName("03 - GET /findById - Buscar usuário pelo ID")
        void cenario03() throws Exception {
                Mockito.when(usuarioService.findById(anyInt()))
                                .thenReturn(usuarioResponse);

                mockMvc.perform(get("/api/usuario/findById/{id}", 1))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nome").value("Marmitech"))
                                .andExpect(jsonPath("$.email").value("marmitech@gmail.com"))
                                .andExpect(jsonPath("$.cargo").value("Caixa"))
                                .andExpect(jsonPath("$.dataCriacao").value(LocalDate.now().toString()));
        }

        @Test
        @DisplayName("04-Cenario UP Deve atualizar os dados de um um usuario existente")
        @WithMockUser(roles = "ADMIN")
        void cenario04() throws Exception {
                Mockito.when(usuarioService.update(anyInt(), any(UsuarioRequestDTO.class)))
                                .thenReturn(usuarioResponse);

                String usuarioJson = objectMapper.writeValueAsString(usuarioRequest);

                mockMvc.perform(put("/api/usuario/update/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usuarioJson))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nome").value("Marmitech"))
                                .andExpect(jsonPath("$.email").value("marmitech@gmail.com"))
                                .andExpect(jsonPath("$.cargo").value("Caixa"))
                                .andExpect(jsonPath("$.dataCriacao").value(LocalDate.now().toString()));
        }

        @Test
        @DisplayName("05 - Cenario Delete - Deve excluir um usuário com sucesso")
        @WithMockUser(roles = "ADMIN")
        void cenario05() throws Exception {
                Mockito.doNothing().when(usuarioService).delete(anyInt());

                mockMvc.perform(delete("/api/usuario/delete/{id}", 1))
                                .andDo(print())
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("06 - Cenario  Deve fazer login com sucesso")
        void cenario06() throws Exception {
                Usuario usuarioLogado = new Usuario();
                usuarioLogado.setId(1);
                usuarioLogado.setNome("Marmitech");
                usuarioLogado.setEmail("marmitech@gmail.com");
                usuarioLogado.setSenha("123456");
                usuarioLogado.setCargo("Caixa");
                usuarioLogado.setDataCriacao(LocalDate.now());

                Mockito.when(usuarioService.login(anyString(), anyString())).thenReturn(usuarioLogado);

                Usuario loginPayload = new Usuario();
                loginPayload.setEmail("marmitech@gmail.com");
                loginPayload.setSenha("123456");

                mockMvc.perform(post("/api/usuario/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginPayload)))
                                .andDo(print())
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("07 - Cenario  Deve buscar usuários pelo cargo")
        void cenario07() throws Exception {
                Mockito.when(usuarioService.findByCargo(anyString())).thenReturn(List.of(usuarioResponse));

                mockMvc.perform(get("/api/usuario/findByCargo/{cargo}", "Caixa"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].nome").value("Marmitech"))
                                .andExpect(jsonPath("$[0].email").value("marmitech@gmail.com"))
                                .andExpect(jsonPath("$[0].cargo").value("Caixa"))
                                .andExpect(jsonPath("$[0].dataCriacao").value(LocalDate.now().toString()));
        }

        @Test
        @DisplayName("08 - Cenario ")
        void cenario08() throws Exception {
                Mockito.when(usuarioService.findByNome(anyString())).thenReturn(List.of(usuarioResponse));

                mockMvc.perform(get("/api/usuario/findByNome/{nome}", "Marmitech"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].nome").value("Marmitech"))
                                .andExpect(jsonPath("$[0].email").value("marmitech@gmail.com"))
                                .andExpect(jsonPath("$[0].cargo").value("Caixa"))
                                .andExpect(jsonPath("$[0].dataCriacao").value(LocalDate.now().toString()));
        }
}