package com.exemplo.livraria;

public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(Long id) {
        super("Livro não encontrado: " + id);
    }
}
