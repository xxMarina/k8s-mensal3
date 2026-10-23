package com.marmitech.Marmitech.Controller;

import com.marmitech.Marmitech.Entity.Cliente;
import com.marmitech.Marmitech.Repository.ClienteRepository;
import com.marmitech.Marmitech.Security.JwUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private JwUtil jwUtil;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Gera um token real de ADMIN e anexa em toda requisição deste teste,
        // já que aqui a chamada HTTP passa pela cadeia de filtros de verdade.
        String token = jwUtil.generateToken("teste@marmitech.com", "ADMIN");

        restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        });

        clienteRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Maria");
        cliente.setEmail("maria@exemplo.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua das Flores, 123");
        cliente.setCpfCnpj("111222333000");
        cliente.setDataCadastro(LocalDate.of(2025, 1, 1).toString());
        clienteRepository.save(cliente);
    }

    // CENÁRIO 1 - POST
    @Test
    @DisplayName("POST /save - Criar cliente com sucesso")
    void deveCriarCliente() {
        Cliente novo = new Cliente();
        novo.setNome("Ana");
        novo.setEmail("ana@exemplo.com");
        novo.setTelefone("11888888888");
        novo.setEndereco("Av. Brasil, 456");
        novo.setCpfCnpj("111222333448");
        novo.setDataCadastro(LocalDate.now().toString());

        ResponseEntity<Cliente> response = restTemplate.postForEntity("/api/cliente/save", novo, Cliente.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ana", response.getBody().getNome());
    }

    // CENÁRIO 2 - GET /findAll
    @Test
    @DisplayName("GET /findAll - Deve listar todos os clientes")
    void deveListarClientes() {
        ResponseEntity<Cliente[]> response = restTemplate.getForEntity("/api/cliente/findAll", Cliente[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().length > 0);
    }

    // CENÁRIO 3 - GET /findById/{id}
    @Test
    @DisplayName("GET /findById - Buscar cliente por ID existente")
    void deveBuscarPorId() {
        ResponseEntity<Cliente> response = restTemplate.getForEntity("/api/cliente/findById/" + cliente.getId(),
                Cliente.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria", response.getBody().getNome());
    }

    // CENÁRIO 4 - PUT /update/{id}
    @Test
    @DisplayName("PUT /update - Atualizar cliente existente")
    void deveAtualizarCliente() {
        cliente.setEndereco("Rua Atualizada, 999");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Cliente> request = new HttpEntity<>(cliente, headers);

        ResponseEntity<Cliente> response = restTemplate.exchange(
                "/api/cliente/update/" + cliente.getId(),
                HttpMethod.PUT,
                request,
                Cliente.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Rua Atualizada, 999", response.getBody().getEndereco());
    }

    // CENÁRIO 5 - DELETE /delete/{id}
    @Test
    @DisplayName("DELETE /delete/{id} - Deletar cliente existente com sucesso")
    void deveDeletarCliente() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/cliente/delete/" + cliente.getId(),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        boolean existe = clienteRepository.findById(cliente.getId()).isPresent();
        assertFalse(existe, "O cliente ainda existe no banco após o delete!");
    }

    // CENÁRIO 6 - GET /findByNome/{nome}
    @Test
    @DisplayName("GET /findByNome - Buscar cliente pelo nome")
    void deveBuscarPorNome() {
        ResponseEntity<Cliente[]> response = restTemplate.getForEntity("/api/cliente/findByNome/Maria",
                Cliente[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    // CENÁRIO 7 - GET /findByCpfCnpj/{cpf}
    @Test
    @DisplayName("GET /findByCpfCnpj - Buscar cliente pelo CPF/CNPJ")
    void deveBuscarPorCpfCnpj() {
        ResponseEntity<Cliente> response = restTemplate
                .getForEntity("/api/cliente/findByCpfCnpj/" + cliente.getCpfCnpj(), Cliente.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cliente.getCpfCnpj(), response.getBody().getCpfCnpj());
    }
}