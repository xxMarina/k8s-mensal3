package com.marmitech.Marmitech.Services;

import com.marmitech.Marmitech.DTO.RequestDTO.ProdutoSaveDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.ProdutoListaDTO;
import com.marmitech.Marmitech.Entity.Categoria;
import com.marmitech.Marmitech.Entity.Produto;
import com.marmitech.Marmitech.Mapper.RequestMapper.SaveProdutoMapping;
import com.marmitech.Marmitech.Mapper.ResponseMapper.ProdutoListaMapper;
import com.marmitech.Marmitech.Repository.CategoriaRepository;
import com.marmitech.Marmitech.Repository.PedidoItemRepository;
import com.marmitech.Marmitech.Repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final CategoriaRepository categoriaRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ProdutoRepository produtoRepository;

    public ProdutoListaDTO save(ProdutoSaveDTO produtoDto) {
        Categoria categoria = categoriaRepository.findById( produtoDto.categoriaId() )
                .orElseThrow( () -> new RuntimeException( "Categoria com ID " + produtoDto.categoriaId() + " não encontrada" ) );

        Produto novoProduto = SaveProdutoMapping.toEntity( produtoDto, categoria );
        novoProduto.setDataCadastro( LocalDate.now().toString() );

        Produto salvoProduto = produtoRepository.save( novoProduto );

        return ProdutoListaMapper.toDto( salvoProduto );
    }

    public List<ProdutoListaDTO> findAll() {
        return produtoRepository.findAll()
                .stream()
                .map( ProdutoListaMapper::toDto )
                .toList();
    }

    public Produto findById(Integer id) {
        return produtoRepository.findById( id ).orElseThrow( RuntimeException::new );
    }

    public void delete(Integer id) {
        var delete = findById( id );

        if (pedidoItemRepository.existsByProdutoId( id )) {
            throw new IllegalStateException( "Não é possível excluir o produto, pois ele já está associado a um ou mais pedidos." );
        }

        produtoRepository.delete( delete );
    }

    public Produto update(Integer id, Produto produto) {
        Produto produtoUpdate = findById( id );
        produtoUpdate.setDataCadastro( LocalDate.now().toString() );

        if (produto.getNome() != null && !produto.getNome().isBlank()) {
            produtoUpdate.setNome( produto.getNome() );
        }
        if (produto.getDescricao() != null && !produto.getDescricao().isBlank()) {
            produtoUpdate.setDescricao( produto.getDescricao() );
        }
        if (produto.getPrecoUnitario() != null && produto.getPrecoUnitario() >= 0) {
            produtoUpdate.setPrecoUnitario( produto.getPrecoUnitario() );
        }
        if (produto.getCategoria() != null) {
            Categoria cat = categoriaRepository.findById( produto.getCategoria().getId() )
                    .orElseThrow( () -> new RuntimeException( "Categoria com ID " + produto.getCategoria().getId() + " não encontrada" ) );
            produtoUpdate.setCategoria( cat );
        }
        if (produto.getEstoque() >= 0) {
            produtoUpdate.setEstoque( produto.getEstoque() );
        }
        if (produto.getSku() != null && !produto.getSku().isBlank()) {
            produtoUpdate.setSku( produto.getSku() );
        }
    
        return produtoRepository.save( produtoUpdate );
    }
}
