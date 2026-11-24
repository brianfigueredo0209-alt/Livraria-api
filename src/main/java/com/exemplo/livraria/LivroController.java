package com.exemplo.livraria;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
public class LivroController {
    private final LivroService service;

    public LivroController(LivroService service) { this.service = service; }

    @GetMapping public List<Livro> listar() { return service.listar(); }

    @GetMapping("/{id}") public Livro buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Livro cadastrar(@RequestBody Livro livro) { return service.cadastrar(livro); }

    @PutMapping("/{id}")
    public Livro atualizar(@PathVariable Long id, @RequestBody Livro livro) { return service.atualizar(id, livro); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) { service.excluir(id); }

    @ExceptionHandler(LivroNaoEncontradoException.class)
    public ResponseEntity<String> naoEncontrado(LivroNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> invalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
