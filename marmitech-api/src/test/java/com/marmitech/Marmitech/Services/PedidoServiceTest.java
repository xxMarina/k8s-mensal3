// package com.marmitech.Marmitech.Services;

// import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoResponseDTO;
// import com.marmitech.Marmitech.Entity.*;
// import com.marmitech.Marmitech.Repository.ClienteRepository;
// import com.marmitech.Marmitech.Repository.PedidoRepository;
// import com.marmitech.Marmitech.Repository.ProdutoRepository;
// import com.marmitech.Marmitech.Repository.UsuarioRepository;
// import com.mysql.cj.exceptions.DataReadException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.stubbing.Answer;

// import java.math.BigDecimal;
// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class PedidoServiceTest {

//     @InjectMocks
//     PedidoService pedidoService;

//     @Mock
//     PedidoRepository pedidoRepository;

//     @Mock
//     UsuarioRepository usuarioRepository;

//     @Mock
//     ClienteRepository clienteRepository;

//     @Mock
//     ProdutoRepository produtoRepository;

//     Pedido pedido;
//     Usuario usuario;
//     Cliente cliente;
//     Produto produto;

//     @BeforeEach
//     void setUp() {
//         usuario = new Usuario();
//         usuario.setId( 1 );
//         usuario.setNome( "Caixa Teste" );

//         cliente = new Cliente();
//         cliente.setId( 1 );
//         cliente.setNome( "Cliente Teste" );

//         produto = new Produto();
//         produto.setId( 1 );
//         produto.setNome( "Produto Teste" );
//         produto.setPrecoUnitario( 10.00 );
//         produto.setEstoque( 100 );

//         PedidoItem item = new PedidoItem();
//         item.setProduto( produto );
//         item.setQuantidade( 2 );
//         item.setPrecoUnitarioPedido( produto.getPrecoUnitario() );
//         item.setSubtotal( 20.00 );

//         HistoricoCompra historico = new HistoricoCompra();
//         historico.setTipoEvento( "CRIADO" );
//         historico.setDescricao( "Pedido Criado" );

//         // Pedido principal
//         pedido = new Pedido();
//         pedido.setId( 1 );
//         pedido.setValorTotal( 20.00 );
//         pedido.setStatus( "FILA" );
//         pedido.setEnderecoEntrega( "Rua Teste, 123" );
//         pedido.setUsuario( usuario );
//         pedido.setCliente( cliente );
//         pedido.setPedidoItems( new HashSet<>( Set.of( item ) ) );
//         pedido.setHistoricos( List.of( historico ) );
//     }

//     @Test
//     @DisplayName("save - Deve salvar o Pedido")
//     void cenario01() {
//         when( usuarioRepository.findById( usuario.getId() ) ).thenReturn( Optional.of( usuario ) );
//         when( clienteRepository.findById( cliente.getId() ) ).thenReturn( Optional.of( cliente ) );
//         when( produtoRepository.findById( produto.getId() ) ).thenReturn( Optional.of( produto ) );
//         when( pedidoRepository.save( any( Pedido.class ) ) ).thenAnswer( (Answer<Pedido>) invocation -> {
//             Pedido savedPedido = invocation.getArgument( 0 );
//             savedPedido.setId( 1 );
//             assertNotNull( savedPedido.getDataPedido() );
//             return savedPedido;
//         } );

//         Pedido result = pedidoService.save( pedido );

//         assertNotNull( result );
//         assertEquals( 1, result.getId() );
//         assertEquals( "FILA", result.getStatus() );
//         assertEquals( usuario, result.getUsuario() );
//         assertEquals( cliente, result.getCliente() );

//         assertEquals( 1, result.getPedidoItems().size() );
//         assertEquals( 1, result.getHistoricos().size() );
//         verify( pedidoRepository, times( 1 ) ).save( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("save - Deve relacionar Pedido, Itens e Histórico")
//     void cenario02() {
//         when( usuarioRepository.findById( usuario.getId() ) ).thenReturn( Optional.of( usuario ) );
//         when( clienteRepository.findById( cliente.getId() ) ).thenReturn( Optional.of( cliente ) );
//         when( produtoRepository.findById( produto.getId() ) ).thenReturn( Optional.of( produto ) );
//         when( pedidoRepository.save( any( Pedido.class ) ) ).thenAnswer( (Answer<Pedido>) invocation -> {
//             Pedido savedPedido = invocation.getArgument( 0 );
//             savedPedido.setId( 1 );
//             return savedPedido;
//         } );

