package com.exemplo.livraria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LivroService {
    private static final Logger log = LoggerFactory.getLogger(LivroService.class);
    private final LivroRepository repository;

    public LivroService(LivroRepository repository) { this.repository = repository; }

    public List<Livro> listar() { return repository.findAll(); }

    public Livro buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

    public Livro cadastrar(Livro livro) {
        validar(livro);
        log.info("Cadastrando livro: {}", livro.getTitulo());
        return repository.save(livro);
    }

    public Livro atualizar(Long id, Livro dados) {
        Livro existente = buscar(id);
        validar(dados);
        existente.setTitulo(dados.getTitulo());
        existente.setAutor(dados.getAutor());
        existente.setAno(dados.getAno());
        existente.setPreco(dados.getPreco());
        return repository.save(existente);
    }

    public void excluir(Long id) {
        buscar(id);
        repository.deleteById(id);
    }

    private void validar(Livro l) {
        if (l.getTitulo() == null || l.getTitulo().isBlank())
            throw new IllegalArgumentException("Título é obrigatório");
        if (l.getPreco() == null || l.getPreco() < 0)
            throw new IllegalArgumentException("Preço inválido");
    }
}
