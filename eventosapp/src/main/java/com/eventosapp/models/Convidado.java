package com.eventosapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Convidado {

    @Id
    @NotBlank(message = "RG é obrigatório")
    private String rg;

    @NotBlank(message = "Nome do convidado é obrigatório")
    private String nomeConvidado;

    @ManyToOne
    private Evento evento;

    // Getters e Setters
    public String getRg() {
        return rg;
    }
    
    public void setRg(String rg) {
        this.rg = rg;
    }
    
    public String getNomeConvidado() {
        return nomeConvidado;
    }
    
    public void setNomeConvidado(String nomeConvidado) {
        this.nomeConvidado = nomeConvidado;
    }
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}