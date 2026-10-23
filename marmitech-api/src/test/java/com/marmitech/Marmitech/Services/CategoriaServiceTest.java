package com.marmitech.Marmitech.Services;
import com.marmitech.Marmitech.DTO.RequestDTO.CategoriaRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.CategoriaResponseDTO;
import com.marmitech.Marmitech.Entity.Categoria;
import com.marmitech.Marmitech.Repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService categoriaService;
    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve retornar todas as categorias")
    void deveRetornarTodasCategorias() {
        Categoria categoria = new Categoria();
        categoria.setNome( "Marmitas");
        categoria.setDescricao( "Tradicionais");
        List<Categoria> listaDeCategoriasFalsas = List.of(categoria);

        when(categoriaRepository.findAll()).thenReturn(listaDeCategoriasFalsas);

        List<CategoriaResponseDTO> resultado = categoriaService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Marmitas", resultado.get(0).nome());

        verify(categoriaRepository, times(1)).findAll();
    }
    @Test
    void buscarCategoria() {
    }


    @Test
    @DisplayName("Deve salvar uma nova categoria")
    void deveSalvarCategoria() {

        CategoriaRequestDTO dtoParaSalvar = new CategoriaRequestDTO("Marmitas", "Tradicionais");

        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(1);
        categoriaSalva.setNome("Marmitas");
        categoriaSalva.setDescricao("Tradicionais");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

        CategoriaResponseDTO resultado = categoriaService.save(dtoParaSalvar);

        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("Marmitas", resultado.nome());

        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve encontrar uma categoria pelo ID com sucesso")
    void deveEncontrarCategoriaPorId() {

        Categoria categoriaFalsa = new Categoria();
        categoriaFalsa.setId(1);
        categoriaFalsa.setNome("porcoes");
        categoriaFalsa.setDescricao("porcoes individuais");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaFalsa));

        CategoriaResponseDTO resultado = categoriaService.findById(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("porcoes", resultado.nome());
    }

    @Test
    @DisplayName("Deve deletar uma categoria com sucesso")
    void deletarCategoria() {

        Categoria categoriaDeletada = new Categoria();
        categoriaDeletada.setId(1);
        categoriaDeletada.setNome("Porcoes");
        categoriaDeletada.setDescricao("porcoes individuais");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaDeletada));
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaService.delete(1);

        verify(categoriaRepository, times(1)).findById(1);
        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve atualizar uma categoria com sucesso (lógica de update)")
    void deveAtualizarCategoria() {

        Categoria categoriaOriginal = new Categoria();
        categoriaOriginal.setId(1);
        categoriaOriginal.setNome("Nome original");
        categoriaOriginal.setDescricao("Descricao original");

        CategoriaRequestDTO dadosNovos = new CategoriaRequestDTO("Nova categoria", "Nova Descricao");

        Categoria categoriaAtualizada = new Categoria();
        categoriaAtualizada.setId(1);
        categoriaAtualizada.setNome("categoria atualizada");
        categoriaAtualizada.setDescricao("descricao atualizada");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaOriginal));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaAtualizada);

        CategoriaResponseDTO resultado = categoriaService.update(1, dadosNovos);

        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("categoria atualizada", resultado.nome());
        assertEquals("descricao atualizada", resultado.descricao());

        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }
}
