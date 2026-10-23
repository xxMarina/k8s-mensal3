// package com.marmitech.Marmitech.Services;

// import com.marmitech.Marmitech.DTO.RequestDTO.HistoricoSaveDTO;
// import com.marmitech.Marmitech.DTO.ResponseDTO.HistoricoResponseDTO;
// import com.marmitech.Marmitech.Entity.HistoricoCompra;
// import com.marmitech.Marmitech.Entity.Pedido;
// import com.marmitech.Marmitech.Repository.HistoricoCompraRepository;
// import com.marmitech.Marmitech.Repository.PedidoRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class HistoricoCompraServiceTest {

//     @InjectMocks
//     HistoricoCompraService historicoCompraService;

//     @Mock
//     HistoricoCompraRepository historicoCompraRepository;

//     @Mock
//     PedidoRepository pedidoRepository;

//     HistoricoCompra historicoCompra;
//     HistoricoCompra historicoParaAtualizar;
//     Pedido pedido;

//     HistoricoSaveDTO historicoSaveDTO;

//     @BeforeEach
//     void setUp() {
//         pedido = new Pedido();
//         pedido.setId( 1 );

//         historicoCompra = new HistoricoCompra();
//         historicoCompra.setId( 1 );
//         historicoCompra.setDataEvento( "2025-10-25" );
//         historicoCompra.setTipoEvento( "CRIADO" );
//         historicoCompra.setDescricao( "Pedido Criado" );
//         historicoCompra.setPedido( pedido );

//         historicoSaveDTO = new HistoricoSaveDTO(
//                 1,
//                 "Pedido Criado via DTO",
//                 "2025-10-26",
//                 "CRIADO_DTO"
//         );

//         historicoParaAtualizar = new HistoricoCompra();
//         historicoParaAtualizar.setDescricao( "Descricao Atualizada" );
//         historicoParaAtualizar.setDataEvento( "2025-10-27" );
//         historicoParaAtualizar.setTipoEvento( "ATUALIZADO" );
//     }

//     @Test
//     @DisplayName("save - Deve salvar um HistoricoCompra com sucesso")
//     void cenario01() {
//         when( pedidoRepository.findById( 1 ) ).thenReturn( Optional.of( pedido ) );
//         when( historicoCompraRepository.save( any( HistoricoCompra.class ) ) ).thenAnswer( invocation -> {
//             HistoricoCompra histSalvo = invocation.getArgument( 0 );
//             histSalvo.setId( 2 );
//             return histSalvo;
//         } );

//         HistoricoCompra result = historicoCompraService.save( historicoSaveDTO );

//         assertNotNull( result );
//         assertEquals( 2, result.getId() );
//         assertEquals( "CRIADO_DTO", result.getTipoEvento() );
//         assertEquals( pedido, result.getPedido() );
//         verify( pedidoRepository, times( 1 ) ).findById( 1 );
//         verify( historicoCompraRepository, times( 1 ) ).save( any( HistoricoCompra.class ) );
//     }

//     @Test
//     @DisplayName("save - Deve lançar exceção se Pedido não for encontrado")
//     void cenario02() {
//         when( pedidoRepository.findById( 99 ) ).thenReturn( Optional.empty() );
//         HistoricoSaveDTO dtoPedidoInvalido = new HistoricoSaveDTO( 99, "desc", "data", "tipo" );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> {
//             historicoCompraService.save( dtoPedidoInvalido );
//         } );

//         assertEquals( "Pedido com ID 99 não encontrado", exception.getMessage() );
//         verify( historicoCompraRepository, never() ).save( any() );
//     }

//     @Test
//     @DisplayName("update - Deve atualizar um HistoricoCompra com sucesso")
//     void cenario03() {

//         when( historicoCompraRepository.findById( 1 ) ).thenReturn( Optional.of( historicoCompra ) );
//         when( historicoCompraRepository.save( any( HistoricoCompra.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

//         HistoricoCompra result = historicoCompraService.update( historicoParaAtualizar, 1 );

//         assertNotNull( result );
//         assertEquals( 1, result.getId() );
//         assertEquals( "ATUALIZADO", result.getTipoEvento() );
//         assertEquals( "Descricao Atualizada", result.getDescricao() );
//         assertEquals( "2025-10-27", result.getDataEvento() );
//         verify( historicoCompraRepository, times( 1 ) ).findById( 1 );
//         verify( historicoCompraRepository, times( 1 ) ).save( historicoCompra );
//     }

//     @Test
//     @DisplayName("update - Deve manter valores antigos se os novos forem nulos/vazios")
//     void cenario04() {
//         HistoricoCompra updateParcial = new HistoricoCompra();
//         updateParcial.setDescricao( "Descricao Nova" );
//         updateParcial.setDataEvento( null );
//         updateParcial.setTipoEvento( "" );

//         when( historicoCompraRepository.findById( 1 ) ).thenReturn( Optional.of( historicoCompra ) );
//         when( historicoCompraRepository.save( any( HistoricoCompra.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

