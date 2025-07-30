package com.eventosapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuario_email", columnList = "email")
})
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty(message = "Nome é obrigatório")
    @Size(min = 2, message = "Nome deve ter pelo menos 2 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;
    
    @NotEmpty(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @NotEmpty(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @Column(nullable = false, length = 255)
    private String senha;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Evento> eventos = new ArrayList<>();
    
    public Usuario() {
        this.dataCriacao = LocalDateTime.now();
    }
    
    public Usuario(String nome, String email, String senha) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }
    
    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }
    
    public List<Evento> getEventos() {
        return eventos;
    }
    
    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }
    
    public void adicionarEvento(Evento evento) {
        eventos.add(evento);
        evento.setUsuario(this);
    }
    
    public void removerEvento(Evento evento) {
        eventos.remove(evento);
        evento.setUsuario(null);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
