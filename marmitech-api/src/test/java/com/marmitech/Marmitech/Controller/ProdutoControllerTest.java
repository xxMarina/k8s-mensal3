package com.marmitech.Marmitech.Controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marmitech.Marmitech.DTO.RequestDTO.ProdutoSaveDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.ProdutoListaDTO;
import com.marmitech.Marmitech.Entity.Categoria;
import com.marmitech.Marmitech.Entity.PedidoItem;
import com.marmitech.Marmitech.Entity.Produto;
import com.marmitech.Marmitech.Services.ProdutoService;

import com.marmitech.Marmitech.Security.JwtAuthFilter;
import com.marmitech.Marmitech.Security.JwUtil;

@WebMvcTest(ProdutoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProdutoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProdutoService produtoService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwUtil jwUtil;

    @Test
    @DisplayName("Teste: Find All Produtos Controller")
    public void test22() throws Exception {

        ProdutoListaDTO produto1 = new ProdutoListaDTO(1, "Produto A", "Descrição A", 1, "Categoria A", "Hoje", 10.00,
                10, "111");
        ProdutoListaDTO produto2 = new ProdutoListaDTO(2, "Produto B", "Descrição B", 2, "Categoria B", "Hoje", 20.00,
                20, "222");
        List<ProdutoListaDTO> produtos = Arrays.asList(produto1, produto2);

        given(produtoService.findAll()).willReturn(produtos);

        mockMvc.perform(get("/api/produto/findAll"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].nome").value("Produto B"));
    }

    @Test
    @DisplayName("Teste: Find By Id Produto Controller")
    public void test23() throws Exception {
        Produto produto = new Produto();
        produto.setId(31);

        given(produtoService.findById(1)).willReturn(produto);

        mockMvc.perform(get("/api/produto/findById/{id}", 1))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(31));
    }

    @Test
    @DisplayName("Teste: Save Produto Controller")
    @WithMockUser(roles = "ADMIN")
    public void test24() throws Exception {
        ProdutoListaDTO produto1 = new ProdutoListaDTO(1, "Produto A", "Descrição A", 1, "Categoria A", "Hoje", 10.00,
                10, "111");
        ProdutoSaveDTO produto2 = new ProdutoSaveDTO("Produto A", "Descrição A", 1, 10, 10.00, "111");

        given(produtoService.save(any(ProdutoSaveDTO.class))).willReturn(produto1);

        mockMvc.perform(post("/api/produto/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produto2)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Produto A"));
    }

    @Test
    @DisplayName("Teste: Delete Produto Controller")
    @WithMockUser(roles = "ADMIN")
    public void test25() throws Exception {
        doNothing().when(produtoService).delete(1);

        mockMvc.perform(delete("/api/produto/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Teste: Update Produto Controller")
    @WithMockUser(roles = "ADMIN")
    public void test26() throws Exception {
        Set<PedidoItem> pedidoItem = new HashSet<>();

        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNome("Categoria A");
        Produto produto = new Produto(1, "111", "Produto A", "Descrição A", 10.00, 10, categoria, "hoje", pedidoItem);

        given(produtoService.update(1, produto)).willReturn(produto);

        mockMvc.perform(put("/api/produto/update/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}