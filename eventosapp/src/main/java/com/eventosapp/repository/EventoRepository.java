package com.eventosapp.repository;

import com.eventosapp.models.Evento;
import com.eventosapp.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    Optional<Evento> findByCodigo(long codigo);
    
    List<Evento> findByUsuario(Usuario usuario);
    
    List<Evento> findByUsuarioId(Long usuarioId);
    
    Optional<Evento> findByCodigoAndUsuario(long codigo, Usuario usuario);
    
    Optional<Evento> findByCodigoAndUsuarioId(long codigo, Long usuarioId);
    
    Long countByUsuario(Usuario usuario);
    
    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.convidados WHERE e.usuario.id = :usuarioId")
    List<Evento> findByUsuarioIdWithConvidados(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COUNT(e) > 0 FROM Evento e WHERE e.codigo = :codigo AND e.usuario.id = :usuarioId")
    boolean existsByCodigoAndUsuarioId(@Param("codigo") long codigo, @Param("usuarioId") Long usuarioId);
}