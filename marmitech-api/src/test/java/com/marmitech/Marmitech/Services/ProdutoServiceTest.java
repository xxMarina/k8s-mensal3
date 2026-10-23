// package com.marmitech.Marmitech.Services;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import com.marmitech.Marmitech.DTO.RequestDTO.ProdutoSaveDTO;
// import com.marmitech.Marmitech.DTO.ResponseDTO.ProdutoListaDTO;
// import com.marmitech.Marmitech.Entity.Produto;
// import com.marmitech.Marmitech.Repository.PedidoItemRepository;
// import com.marmitech.Marmitech.Repository.ProdutoRepository;


// @ExtendWith(MockitoExtension.class)
// public class ProdutoServiceTest {

//     @InjectMocks
//     ProdutoService produtoService;

//     @Mock
//     ProdutoRepository produtoRepository;

//     @Mock
//     PedidoItemRepository pedidoItemRepository;

//     @Test
//     @DisplayName("Teste: FindAll Produto Service")
//     void test01() {
//         Produto produto1 = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());
//         Produto produto2 = new Produto(2, "SKU002", "Produto B", "Descricao B", 20.00, 200, "Categoria B", "2025-10-19", Collections.emptySet());
//         List<Produto> produtos = Arrays.asList(produto1, produto2);

//         when(produtoRepository.findAll()).thenReturn(produtos);

//         List<ProdutoListaDTO> resposta = this.produtoService.findAll();

//         assertEquals(2, resposta.size());        
//         assertEquals("Produto A", resposta.get(0).nome());
//     }


//     @Test
//     @DisplayName("Teste: FindById Produto Service")
//     void test02() {
//         Produto produto1 = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto1));

//         Produto produto = this.produtoService.findById(1);

//         assertEquals(produto1, produto);
//     }

//     @Test
//     @DisplayName("Teste: FindById Produto Service Exceção")
//     void test03() {
//         when(produtoRepository.findById(-1)).thenReturn(Optional.empty());

//         assertThrows(RuntimeException.class, () -> { produtoService.findById(-1); } );
//     }

//     @Test
//     @DisplayName("Teste: Delete Produto Service Exceção")
//     void test04() {
//         Produto produto = new Produto();

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

//         when(pedidoItemRepository.existsByProdutoId(1)).thenReturn(true);

//         assertThrows(IllegalStateException.class, () -> {
//             produtoService.delete(1);
//         } );
//     }

//     @Test
//     @DisplayName("Teste: Delete Produto Service")
//     void test05() {
//         Produto produto = new Produto();
//         produto.setId(1);

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

//         when(pedidoItemRepository.existsByProdutoId(1)).thenReturn(false);

//         assertDoesNotThrow(() -> {
//             produtoService.delete(1);
//         });

//         verify(produtoRepository, times(1)).delete(produto);
//     }

//     @Test
//     @DisplayName("Teste: Save Produto Service")
//     void test06() {
//         Produto produto = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());
        
//         ProdutoSaveDTO produtoSaveDTO = new ProdutoSaveDTO("Produto A", "Descricao A", "Categoria A", 100, 10.00, "SKU001");
        
//         when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

//         ProdutoListaDTO produtoRetorno = this.produtoService.save(produtoSaveDTO);

//         assertEquals(produtoSaveDTO.nome(), produtoRetorno.nome());
//     }

//     @Test
//     @DisplayName("Teste: Update Produto Service")
//     void test07() {
//         Produto produto = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());

//         Produto produtoSalvo = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoSalvo));

//         when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Produto produtoRetorno = produtoService.update(1, produto);

//         assertEquals(produtoSalvo.getNome(), produtoRetorno.getNome());

//         verify(produtoRepository, times(1)).save(produtoSalvo);
//     }

//     @Test
//     @DisplayName("Teste: Update Produto Service IFs is blank")
//     void test08() {
//         Produto produto = new Produto(1, "", "",  "" , null, -100, "", "2025-10-19", Collections.emptySet());

//         Produto produtoSalvo = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoSalvo));

//         when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Produto produtoRetorno = produtoService.update(1, produto);

//         assertEquals(produtoSalvo.getNome(), produtoRetorno.getNome());
//         assertEquals(produtoSalvo.getDescricao(), produtoRetorno.getDescricao());
//         assertEquals(produtoSalvo.getEstoque(), produtoRetorno.getEstoque());
//         assertEquals(produtoSalvo.getSku(), produtoRetorno.getSku());
//         assertEquals(produtoSalvo.getPrecoUnitario(), produtoRetorno.getPrecoUnitario());
//         assertEquals(produtoSalvo.getCategoria(), produtoRetorno.getCategoria());

//         verify(produtoRepository, times(1)).save(produtoSalvo);
//     }
    
//     @Test
//     @DisplayName("Teste: Update Produto Service IFs not null")
//     void test09() {
//         Produto produto = new Produto(1, null, null,  null , -10.00, -100, null, "2025-10-19", Collections.emptySet());

//         Produto produtoSalvo = new Produto(1, "SKU001", "Produto A", "Descricao A", 10.00, 100, "Categoria A", "2025-10-19", Collections.emptySet());

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoSalvo));

//         when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Produto produtoRetorno = produtoService.update(1, produto);

//         assertEquals(produtoSalvo.getNome(), produtoRetorno.getNome());
//         assertEquals(produtoSalvo.getDescricao(), produtoRetorno.getDescricao());
//         assertEquals(produtoSalvo.getEstoque(), produtoRetorno.getEstoque());
//         assertEquals(produtoSalvo.getSku(), produtoRetorno.getSku());
//         assertEquals(produtoSalvo.getPrecoUnitario(), produtoRetorno.getPrecoUnitario());
//         assertEquals(produtoSalvo.getCategoria(), produtoRetorno.getCategoria());

//         verify(produtoRepository, times(1)).save(produtoSalvo);
//     }    
// }