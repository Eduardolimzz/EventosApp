package com.eventosapp.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventosapp.models.Convidado;
import com.eventosapp.models.Evento;
import com.eventosapp.repository.ConvidadoRepository;
import com.eventosapp.repository.EventoRepository;

import jakarta.validation.Valid;

@Controller
public class EventoController {

    @Autowired
    private EventoRepository er;

    @Autowired
    private ConvidadoRepository cr;

    @RequestMapping(value="/cadastrarEvento", method=RequestMethod.GET)
    public String form() {
        return "evento/formEvento";
    }

    @RequestMapping(value="/cadastrarEvento", method=RequestMethod.POST)
    public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
        if(result.hasErrors()) {
            StringBuilder mensagemErro = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                mensagemErro.append(error.getDefaultMessage()).append(" ");
            });
            
            attributes.addFlashAttribute("mensagemErro", mensagemErro.toString());
            return "redirect:/cadastrarEvento";
        }
        
        er.save(evento);
        attributes.addFlashAttribute("mensagemSucesso", "Evento cadastrado com sucesso!");
        return "redirect:/cadastrarEvento";
    }

    @RequestMapping("/eventos")
    public ModelAndView listaEventos() {
        ModelAndView mv = new ModelAndView("listaEventos");
        Iterable<Evento> eventos = er.findAll(); 
        mv.addObject("eventos", eventos);
        return mv;
    }

    @RequestMapping(value="/detalhesEvento/{codigo}", method=RequestMethod.GET)
    public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo){
        Evento evento = er.findByCodigo(codigo); 
        ModelAndView mv = new ModelAndView("evento/detalhesEvento");
        mv.addObject("evento", evento);
        Iterable<Convidado> convidados = cr.findByEvento(evento);
        mv.addObject("convidados", convidados);
        return mv;
    }
    
    @RequestMapping(value = "/deletarEvento/{codigo}", method = RequestMethod.GET)
    public String deletarEvento(@PathVariable("codigo") long codigo, RedirectAttributes attributes) {
        Evento evento = er.findByCodigo(codigo);

        if (evento != null) {
            Iterable<Convidado> convidados = cr.findByEvento(evento);
            cr.deleteAll(convidados);

            er.delete(evento);

            attributes.addFlashAttribute("mensagemSucesso", "Evento e convidados deletados com sucesso!");
        } else {
            attributes.addFlashAttribute("mensagemErro", "Evento não encontrado para exclusão.");
        }

        return "redirect:/eventos";
    }



    @RequestMapping(value="/detalhesEvento/{codigo}", method=RequestMethod.POST)
    public String detalhesEvento(@PathVariable("codigo") long codigo, 
                               @Valid Convidado convidado, 
                               BindingResult result, 
                               RedirectAttributes attributes){
        if(result.hasErrors()) {
            StringBuilder mensagemErro = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                mensagemErro.append(error.getDefaultMessage()).append(" ");
            });
            
            attributes.addFlashAttribute("mensagemErro", mensagemErro.toString());
            return "redirect:/detalhesEvento/{codigo}";
        }
        
        Evento evento = er.findByCodigo(codigo);
        convidado.setEvento(evento);
        cr.save(convidado);
        
        attributes.addFlashAttribute("mensagemSucesso", "Convidado adicionado com sucesso!");
        return "redirect:/detalhesEvento/{codigo}";
    }
    
    @RequestMapping(value = "/deletarConvidado/{id}", method = RequestMethod.GET)
    public String deletarConvidado(@PathVariable("id") long id, RedirectAttributes attributes) {
        Optional<Convidado> convidadoOptional = cr.findById(id);

        if (convidadoOptional.isPresent()) {
            Convidado convidado = convidadoOptional.get();
            Evento evento = convidado.getEvento();

            cr.delete(convidado);
            attributes.addFlashAttribute("mensagemSucesso", "Convidado deletado com sucesso!");

            return "redirect:/detalhesEvento/" + evento.getCodigo(); 
        } else {
            attributes.addFlashAttribute("mensagemErro", "Convidado não encontrado para exclusão.");
            return "redirect:/eventos";
        }
    }
    
}