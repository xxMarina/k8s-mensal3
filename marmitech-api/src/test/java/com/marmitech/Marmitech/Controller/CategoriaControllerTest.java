package com.marmitech.Marmitech.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marmitech.Marmitech.DTO.RequestDTO.CategoriaRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.CategoriaResponseDTO;
import com.marmitech.Marmitech.Services.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.eq;

import com.marmitech.Marmitech.Security.JwtAuthFilter;
import com.marmitech.Marmitech.Security.JwUtil;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CategoriaControllerTest {

        @Autowired
        MockMvc mockMvc;
        @MockitoBean
        CategoriaService categoriaService;
        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private JwtAuthFilter jwtAuthFilter;

        @MockitoBean
        private JwUtil jwUtil;

        @Test
        @DisplayName("Deve retornar a lista de categorias com sucesso")
        void buscarcategorias() throws Exception {
                CategoriaResponseDTO categoria1 = new CategoriaResponseDTO(null, "Marmitex", "Marmita Grande");
                CategoriaResponseDTO categoria2 = new CategoriaResponseDTO(null, "Porcao torresmo",
                                "Porcao torresmo 350g");

                List<CategoriaResponseDTO> listadeCategorias = Arrays.asList(categoria1, categoria2);

                when(categoriaService.findAll()).thenReturn(listadeCategorias);

                mockMvc.perform(get("/api/categoria/findAll"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].nome", is("Marmitex")))
                                .andExpect(jsonPath("$[1].nome", is("Porcao torresmo")));
        }

        @Test
        @DisplayName("deve salvar uma categoria")
        @WithMockUser(roles = "ADMIN")
        void salvarcategoria() throws Exception {
                CategoriaResponseDTO categoriaSalva = new CategoriaResponseDTO(1, "Marmitas", "Marmitas variadas");

                when(categoriaService.save(any(CategoriaRequestDTO.class))).thenReturn(categoriaSalva);

                String jsonEnviado = objectMapper
                                .writeValueAsString(new CategoriaRequestDTO("Marmitas", "Marmitas variadas"));

                mockMvc.perform(post("/api/categoria/save").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonEnviado)).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.nome", is("Marmitas")))
                                .andExpect(jsonPath("$.descricao", is("Marmitas variadas")));
        }

        @Test
        @DisplayName("Deve deletar uma categoria com sucesso")
        @WithMockUser(roles = "ADMIN")
        void DeletarCategoriaComSucesso() throws Exception {

                Integer idParaDeletar = Math.toIntExact(1);

                doNothing().when(categoriaService).delete(idParaDeletar);

                mockMvc.perform(delete("/api/categoria/delete/{id}", idParaDeletar))
                                .andExpect(status().isNoContent());

                verify(categoriaService, times(1)).delete(idParaDeletar);
        }

        @Test
        @DisplayName("Deve atualizar uma categoria com sucesso")
        @WithMockUser(roles = "ADMIN")
        void deveAtualizarCategoriaComSucesso() throws Exception {

                Integer idParaAtualizar = 1;

                CategoriaResponseDTO categoriaAtualizadaRetornada = new CategoriaResponseDTO(idParaAtualizar,
                                "Marmitex Atualizada", "Descricao Atualizada");

                when(categoriaService.update(eq(idParaAtualizar), any(CategoriaRequestDTO.class)))
                                .thenReturn(categoriaAtualizadaRetornada);

                String jsonParaEnviar = objectMapper.writeValueAsString(
                                new CategoriaRequestDTO("Marmitex Atualizada", "Descricao Atualizada"));

                mockMvc.perform(put("/api/categoria/update/{id}", idParaAtualizar)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonParaEnviar))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(idParaAtualizar.intValue())))
                                .andExpect(jsonPath("$.nome", is("Marmitex Atualizada")))
                                .andExpect(jsonPath("$.descricao", is("Descricao Atualizada")));
        }
}