//         HistoricoCompra result = historicoCompraService.update( updateParcial, 1 );

//         assertNotNull( result );
//         assertEquals( "Descricao Nova", result.getDescricao() );
//         assertEquals( "CRIADO", result.getTipoEvento() );
//         assertEquals( "2025-10-25", result.getDataEvento() );
//         verify( historicoCompraRepository, times( 1 ) ).save( historicoCompra );
//     }

//     @Test
//     @DisplayName("update - Deve lançar exceção se Histórico não for encontrado")
//     void cenario05() {
//         when( historicoCompraRepository.findById( 99 ) ).thenReturn( Optional.empty() );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> {
//             historicoCompraService.update( historicoParaAtualizar, 99 );
//         } );

//         assertEquals( "Historico com ID 99 não encontrado para atualização", exception.getMessage() );
//         verify( historicoCompraRepository, never() ).save( any() );
//     }

//     @Test
//     @DisplayName("delete - Deve deletar um HistoricoCompra com sucesso")
//     void cenario06() {
//         when( historicoCompraRepository.existsById( 1 ) ).thenReturn( true );
//         doNothing().when( historicoCompraRepository ).deleteById( 1 );

//         String result = historicoCompraService.delete( 1 );

//         assertEquals( "Historico deletado com sucesso!", result );
//         verify( historicoCompraRepository, times( 1 ) ).deleteById( 1 );
//     }

//     @Test
//     @DisplayName("delete - Deve lançar exceção se ID for inválido")
//     void cenario07() {
//         IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
//             historicoCompraService.delete( -1 );
//         } );

//         assertEquals( "ID DO HISTORICO INVALIDO", exception.getMessage() );
//         verify( historicoCompraRepository, never() ).deleteById( anyInt() );
//     }

//     @Test
//     @DisplayName("delete - Deve lançar exceção se Histórico não existir")
//     void cenario08() {
//         when( historicoCompraRepository.existsById( 99 ) ).thenReturn( false );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> {
//             historicoCompraService.delete( 99 );
//         } );

//         assertEquals( "Historico com ID 99 não encontrado", exception.getMessage() );
//         verify( historicoCompraRepository, times( 1 ) ).existsById( 99 );
//         verify( historicoCompraRepository, never() ).deleteById( anyInt() );
//     }

//     @Test
//     @DisplayName("findById - Deve buscar um HistoricoCompra por ID (retornando DTO)")
//     void cenario09() {
//         when( historicoCompraRepository.findById( 1 ) ).thenReturn( Optional.of( historicoCompra ) );

//         HistoricoResponseDTO result = historicoCompraService.findById( 1 );

//         assertNotNull( result );

//         assertEquals( 1, result.id() );
//         assertEquals( "CRIADO", result.tipoEvento() );
//         assertEquals( 1, result.pedidoId() );
//         verify( historicoCompraRepository, times( 1 ) ).findById( 1 );
//     }

//     @Test
//     @DisplayName("findById - Deve lançar exceção se ID for inválido")
//     void cenario10() {
//         IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
//             historicoCompraService.findById( -1 );
//         } );

//         assertEquals( "ID DO HISTORICO INVALIDO", exception.getMessage() );
//         verify( historicoCompraRepository, never() ).findById( anyInt() );
//     }

//     @Test
//     @DisplayName("findById - Deve lançar exceção se Histórico não for encontrado")
//     void cenario11() {
//         when( historicoCompraRepository.findById( 99 ) ).thenReturn( Optional.empty() );

//         RuntimeException exception = assertThrows( RuntimeException.class, () -> {
//             historicoCompraService.findById( 99 );
//         } );

//         assertEquals( "Historico com ID 99 não encontrado", exception.getMessage() );
//     }

//     @Test
//     @DisplayName("findAll - Deve retornar lista de HistoricoResponseDTO")
//     void cenario12() {
//         when( historicoCompraRepository.findAll() ).thenReturn( List.of( historicoCompra ) );
//         List<HistoricoResponseDTO> result = historicoCompraService.findAll();

//         assertNotNull( result );
//         assertEquals( 1, result.size() );
//         assertEquals( 1, result.get( 0 ).id() );
//         verify( historicoCompraRepository, times( 1 ) ).findAll();
//     }

//     @Test
//     @DisplayName("findByDataEvento - Deve retornar lista de HistoricoResponseDTO por Data")
//     void cenario13() {
//         when( historicoCompraRepository.findByDataEvento( "2025-10-25" ) ).thenReturn( List.of( historicoCompra ) );
//         List<HistoricoResponseDTO> result = historicoCompraService.findByDataEvento( "2025-10-25" );

//         assertNotNull( result );
//         assertEquals( 1, result.size() );
//         assertEquals( "2025-10-25", result.get( 0 ).dataEvento() );
//         verify( historicoCompraRepository, times( 1 ) ).findByDataEvento( "2025-10-25" );
//     }
// }