package com.exemplo.livraria;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LivroControllerTest {
    private final LivroService service = mock(LivroService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new LivroController(service)).build();

    @Test
    void deveListarLivros() throws Exception {
        when(service.listar()).thenReturn(List.of());
        mvc.perform(get("/livros")).andExpect(status().isOk());
    }

    @Test
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        when(service.buscar(9L)).thenThrow(new LivroNaoEncontradoException(9L));
        mvc.perform(get("/livros/9")).andExpect(status().isNotFound());
    }
}
