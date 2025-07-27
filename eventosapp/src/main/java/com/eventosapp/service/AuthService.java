package com.eventosapp.service;

import com.eventosapp.models.Usuario;
import com.eventosapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public Usuario cadastrarUsuario(Usuario usuario) throws Exception {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("Email já está em uso");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().length() < 2) {
            throw new Exception("Nome deve ter pelo menos 2 caracteres");
        }
        
        if (usuario.getEmail() == null || !usuario.getEmail().contains("@")) {
            throw new Exception("Email inválido");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new Exception("Senha deve ter pelo menos 6 caracteres");
        }
        
        usuario.setDataCriacao(LocalDateTime.now());
        
        return usuarioRepository.save(usuario);
    }
    
    public Usuario login(String email, String senha) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email é obrigatório");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new Exception("Senha é obrigatória");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndSenha(email.trim(), senha);
        
        if (!usuarioOpt.isPresent()) {
            throw new Exception("Email ou senha incorretos");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        return usuario;
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    public Usuario atualizarUsuario(Usuario usuario) throws Exception {
        if (usuario.getId() == null) {
            throw new Exception("ID do usuário é obrigatório para atualização");
        }
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());
        if (!usuarioExistente.isPresent()) {
            throw new Exception("Usuário não encontrado");
        }
        
        Usuario usuarioAtual = usuarioExistente.get();
        if (!usuarioAtual.getEmail().equals(usuario.getEmail()) && 
            usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("Email já está em uso por outro usuário");
        }
        
        return usuarioRepository.save(usuario);
    }
    
    public Long contarUsuarios() {
        return usuarioRepository.count();
    }
}