//         Pedido result = pedidoService.save( pedido );

//         assertNotNull( result );
//         assertEquals( 1, result.getId() );
//         // Verifica se o mapeamento Pedido <-> PedidoItem foi feito
//         PedidoItem itemVerificado = result.getPedidoItems().iterator().next();
//         assertEquals( result, itemVerificado.getPedido() );
//         // Verifica se o mapeamento Pedido <-> Historico foi feito
//         assertEquals( result, result.getHistoricos().get( 0 ).getPedido() );

//         verify( pedidoRepository, times( 1 ) ).save( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("save - Deve lançar exceção se Produto não for encontrado")
//     void cenario03() {
//         when( produtoRepository.findById( produto.getId() ) ).thenReturn( Optional.empty() );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> pedidoService.save( pedido ) );

//         assertEquals( "Produto não encontrado: 1", exception.getMessage() );
//         verify( pedidoRepository, never() ).save( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("save - Deve lançar exceção se Usuário não for encontrado")
//     void cenario04() {
//         when( produtoRepository.findById( produto.getId() ) ).thenReturn( Optional.of( produto ) );
//         when( usuarioRepository.findById( anyInt() ) ).thenReturn( Optional.empty() );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> pedidoService.save( pedido ) );
//         assertEquals( "Usuário não encontrado", exception.getMessage() );
//         verify( pedidoRepository, never() ).save( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("save - Deve lançar exceção se Cliente não for encontrado")
//     void cenario05() {
//         when( produtoRepository.findById( produto.getId() ) ).thenReturn( Optional.of( produto ) );
//         when( usuarioRepository.findById( anyInt() ) ).thenReturn( Optional.of( usuario ) );

//         when( clienteRepository.findById( anyInt() ) ).thenReturn( Optional.empty() );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> pedidoService.save( pedido ) );
//         assertEquals( "Cliente não encontrado", exception.getMessage() );
//         verify( pedidoRepository, never() ).save( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("findAll - Deve retornar lista de Pedidos mapeados para DTO")
//     void cenario06() {
//         PedidoItem itemCompleto = new PedidoItem();
//         itemCompleto.setProduto( produto );
//         itemCompleto.setPedido( pedido );
//         itemCompleto.setQuantidade( 2 );
//         itemCompleto.setPrecoUnitarioPedido( produto.getPrecoUnitario() );
//         itemCompleto.setSubtotal( 20.00 );

//         Pedido pedidoCompleto = new Pedido();
//         pedidoCompleto.setId( 1 );
//         pedidoCompleto.setCliente( cliente );
//         pedidoCompleto.setPedidoItems( new HashSet<>( Set.of( itemCompleto ) ) );

//         when( pedidoRepository.findAll() ).thenReturn( List.of( pedidoCompleto ) );

//         List<PedidoResponseDTO> result = pedidoService.findAll();

//         assertNotNull( result );
//         assertFalse( result.isEmpty() );
//         assertEquals( 1, result.size() );
//         assertEquals( "Cliente Teste", result.get( 0 ).nomeCliente() );
//         verify( pedidoRepository, times( 1 ) ).findAll();
//     }

//     @Test
//     @DisplayName("findById - Deve retornar o Pedido por ID")
//     void cenario07() {
//         when( pedidoRepository.findById( 1 ) ).thenReturn( Optional.of( pedido ) );
//         Pedido result = pedidoService.findById( 1 );
//         assertNotNull( result );
//     }

//     @Test
//     @DisplayName("findById - Deve lançar exceção se ID não for encontrado")
//     void cenario08() {
//         when( pedidoRepository.findById( 99 ) ).thenReturn( Optional.empty() );
//         assertThrows( RuntimeException.class, () -> pedidoService.findById( 99 ) );
//     }

//     @Test
//     @DisplayName("findByStatus - Deve retornar lista de Pedidos por Status")
//     void cenario09() {
//         when( pedidoRepository.findByStatus( "FILA" ) ).thenReturn( List.of( pedido ) );
//         List<Pedido> result = pedidoService.findByStatus( "FILA" );
//         assertNotNull( result );
//     }

