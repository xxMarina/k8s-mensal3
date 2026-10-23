package com.marmitech.Marmitech.Services;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.marmitech.Marmitech.DTO.RequestDTO.HistoricoSaveDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.HistoricoResponseDTO;
import com.marmitech.Marmitech.Mapper.ResponseMapper.HistoricoResponseMapper;
import com.marmitech.Marmitech.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marmitech.Marmitech.Entity.HistoricoCompra;
import com.marmitech.Marmitech.Repository.HistoricoCompraRepository;

import jakarta.transaction.Transactional;

@Service
public class HistoricoCompraService {

    private Boolean isValidoString(String novaString) {
        return novaString != null && !novaString.isBlank();
    }

    private <T> T validador(T novo, T antigo, Predicate<T> validar) {
        return validar.test( novo ) ? novo : antigo;
    }

    @Autowired
    private HistoricoCompraRepository historicoCompraRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional
    public HistoricoCompra save(HistoricoSaveDTO novoHistoricoDTO) {
        var pedido = pedidoRepository.findById( novoHistoricoDTO.pedidoId() )
                .orElseThrow( () -> new RuntimeException( "Pedido com ID " + novoHistoricoDTO.pedidoId() + " não encontrado" ) );

        HistoricoCompra novoHistoricoCompra = new HistoricoCompra();
        novoHistoricoCompra.setPedido( pedido );
        novoHistoricoCompra.setDataEvento( novoHistoricoDTO.dataEvento() );
        novoHistoricoCompra.setTipoEvento( novoHistoricoDTO.tipoEvento() );
        novoHistoricoCompra.setDescricao( novoHistoricoDTO.descricao() );

        return historicoCompraRepository.save( novoHistoricoCompra );
    }

    @Transactional
    public HistoricoCompra update(HistoricoCompra novoHistoricoCompra, int historicoCompraId) {
        HistoricoCompra antigoHistoricoCompra = historicoCompraRepository.findById( historicoCompraId )
                .orElseThrow( () -> new RuntimeException( "Historico com ID " + historicoCompraId + " não encontrado para atualização" ) );

        antigoHistoricoCompra.setTipoEvento( validador( novoHistoricoCompra.getTipoEvento(), antigoHistoricoCompra.getTipoEvento(), this::isValidoString ) );
        antigoHistoricoCompra.setDataEvento( validador( novoHistoricoCompra.getDataEvento(), antigoHistoricoCompra.getDataEvento(), this::isValidoString ) );
        antigoHistoricoCompra.setDescricao( validador( novoHistoricoCompra.getDescricao(), antigoHistoricoCompra.getDescricao(), this::isValidoString ) );

        return historicoCompraRepository.save( antigoHistoricoCompra );
    }

    @Transactional
    public String delete(int historicoCompraId) {
        if (historicoCompraId <= 0) {
            throw new IllegalArgumentException( "ID DO HISTORICO INVALIDO" );
        }
        if (!historicoCompraRepository.existsById( historicoCompraId )) {
            throw new RuntimeException( "Historico com ID " + historicoCompraId + " não encontrado" );
        }
        historicoCompraRepository.deleteById( historicoCompraId );
        return "Historico deletado com sucesso!";
    }

    public HistoricoResponseDTO findById(int historicoCompraId) {
        if (historicoCompraId <= 0) {
            throw new IllegalArgumentException( "ID DO HISTORICO INVALIDO" );
        }
        HistoricoCompra historico = historicoCompraRepository.findById( historicoCompraId )
                .orElseThrow( () -> new RuntimeException( "Historico com ID " + historicoCompraId + " não encontrado" ) );

        return HistoricoResponseMapper.toDto( historico );
    }

    public List<HistoricoResponseDTO> findAll() {
        return historicoCompraRepository.findAll()
                .stream()
                .map( HistoricoResponseMapper::toDto )
                .collect( Collectors.toList() );
    }

    public List<HistoricoResponseDTO> findByDataEvento(String dataEvento) {
        return historicoCompraRepository.findByDataEvento( dataEvento )
                .stream()
                .map( HistoricoResponseMapper::toDto )
                .collect( Collectors.toList() );
    }
}