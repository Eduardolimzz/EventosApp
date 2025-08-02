package com.eventosapp.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventosapp.models.Convidado;
import com.eventosapp.models.Evento;
import com.eventosapp.repository.ConvidadoRepository;
import com.eventosapp.repository.EventoRepository;

@Controller
public class PublicController {

    @Autowired
    private EventoRepository er;

    @Autowired
    private ConvidadoRepository cr;

    @RequestMapping(value="/confirmar/{codigo}", method=RequestMethod.GET)
    public ModelAndView paginaConfirmacao(@PathVariable("codigo") long codigo) {
        Optional<Evento> eventoOpt = er.findByCodigo(codigo);
        
        if (!eventoOpt.isPresent()) {
            ModelAndView mv = new ModelAndView("erro404");
            mv.addObject("mensagem", "Evento não encontrado.");
            return mv;
        }
        
        Evento evento = eventoOpt.get();
        ModelAndView mv = new ModelAndView("publico/confirmarPresenca");
        mv.addObject("evento", evento);
        
        return mv;
    }

    @RequestMapping(value="/confirmar/{codigo}", method=RequestMethod.POST)
    public String confirmarPresenca(@PathVariable("codigo") long codigo,
                                  @RequestParam("nomeConvidado") String nomeConvidado,
                                  @RequestParam("email") String email,
                                  RedirectAttributes attributes) {
        
        Optional<Evento> eventoOpt = er.findByCodigo(codigo);
        
        if (!eventoOpt.isPresent()) {
            attributes.addFlashAttribute("mensagemErro", "Evento não encontrado.");
            return "redirect:/confirmar/" + codigo;
        }
        
        Evento evento = eventoOpt.get();
        
        if (nomeConvidado == null || nomeConvidado.trim().isEmpty()) {
            attributes.addFlashAttribute("mensagemErro", "Nome é obrigatório.");
            return "redirect:/confirmar/" + codigo;
        }
        
        if (email == null || email.trim().isEmpty()) {
            attributes.addFlashAttribute("mensagemErro", "Email é obrigatório.");
            return "redirect:/confirmar/" + codigo;
        }
        
        Optional<Convidado> convidadoExistente = cr.findById(email.toLowerCase().trim());
        
        if (convidadoExistente.isPresent()) {
            if (convidadoExistente.get().getEvento().getCodigo() == codigo) {
                attributes.addFlashAttribute("mensagemErro", "Este email já foi usado para confirmar presença neste evento.");
                attributes.addFlashAttribute("jaConfirmado", true);
                return "redirect:/confirmar/" + codigo;
            }
        }
        
        Convidado novoConvidado = new Convidado();
        novoConvidado.setRg(email.toLowerCase().trim()); 
        novoConvidado.setNomeConvidado(nomeConvidado.trim());
        novoConvidado.setEvento(evento);
        novoConvidado.setStatusConfirmacao(Convidado.STATUS_CONFIRMADO); 
        
        try {
            cr.save(novoConvidado);
            attributes.addFlashAttribute("mensagemSucesso", "Presença confirmada com sucesso! Obrigado por confirmar.");
            attributes.addFlashAttribute("jaConfirmado", true);
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao confirmar presença. Tente novamente.");
        }
        
        return "redirect:/confirmar/" + codigo;
    }
}