//     @Test
//     @DisplayName("findByProdutoNome - Deve retornar lista de Pedidos por nome do Produto")
//     void cenario10() {
//         when( pedidoRepository.findByPedidoItemsProdutoNome( "Produto Teste" ) ).thenReturn( List.of( pedido ) );
//         List<Pedido> result = pedidoService.findByProdutoNome( "Produto Teste" );
//         assertNotNull( result );
//     }

//     @Test
//     @DisplayName("findByProduto - Deve retornar lista de Pedidos por ID de Produto")
//     void cenario11() {
//         when( pedidoRepository.findByPedidoItemsProduto( any( Produto.class ) ) ).thenReturn( List.of( pedido ) );

//         List<Pedido> result = pedidoService.findByProduto( produto.getId() );

//         assertNotNull( result );
//         assertFalse( result.isEmpty() );
//         assertEquals( 1, result.size() );

//         verify( pedidoRepository, times( 1 ) ).findByPedidoItemsProduto( any( Produto.class ) );
//     }

//     @Test
//     @DisplayName("findByProduto - Deve retornar lista vazia se nenhum Pedido for encontrado")
//     void cenario12() {
//         when( pedidoRepository.findByPedidoItemsProduto( any( Produto.class ) ) ).thenReturn( Collections.emptyList() );

//         List<Pedido> result = pedidoService.findByProduto( 999 );
//         assertNotNull( result );
//         assertTrue( result.isEmpty() );

//         verify( pedidoRepository, times( 1 ) ).findByPedidoItemsProduto( any( Produto.class ) );
//     }

//     @Test
//     @DisplayName("delete - Deve deletar o Pedido por ID")
//     void cenario13() {
//         when( pedidoRepository.findById( 1 ) ).thenReturn( Optional.of( pedido ) );
//         pedidoService.delete( 1 );
//         verify( pedidoRepository, times( 1 ) ).delete( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("delete - Deve lançar exceção se ID for negativo")
//     void cenario14() {
//         IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> pedidoService.delete( -1 ) );
//         assertEquals( "ID DO PEDIDO INVALIDO", exception.getMessage() );
//         verify( pedidoRepository, never() ).delete( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("update - Deve atualizar o Pedido")
//     void cenario15() {
//         when( pedidoRepository.findById( pedido.getId() ) ).thenReturn( Optional.of( pedido ) );

//         Pedido pedidoUpdate = new Pedido();
//         pedidoUpdate.setStatus( "ENVIADO" );
//         pedidoUpdate.setEnderecoEntrega( "Nova Rua Teste, 456" );
//         pedidoUpdate.setId( pedido.getId() );
//         pedidoUpdate.setCliente( cliente );
//         pedidoUpdate.setUsuario( usuario );
//         pedidoUpdate.setPedidoItems( pedido.getPedidoItems() );

//         when( pedidoRepository.save( any( Pedido.class ) ) ).thenAnswer( (Answer<Pedido>) invocation -> {
//             Pedido savedPedido = invocation.getArgument( 0 );
//             savedPedido.setStatus( pedidoUpdate.getStatus() );
//             savedPedido.setEnderecoEntrega( pedidoUpdate.getEnderecoEntrega() );
//             return savedPedido;
//         } );

//         Pedido result = pedidoService.update( pedido.getId(), pedidoUpdate );

//         assertNotNull( result );
//         assertEquals( pedido.getId(), result.getId() );
//         assertEquals( "ENVIADO", result.getStatus() );
//         assertEquals( "Nova Rua Teste, 456", result.getEnderecoEntrega() );

//         verify( pedidoRepository, times( 1 ) ).findById( pedido.getId() );
//         verify( pedidoRepository, times( 1 ) ).save( any( Pedido.class ) );
//     }

//     @Test
//     @DisplayName("update - Deve lançar exceção se Pedido não for encontrado")
//     void cenario16() {
//         when( pedidoRepository.findById( 99 ) ).thenReturn( Optional.empty() );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> pedidoService.update( 99, pedido ) );

//         assertEquals( "Pedido com ID 99 não encontrado", exception.getMessage() );

//         verify( pedidoRepository, times( 1 ) ).findById( 99 );
//         verify( pedidoRepository, never() ).save( any( Pedido.class ) );
//     }
// }