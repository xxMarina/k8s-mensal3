// package com.marmitech.Marmitech.Services;

// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.junit.jupiter.MockitoExtension;

// import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoItemResponseDTO;
// import com.marmitech.Marmitech.Entity.Cliente;
// import com.marmitech.Marmitech.Entity.Pedido;
// import com.marmitech.Marmitech.Entity.PedidoItem;
// import com.marmitech.Marmitech.Entity.Produto;
// import com.marmitech.Marmitech.Repository.PedidoItemRepository;
// import com.marmitech.Marmitech.Repository.PedidoRepository;
// import com.marmitech.Marmitech.Repository.ProdutoRepository;

// @ExtendWith(MockitoExtension.class)
// public class PedidoItemServiceTest {

//     @InjectMocks
//     PedidoItemService pedidoItemService;

//     @Mock
//     PedidoRepository pedidoRepository;

//     @Mock
//     ProdutoRepository produtoRepository;

//     @Mock
//     PedidoItemRepository pedidoItemRepository;

//     @Test
//     @DisplayName("Teste: Pedido Item Save")
//     void test10() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, 10.00, 10.00);

//         PedidoItem pedido1 = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         PedidoItem pedidoSalvo = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        
//         when(pedidoItemRepository.save(any(PedidoItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         PedidoItem pedidoResultado = this.pedidoItemService.save(pedidoItemResponseDTO);

//         assertEquals(pedidoSalvo.getPedido(), pedidoResultado.getPedido());
//         assertEquals(pedidoSalvo.getProduto(), pedidoResultado.getProduto());
//         assertEquals(pedidoSalvo.getPrecoUnitarioPedido(), pedidoResultado.getPrecoUnitarioPedido());
//     }

//     @Test
//     @DisplayName("Teste: Pedido Item Save Preço Unitario Nulo")
//     void test11() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, null, 10.00);

//         when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        
//         assertThrows(IllegalArgumentException.class, () -> {
//             pedidoItemService.save(pedidoItemResponseDTO);
//         });
//     }


//     @Test
//     @DisplayName("Teste: Pedido Item Save Pedido Nulo")
//     void test12() {
//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, null, 10.00);
        
//         RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
//             pedidoItemService.save(pedidoItemResponseDTO);
//         });

//         assertEquals("Pedido não encontrado com ID: 1", excecao.getMessage());
//     }

//     @Test
//     @DisplayName("Teste: Pedido Item Update")
//     void test13() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, 10.00, 10.00);

//         PedidoItem pedidoItem = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         when(pedidoItemRepository.findById(1)).thenReturn(Optional.of(pedidoItem));

//         when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

//         when(pedidoItemRepository.save(any(PedidoItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         PedidoItem pedidoResultado = this.pedidoItemService.update(pedidoItemResponseDTO, 1);

//         assertEquals(pedidoItem.getPedido(), pedidoResultado.getPedido());
//         assertEquals(pedidoItem.getProduto(), pedidoResultado.getProduto());
//         assertEquals(pedidoItem.getPrecoUnitarioPedido(), pedidoResultado.getPrecoUnitarioPedido());
//     }

//     @Test
//     @DisplayName("Teste: Pedido Item Update Pedido Nulo")
//     void test14() {
//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 2, 2, "teste", "prodteste", 1, 10.00, 10.00);

//         when(pedidoItemRepository.findById(2)).thenReturn(Optional.of(new PedidoItem()));

//         when(pedidoRepository.findById(2)).thenThrow(RuntimeException.class);

//         assertThrows(RuntimeException.class, () -> { this.pedidoItemService.update(pedidoItemResponseDTO, 1); }); 
//     }

//     @Test
//     @DisplayName("Teste: Pedido Item Update Pedido Item Nulo")
//     void test15() {     
//         when(pedidoItemRepository.findById(1)).thenReturn(Optional.empty());

//         RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
//             pedidoItemService.update(new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", -1, -10.00, 10.00), 1);
//         });

//         assertEquals("PedidoItem not found with id: 1", excecao.getMessage());
//     }

//     @Test
//     @DisplayName("Teste: Pedido Item Update novo pedido não encontrado")
//     void test16() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, 10.00, 10.00);

//         PedidoItem pedidoItem = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         when(pedidoItemRepository.findById(1)).thenReturn(Optional.of(pedidoItem));

//         when(pedidoRepository.findById(1)).thenReturn(Optional.empty());

//         RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
//             pedidoItemService.update(pedidoItemResponseDTO, 1);
//         });

