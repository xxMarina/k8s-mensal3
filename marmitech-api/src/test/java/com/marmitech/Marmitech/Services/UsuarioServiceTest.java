// package com.marmitech.Marmitech.Services;

// import com.marmitech.Marmitech.Entity.Cliente;
// import com.marmitech.Marmitech.Entity.Usuario;
// import com.marmitech.Marmitech.Repository.UsuarioRepository;
// import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.context.ActiveProfiles;

// import java.time.LocalDate;
// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// //JUnit + Mockito

// @ExtendWith(MockitoExtension.class)
// @ActiveProfiles("test")
// public class UsuarioServiceTest {
//     // O Mock cria um objeto faloso
//     // O Spy usa o objeto real , mas voce ainda pode monitorar ou sobrescrever partes dele
//     @Mock
//     private UsuarioRepository usuarioRepository;

//     @InjectMocks
//     private UsuarioService usuarioService;

//     private Usuario usuario;

//     // private Usuario usuarioUpdate;
//     @BeforeEach
//     void setUp() {
//         usuario = new Usuario();
//         usuario.setId(1);
//         usuario.setNome("Gabi");
//         usuario.setEmail("gabi@gabi.com");
//         usuario.setSenha("123456");
//         usuario.setCargo("Caixa");
//         usuario.setData_criacao(LocalDate.now());
//         // usuarioUpdate.setEmail(null); teste de update para ver se ia dar verde( IFs)
//     }

//     //  Testar o método save
//     @Test
//     @DisplayName("Cenário 01 - Testar método save da UsuarioService")
//     /*Se voce trocar Mock por Spy , o teste vai parar de ser Falso e vai tentar  chamar o banco real ,
//     entao elequebra*/
//     void cenario01() {


//         Mockito.when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
//             Usuario u = invocation.getArgument(0);
//             u.setId(1); // simula o ID que o banco geraria
//             return u;
//         });

//         // Aqui você usa o usuario que vem do @BeforeEach
//         var usuarioSalvo = usuarioService.save(usuario);

//         // Verificações
//         assertNotNull(usuarioSalvo);
//         assertEquals("Gabi", usuarioSalvo.getNome());
//         assertEquals("gabi@gabi.com", usuarioSalvo.getEmail());
//         assertEquals("123456", usuarioSalvo.getSenha());
//         assertEquals("Caixa", usuarioSalvo.getCargo());
//         assertEquals(LocalDate.now(), usuarioSalvo.getData_criacao());
//         assertTrue(usuarioSalvo.getId() > 0);

//         // Garante que o repository foi chamado
//         verify(usuarioRepository, times(1)).save(any(Usuario.class));


//     }

//     //  Testar findAll
//     @Test
//     @DisplayName("Cenário 02 - Listar todos os usuários")
//     void cenario02() {
//         List<Usuario> lista = List.of(usuario);

//      Mockito
//              .
//              when(usuarioRepository.findAll()).thenReturn(lista);

//         var usuarios = usuarioService.findAll();

//         assertNotNull(usuarios);
//         assertEquals(1, usuarios.size());
//         assertEquals("Gabi", usuarios.get(0).getNome());
//         assertEquals("gabi@gabi.com", usuarios.get(0).getEmail());
//         assertEquals("123456", usuarios.get(0).getSenha());
//         assertEquals("Caixa", usuarios.get(0).getCargo());
//         assertEquals(LocalDate.now(), usuarios.get(0).getData_criacao());

//         // Verifica se o repositório foi chamado uma vez
//         verify(usuarioRepository, times(1)).findAll();

//     }

//     // - Testar findById
//     @Test
//     @DisplayName("Cenário 03 - Buscar usuário por ID")
//     void cenario03() {
//        Mockito.when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

//         var resultado = usuarioService.findById(1);

//         assertNotNull(resultado);
//         assertEquals("Gabi", resultado.getNome());
//         assertEquals("gabi@gabi.com", resultado.getEmail());
//         assertEquals("123456", resultado.getSenha());
//         assertEquals("Caixa", resultado.getCargo());
//         assertEquals(LocalDate.now(), resultado.getData_criacao());
//         verify(usuarioRepository, times(1)).findById(1);

