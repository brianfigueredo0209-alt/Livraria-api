package com.exemplo.livraria;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {
    @Mock private LivroRepository repository;       // dependência simulada (sem banco real)
    @InjectMocks private LivroService service;      // objeto real, recebe o mock

    private Livro livro() {
        Livro l = new Livro();
        l.setTitulo("Clean Code"); l.setAutor("Robert Martin");
        l.setAno(2008); l.setPreco(99.9);
        return l;
    }

    @Test
    void deveCadastrarLivroValido() {
        Livro l = livro();                              // Arrange
        when(repository.save(l)).thenReturn(l);
        Livro r = service.cadastrar(l);                 // Act
        assertEquals("Clean Code", r.getTitulo());      // Assert
        verify(repository).save(l);                     // interação com o Repository
    }

    @Test
    void deveBuscarLivroPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(livro()));
        assertEquals("Robert Martin", service.buscar(1L).getAutor());
        verify(repository).findById(1L);
    }

    @Test
    void deveLancarErroQuandoLivroNaoExiste() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> service.buscar(9L));
    }

    @Test
    void deveRejeitarTituloVazio() {
        Livro l = livro(); l.setTitulo(" ");
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(l));
        verify(repository, never()).save(any());
    }
}
