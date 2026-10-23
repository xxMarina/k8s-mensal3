package com.marmitech.Marmitech.Mapper.RequestMapper;

import com.marmitech.Marmitech.DTO.RequestDTO.ClienteRequestDTO;
import com.marmitech.Marmitech.Entity.Cliente;

public class ClienteRequestMapper {

    public static Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setCpfCnpj(dto.cpfCnpj());
        cliente.setEndereco(dto.endereco());
        return cliente;
    }

    public static ClienteRequestDTO toDto(Cliente cliente) {
        return new ClienteRequestDTO(
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpfCnpj(),
                cliente.getEndereco()
        );
    }
}