//         assertEquals("Novo Pedido não encontrado com ID: 1", excecao.getMessage());
//     }

//         @Test
//     @DisplayName("Teste: Pedido Item Update novo produto não encontrado")
//     void test17() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, 10.00, 10.00);

//         PedidoItem pedidoItem = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         when(pedidoItemRepository.findById(1)).thenReturn(Optional.of(pedidoItem));

//         when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

//         when(produtoRepository.findById(1)).thenReturn(Optional.empty());

//         RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
//             pedidoItemService.update(pedidoItemResponseDTO, 1);
//         });

//         assertEquals("Novo Produto não encontrado com ID: 1", excecao.getMessage());
//     }

//     @Test
//     @DisplayName("Teste: Pedido Item Update Validadores")
//     void test18() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItemResponseDTO pedidoItemResponseDTO = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, 0.00, 0.00);

//         PedidoItem pedidoItem = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         when(pedidoItemRepository.findById(1)).thenReturn(Optional.of(pedidoItem));

//         when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

//         when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

//         when(pedidoItemRepository.save(any(PedidoItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         PedidoItem pedidoResultado = this.pedidoItemService.update(pedidoItemResponseDTO, 1);

//         assertEquals(pedidoItem.getPedido(), pedidoResultado.getPedido());
//         assertEquals(pedidoItem.getProduto(), pedidoResultado.getProduto());
//         assertEquals(pedidoItem.getPrecoUnitarioPedido(), pedidoResultado.getPrecoUnitarioPedido());
//     }

//     @Test
//     @DisplayName("Teste: Delete Pedido Item Service")
//     void test19() {
//         PedidoItem pedidoItem = new PedidoItem();
//         pedidoItem.setId(1);

//         assertDoesNotThrow(() -> {
//            pedidoItemService.delete(1);
//         });

//         verify(pedidoItemRepository, times(1)).deleteById(1);
        
//         assertEquals("Linha deleteda da tabela.", pedidoItemService.delete(1));  
//     }

//     @Test
//     @DisplayName("Teste: Find By Id Pedido Item Service")
//     void test20() {
//         Pedido pedido = new Pedido();

//         Produto produto = new Produto();

//         PedidoItem pedidoItem = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         when(pedidoItemRepository.findById(1)).thenReturn(Optional.of(pedidoItem));

//         PedidoItem pedidoResultado = this.pedidoItemService.findById(1);

//         assertEquals(pedidoItem.getPedido(), pedidoResultado.getPedido());
//         assertEquals(pedidoItem.getProduto(), pedidoResultado.getProduto());
//         assertEquals(pedidoItem.getPrecoUnitarioPedido(), pedidoResultado.getPrecoUnitarioPedido());
//     }

//     @Test
//     @DisplayName("Teste: FindAll Pedido Item Service")
//     void test21() {
//         Cliente cliente = new Cliente();
//         cliente.setNome("teste");

//         Pedido pedido = new Pedido();
//         pedido.setId(1);
//         pedido.setCliente(cliente);

//         Produto produto = new Produto();
//         produto.setId(1);

//         PedidoItemResponseDTO pedidoItemResponseDTO1 = new PedidoItemResponseDTO(1, 1, 1, "teste", "prodteste", 1, 10.00, 10.00);

//         PedidoItemResponseDTO pedidoItemResponseDTO2 = new PedidoItemResponseDTO(2, 1, 1, "teste2", "prodteste2", 2, 20.00, 40.00);

//         PedidoItem pedidoItem = new PedidoItem(1, 1, 10.00, 10.00, pedido, produto);

//         PedidoItem pedidoItem2 = new PedidoItem(2, 2, 20.00, 40.00, pedido, produto);

//         List<PedidoItem> pedidoItems = List.of(pedidoItem, pedidoItem2);

//         when(pedidoItemRepository.findAll()).thenReturn(pedidoItems);

//         List<PedidoItemResponseDTO> pedidoResultado = this.pedidoItemService.findAll();

//         assertEquals(pedidoItemResponseDTO1.pedidoId(), pedidoResultado.get(0).pedidoId());
//         assertEquals(pedidoItemResponseDTO1.produtoId(), pedidoResultado.get(0).produtoId());
//         assertEquals(2, pedidoResultado.size());
//         assertEquals(pedidoItemResponseDTO2.precoUnitarioPedido(), pedidoResultado.get(1).precoUnitarioPedido());
//     }
// }
