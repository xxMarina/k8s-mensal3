package com.marmitech.Marmitech.Mapper.ResponseMapper;

import com.marmitech.Marmitech.DTO.ResponseDTO.ClienteResponseDTO;
import com.marmitech.Marmitech.Entity.Cliente;

public class ClienteResponseMapper {

    public static ClienteResponseDTO toDto(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpfCnpj(),
                cliente.getEndereco(),
                cliente.getDataCadastro()
        );
    }
}
