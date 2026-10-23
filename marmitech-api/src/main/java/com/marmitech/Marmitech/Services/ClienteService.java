package com.marmitech.Marmitech.Services;

import com.marmitech.Marmitech.DTO.RequestDTO.ClienteRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.ClienteResponseDTO;
import com.marmitech.Marmitech.Entity.Cliente;
import com.marmitech.Marmitech.Mapper.RequestMapper.ClienteRequestMapper;
import com.marmitech.Marmitech.Mapper.ResponseMapper.ClienteResponseMapper;
import com.marmitech.Marmitech.Repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponseDTO save(ClienteRequestDTO dto) {
        Cliente cliente = ClienteRequestMapper.toEntity(dto);
        cliente.setDataCadastro( LocalDate.now().toString() );

        if (cliente.getNome() != null && !cliente.getNome().isBlank()) {
            List<Cliente> clientesComMesmoNome = clienteRepository.findByNome( cliente.getNome() );
            if (!clientesComMesmoNome.isEmpty()) {
                throw new RuntimeException( "Nome já cadastrado" );
            }
        }

        if (cliente.getCpfCnpj() != null && !cliente.getCpfCnpj().isBlank()) {
            Optional<Cliente> clienteBD = clienteRepository.findByCpfCnpj( cliente.getCpfCnpj() );
            clienteBD.ifPresent( clienteModel -> {
                throw new RuntimeException( "CPF/CNPJ ja cadastrado" );
            } );
        }

        Cliente saved = clienteRepository.save( cliente );
        return ClienteResponseMapper.toDto( saved );
    }

    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map( ClienteResponseMapper::toDto )
                .toList();
    }

    public ClienteResponseDTO findById(Integer id) {
        Cliente cliente = clienteRepository.findById( id ).orElseThrow( RuntimeException::new );
        return ClienteResponseMapper.toDto( cliente );
    }

    public void delete(Integer id) {
        var delete = clienteRepository.findById( id ).orElseThrow( RuntimeException::new );
        clienteRepository.delete( delete );
    }

    public ClienteResponseDTO update(Integer id, ClienteRequestDTO dto) {
        Cliente clienteUpdate = clienteRepository.findById( id ).orElseThrow( RuntimeException::new );
        clienteUpdate.setDataCadastro( LocalDateTime.now().toString() );

        if (dto.nome() != null && !dto.nome().isBlank()) {
            clienteUpdate.setNome( dto.nome() );
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            clienteUpdate.setEmail( dto.email() );
        }
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            clienteUpdate.setTelefone( dto.telefone() );
        }
        if (dto.cpfCnpj() != null && !dto.cpfCnpj().isBlank()) {
            clienteUpdate.setCpfCnpj( dto.cpfCnpj() );
        }
        if (dto.endereco() != null && !dto.endereco().isBlank()) {
            clienteUpdate.setEndereco( dto.endereco() );
        }

        Cliente saved = clienteRepository.save( clienteUpdate );
        return ClienteResponseMapper.toDto( saved );
    }

    public List<ClienteResponseDTO> findByNome(String nome) {
        return clienteRepository.getByNome( nome )
                .stream()
                .map( ClienteResponseMapper::toDto )
                .toList();
    }

    public ClienteResponseDTO findByCpfCnpj(String cpf_cnpj) {
        Cliente cliente = clienteRepository.getByCpfCnpj( cpf_cnpj );
        return ClienteResponseMapper.toDto( cliente );
    }
}
