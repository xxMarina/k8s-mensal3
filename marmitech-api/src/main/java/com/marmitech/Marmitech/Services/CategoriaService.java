package com.marmitech.Marmitech.Services;
import com.marmitech.Marmitech.DTO.RequestDTO.CategoriaRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.CategoriaResponseDTO;
import com.marmitech.Marmitech.Entity.Categoria;
import com.marmitech.Marmitech.Mapper.RequestMapper.CategoriaRequestMapper;
import com.marmitech.Marmitech.Mapper.ResponseMapper.CategoriaResponseMapper;
import com.marmitech.Marmitech.Repository.CategoriaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll()
                .stream()
                .map( CategoriaResponseMapper::toDto )
                .toList();
    }

    public CategoriaResponseDTO save(@Valid CategoriaRequestDTO dto) {
        Categoria categoria = CategoriaRequestMapper.toEntity( dto );
        Categoria saved = categoriaRepository.save( categoria );
        return CategoriaResponseMapper.toDto( saved );
    }

    public CategoriaResponseDTO findById(Integer id) {
        Categoria categoria = categoriaRepository.findById( id ).orElseThrow( RuntimeException::new );
        return CategoriaResponseMapper.toDto( categoria );
    }

    public void delete(Integer id) {
        var delete = findById( id );
        categoriaRepository.deleteById( delete.id() );
    }

    public CategoriaResponseDTO update(Integer id, CategoriaRequestDTO dto) {
        Categoria categoriaParaAtualizar = categoriaRepository.findById( id ).orElseThrow( RuntimeException::new );

        if (dto.nome() != null && !dto.nome().isBlank()) {
            categoriaParaAtualizar.setNome( dto.nome() );
        }

        if (dto.descricao() != null && !dto.descricao().isBlank()) {
            categoriaParaAtualizar.setDescricao( dto.descricao() );
        }

        Categoria saved = categoriaRepository.save( categoriaParaAtualizar );
        return CategoriaResponseMapper.toDto( saved );
    }
}
