package com.eventosapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Convidado {

    @NotEmpty
    private String nomeConvidado;

    @Id
    @NotEmpty
    private String rg;

    @ManyToOne
    private Evento evento;

    @Column(columnDefinition = "integer default 0")
    private Integer statusConfirmacao = 0;

    public static final int STATUS_PENDENTE = 0;
    public static final int STATUS_CONFIRMADO = 1;
    public static final int STATUS_AUSENTE = 2;

    public String getNomeConvidado() {
        return nomeConvidado;
    }

    public void setNomeConvidado(String nomeConvidado) {
        this.nomeConvidado = nomeConvidado;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Integer getStatusConfirmacao() {
        return statusConfirmacao != null ? statusConfirmacao : STATUS_PENDENTE;
    }

    public void setStatusConfirmacao(Integer statusConfirmacao) {
        this.statusConfirmacao = statusConfirmacao != null ? statusConfirmacao : STATUS_PENDENTE;
    }

    public boolean isPendente() {
        return getStatusConfirmacao() == STATUS_PENDENTE;
    }

    public boolean isConfirmado() {
        return getStatusConfirmacao() == STATUS_CONFIRMADO;
    }

    public boolean isAusente() {
        return getStatusConfirmacao() == STATUS_AUSENTE;
    }

    public String getStatusTexto() {
        switch (getStatusConfirmacao()) {
            case STATUS_CONFIRMADO:
                return "Confirmado";
            case STATUS_AUSENTE:
                return "Ausente";
            default:
                return "Pendente";
        }
    }

    public String getStatusCor() {
        switch (getStatusConfirmacao()) {
            case STATUS_CONFIRMADO:
                return "green";
            case STATUS_AUSENTE:
                return "red";
            default:
                return "orange";
        }
    }
}