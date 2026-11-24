package com.exemplo.livraria;
import jakarta.persistence.*;

@Entity
public class Livro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String autor;
    private Integer ano;
    private Double preco;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String t) { this.titulo = t; }
    public String getAutor() { return autor; }
    public void setAutor(String a) { this.autor = a; }
    public Integer getAno() { return ano; }
    public void setAno(Integer a) { this.ano = a; }
    public Double getPreco() { return preco; }
    public void setPreco(Double p) { this.preco = p; }
}