//     }

//     //  Testar delete
//     @Test
//     @DisplayName("Cenário 04 - Deletar usuário pelo ID")
//     void cenario04() {
//         Mockito.when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

//         usuarioService.delete(1);

//         verify(usuarioRepository, times(1)).findById(1);
//         verify(usuarioRepository, times(1)).delete(usuario);
//     }

//     @Test
//     @DisplayName("Cenario 05 -Up Atualizar pelo nome e id ")
//     void cenario05() {


//         usuario.setId(1);
//         usuario.setNome("Gabi");
//         usuario.setEmail("gabi@gabi.com");
//         usuario.setSenha("123456");
//         usuario.setCargo("Caixa");
//         usuario.setData_criacao(LocalDate.now());
//         when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));


//         //when(usuarioRepository.findByEmail("gabiexemplo,com")),(usuario));
//         when(usuarioRepository.save(any())).thenReturn(usuario);

//         //usuarioService.update(1, usuario);


//         usuario.setNome(null);
//         usuario.setSenha(null);
//         usuario.setCargo(null);
//         usuario.setEmail(null);
//         usuario.setData_criacao(null);
//         usuarioService.update(1, usuario);


//         verify(usuarioRepository, times(1)).findById(1);

//         verify(usuarioRepository, atLeastOnce()).save(any());
//         assertNotNull(usuario);

//     }

//     @Test
//     @DisplayName("cENARIO 06 De login e senha ")
//     void cenario06() {


//         usuario.setNome("Gabi");
//         usuario.setSenha("Admin");
//         usuario.setCargo("Caixa");

//         when(usuarioRepository.findByNomeAndSenha(anyString(), anyString())).thenReturn(Optional.empty());


//         RuntimeException exception = assertThrows(RuntimeException.class, () -> usuarioService.login("", "345"));
//         assertEquals("Usuario ou senha invalidos", exception.getMessage());


//         when(usuarioRepository.findByNomeAndSenha("Gabi", "Admin")).thenReturn(Optional.of(usuario));
//         assertDoesNotThrow(() -> usuarioService.login("Gabi", "Admin"));
//         assertEquals("Gabi", usuario.getNome());
//         assertEquals("Caixa", usuario.getCargo());


//         usuario.setNome("Ana");
//         usuario.setSenha("Admin");
//         usuario.setCargo("Cozinha");
//         when(usuarioRepository.findByNomeAndSenha("Ana", "Admin")).thenReturn(Optional.of(usuario));
//         assertDoesNotThrow(() -> usuarioService.login("Ana", "Admin"));
//         assertEquals("Cozinha", usuario.getCargo());

//         //verifica se chamou corretamente
//        // verify(usuarioRepository, times(1)).findByNomeAndSenha("ga", "345"); // login ou asenha invalids
//         verify(usuarioRepository, times(1)).findByNomeAndSenha("Gabi", "Admin");
//         verify(usuarioRepository, times(1)).findByNomeAndSenha("Ana", "Admin");
// //nao pode colocar null
//         assertNotNull(usuario);


//     }


//     @Test
//     @DisplayName("Cenario 07 de lista de usuarios do Cargos")
//     void cenario07() {
//         when(usuarioRepository.getByCargo(anyString())).thenReturn(List.of(new Usuario()));

//         var resultado = usuarioService.findByCargo("Caixa");

//         assertFalse(resultado.isEmpty());

//     }

//     @Test
//     @DisplayName("Cenario 08 teste de Nome")
//     void cenario08() {
//         List<Usuario>  nome = usuarioRepository.findByNome("Gabi");
//         when(usuarioRepository.findByNome(anyString())).thenReturn(nome);

//         usuario.setNome("Gabi");


//         when(usuarioRepository.findByNome(anyString())).thenReturn(List.of(usuario));
//         var resultado = usuarioService.findByNome("Gabi");

//         //assertTrue(resultado.isEmpty());
//         assertEquals("Gabi", resultado.get(0).getNome());
//     }
// }
