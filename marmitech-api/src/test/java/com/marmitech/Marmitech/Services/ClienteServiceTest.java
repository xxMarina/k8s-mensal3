// package com.marmitech.Marmitech.Services;

// import com.marmitech.Marmitech.Entity.Cliente;
// import com.marmitech.Marmitech.Repository.ClienteRepository;
// import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.context.ActiveProfiles;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// @ActiveProfiles("test")
// class ClienteServiceTest {

//     @Mock
//     private ClienteRepository clienteRepository;

//     @InjectMocks
//     private ClienteService clienteService;

//     private Cliente cliente;

//     @BeforeEach
//     void setUp() {
//         cliente = new Cliente();
//         cliente.setId(1);
//         cliente.setNome("Maria");
//         cliente.setEmail("maria@exemplo.com");
//         cliente.setTelefone("11999999999");
//         cliente.setEndereco("Rua A, 123");
//         cliente.setCpfCnpj("11122233344");
//         cliente.setDataCadastro(LocalDate.now().toString());
//     }

//     //  CENÁRIO 1 - SALVAR CLIENTE NORMAL
//     @Test
//     @DisplayName("Salvar cliente com sucesso")
//     void deveSalvarCliente() {
//         when(clienteRepository.findByNome("Maria")).thenReturn(Collections.emptyList());
//         when(clienteRepository.findByCpfCnpj("11122233344")).thenReturn(Optional.empty());
//         when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
//             Cliente c = invocation.getArgument(0);
//             c.setId(10);
//             return c;
//         });

//         Cliente salvo = clienteService.save(cliente);

//         assertNotNull(salvo);
//         assertEquals(10, salvo.getId());
//         verify(clienteRepository, times(1)).save(any());
//     }

//     //  CENÁRIO 2 - NOME DUPLICADO
//     @Test
//     @DisplayName("Não deve salvar cliente com nome duplicado")
//     void deveLancarErroNomeDuplicado() {
//         when(clienteRepository.findByNome("Maria")).thenReturn(List.of(cliente));

//         RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteService.save(cliente));

//         assertEquals("Nome já cadastrado", ex.getMessage());
//         verify(clienteRepository, never()).save(any());
//     }

//     //  CENÁRIO 3 - CPF/CNPJ DUPLICADO
//     @Test
//     @DisplayName("Não deve salvar cliente com CPF/CNPJ duplicado")
//     void deveLancarErroCpfDuplicado() {
//         when(clienteRepository.findByNome("Maria")).thenReturn(Collections.emptyList());
//         when(clienteRepository.findByCpfCnpj("11122233344")).thenReturn(Optional.of(cliente));

//         RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteService.save(cliente));

//         assertEquals("CPF/CNPJ ja cadastrado", ex.getMessage());
//         verify(clienteRepository, never()).save(any());
//     }

//     //  CENÁRIO 4 - FINDALL
//     @Test
//     @DisplayName("Listar todos os clientes")
//     void deveListarClientes() {
//         when(clienteRepository.findAll()).thenReturn(List.of(cliente));

//         List<Cliente> lista = clienteService.findAll();

//         assertEquals(1, lista.size());
//         verify(clienteRepository).findAll();
//     }

//     //  CENÁRIO 5 - FINDBYID
//     @Test
//     @DisplayName("Buscar cliente por ID existente")
//     void deveBuscarPorId() {
//         when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

//         Cliente result = clienteService.findById(1);

//         assertEquals("Maria", result.getNome());
//         verify(clienteRepository).findById(1);
//     }

//     // CENÁRIO 6 - FINDBID INEXISTENTE
//     @Test
//     @DisplayName("Buscar cliente por ID inexistente deve lançar exceção")
//     void deveLancarExcecaoAoBuscarId() {
//         when(clienteRepository.findById(99)).thenReturn(Optional.empty());

//         assertThrows(RuntimeException.class, () -> clienteService.findById(99));
//     }



//     //  CENÁRIO 8 - UPDATE CLIENTE (todos os ifs)
//     @Test
//     @DisplayName("Atualizar cliente com todos os campos cobrindo todos os ifs")
//     void deveAtualizarCliente() {
//         Cliente clienteUpdate = new Cliente();
//         clienteUpdate.setNome("Novo");
//         clienteUpdate.setEmail("novo@email.com");
//         clienteUpdate.setTelefone("11888888888");
//         clienteUpdate.setEndereco("Rua Nova");
//         clienteUpdate.setCpfCnpj("55566677788");

//         when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
//         when(clienteRepository.save(any())).thenReturn(cliente);

//         Cliente atualizado = clienteService.update(1, clienteUpdate);

//         assertNotNull(atualizado);
//         verify(clienteRepository, times(1)).save(any());

//         // cobre casos nulos
//         clienteUpdate.setNome(null);
//         clienteUpdate.setEmail(null);
//         clienteUpdate.setTelefone(null);
//         clienteUpdate.setEndereco(null);
//         clienteUpdate.setCpfCnpj(null);
//         clienteService.update(1, clienteUpdate);

//         verify(clienteRepository, atLeast(2)).save(any());
//     }

//     //  CENÁRIO 9 - FIND BY NOME
//     @Test
//     @DisplayName("Buscar clientes por nome")
//     void deveBuscarPorNome() {
//         when(clienteRepository.getByNome("Maria")).thenReturn(List.of(cliente));

//         List<Cliente> resultado = clienteService.findByNome("Maria");

//         assertFalse(resultado.isEmpty());
//         verify(clienteRepository).getByNome("Maria");
//     }

//     // CENÁRIO 10 - FIND BY CPF/CNPJ
//     @Test
//     @DisplayName("Buscar cliente por CPF/CNPJ")
//     void deveBuscarPorCpfCnpj() {
//         when(clienteRepository.getByCpfCnpj("11122233344")).thenReturn(cliente);

//         Cliente resultado = clienteService.findByCpfCnpj("11122233344");

//         assertEquals("11122233344", resultado.getCpfCnpj());
//         verify(clienteRepository).getByCpfCnpj("11122233344");
//     }
// }
