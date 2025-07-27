package com.eventosapp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

        Iterable<Convidado> convidadosIterable = cr.findByEvento(evento);

        List<Convidado> convidados = new ArrayList<>();
        convidadosIterable.forEach(convidados::add); 

        mv.addObject("convidados", convidados); 
        mv.addObject("totalConvidados", convidados.size());

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
    
    @RequestMapping(value = "/deletarConvidado/{rg}", method = RequestMethod.GET)
    public String deletarConvidado(@PathVariable("rg") String rg, RedirectAttributes attributes) {
       
        Optional<Convidado> convidadoOptional = cr.findById(rg);

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
    @RequestMapping(value = "/editarConvidado", method = RequestMethod.POST)
    @ResponseBody
    public String editarConvidado(@RequestBody Map<String, String> dadosConvidado) {
        try {
            String rgOriginal = dadosConvidado.get("rg");
            String novoNome = dadosConvidado.get("nomeConvidado");
            String novoRg = dadosConvidado.get("novoRg");
            
            System.out.println("Dados recebidos - RG Original: " + rgOriginal + ", Nome: " + novoNome + ", Novo RG: " + novoRg);
            
            if (novoRg == null || novoRg.isEmpty()) {
                novoRg = rgOriginal;
            }
            
            Optional<Convidado> convidadoOptional = cr.findById(rgOriginal);
            
            if (convidadoOptional.isPresent()) {
                Convidado convidadoExistente = convidadoOptional.get();
                
                if (!rgOriginal.equals(novoRg)) {
                    Optional<Convidado> convidadoComNovoRg = cr.findById(novoRg);
                    if (convidadoComNovoRg.isPresent()) {
                        System.out.println("RG já existe: " + novoRg);
                        return "rg_exists";
                    }
                    
                    Convidado novoConvidado = new Convidado();
                    novoConvidado.setRg(novoRg);
                    novoConvidado.setNomeConvidado(novoNome);
                    novoConvidado.setEvento(convidadoExistente.getEvento());
                    
                    cr.save(novoConvidado);
                    cr.delete(convidadoExistente);
                    System.out.println("Convidado atualizado com novo RG");
                } else {
                    convidadoExistente.setNomeConvidado(novoNome);
                    cr.save(convidadoExistente);
                    System.out.println("Nome do convidado atualizado");
                }
                
                return "success";
            } else {
                System.out.println("Convidado não encontrado: " + rgOriginal);
                return "not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao editar convidado: " + e.getMessage());
            return "error: " + e.getMessage();
        }
    }
    @RequestMapping(value = "/editarEvento", method = RequestMethod.POST)
    @ResponseBody
    public String editarEvento(@RequestBody Map<String, String> dadosEvento) {
        try {
            long codigo = Long.parseLong(dadosEvento.get("codigo"));
            String novoNome = dadosEvento.get("nome");
            String novoLocal = dadosEvento.get("local");
            String novaData = dadosEvento.get("data");
            String novoHorario = dadosEvento.get("horario");

            Optional<Evento> eventoOptional = er.findById(codigo);

            if (eventoOptional.isPresent()) {
                Evento eventoExistente = eventoOptional.get();
                eventoExistente.setNome(novoNome);
                eventoExistente.setLocal(novoLocal);
                eventoExistente.setData(novaData);
                eventoExistente.setHorario(novoHorario);
                er.save(eventoExistente);
                return "success";
            } else {
                return "not_found";
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato de número ao editar evento: " + e.getMessage());
            return "error: Invalid Code";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao editar evento: " + e.getMessage());
            return "error: " + e.getMessage();
        }
    }
}