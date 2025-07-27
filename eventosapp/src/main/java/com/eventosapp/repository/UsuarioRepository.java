package com.eventosapp.repository;

import com.eventosapp.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<Usuario> findByEmailAndSenha(String email, String senha);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.eventos WHERE u.email = :email")
    Optional<Usuario> findByEmailWithEventos(@Param("email") String email);
    
    @Query("SELECT COUNT(u) FROM Usuario u")
    Long countTotalUsuarios();
}