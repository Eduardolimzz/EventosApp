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
import com.eventosapp.models.Usuario;
import com.eventosapp.repository.ConvidadoRepository;
import com.eventosapp.repository.EventoRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class EventoController {

    @Autowired
    private EventoRepository er;

    @Autowired
    private ConvidadoRepository cr;

    private Usuario verificarUsuarioLogado(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    @RequestMapping(value="/cadastrarEvento", method=RequestMethod.GET)
    public String form(HttpSession session, RedirectAttributes attributes) {
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para acessar esta página.");
            return "redirect:/login";
        }
        return "evento/formEvento";
    }

    @RequestMapping(value="/cadastrarEvento", method=RequestMethod.POST)
    public String form(@Valid Evento evento, BindingResult result, 
                      RedirectAttributes attributes, HttpSession session) {
        
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para cadastrar eventos.");
            return "redirect:/login";
        }
        
        if(result.hasErrors()) {
            StringBuilder mensagemErro = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                mensagemErro.append(error.getDefaultMessage()).append(" ");
            });
            
            attributes.addFlashAttribute("mensagemErro", mensagemErro.toString());
            return "redirect:/cadastrarEvento";
        }
        
        evento.setUsuario(usuarioLogado);
        er.save(evento);
        attributes.addFlashAttribute("mensagemSucesso", "Evento cadastrado com sucesso!");
        return "redirect:/eventos";
    }

    @RequestMapping("/eventos")
    public ModelAndView listaEventos(HttpSession session, RedirectAttributes attributes) {
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para ver seus eventos.");
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mv = new ModelAndView("listaEventos");
        List<Evento> eventos = er.findByUsuario(usuarioLogado);
        mv.addObject("eventos", eventos);
        mv.addObject("usuarioNome", usuarioLogado.getNome());
        return mv;
    }

    @RequestMapping(value="/detalhesEvento/{codigo}", method=RequestMethod.GET)
    public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo, 
                                     HttpSession session, RedirectAttributes attributes){
        
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para acessar esta página.");
            return new ModelAndView("redirect:/login");
        }
        
        Optional<Evento> eventoOpt = er.findByCodigoAndUsuario(codigo, usuarioLogado);
        if (!eventoOpt.isPresent()) {
            attributes.addFlashAttribute("mensagemErro", "Evento não encontrado ou você não tem permissão para acessá-lo.");
            return new ModelAndView("redirect:/eventos");
        }
        
        Evento evento = eventoOpt.get();
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
    public String deletarEvento(@PathVariable("codigo") long codigo, 
                               RedirectAttributes attributes, HttpSession session) {
        
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para deletar eventos.");
            return "redirect:/login";
        }
        
        Optional<Evento> eventoOpt = er.findByCodigoAndUsuario(codigo, usuarioLogado);
        if (!eventoOpt.isPresent()) {
            attributes.addFlashAttribute("mensagemErro", "Evento não encontrado ou você não tem permissão para deletá-lo.");
            return "redirect:/eventos";
        }
        
        Evento evento = eventoOpt.get();
        
        Iterable<Convidado> convidados = cr.findByEvento(evento);
        cr.deleteAll(convidados);

        er.delete(evento);

        attributes.addFlashAttribute("mensagemSucesso", "Evento e convidados deletados com sucesso!");
        return "redirect:/eventos";
    }

    @RequestMapping(value="/detalhesEvento/{codigo}", method=RequestMethod.POST)
    public String detalhesEvento(@PathVariable("codigo") long codigo, 
                               @Valid Convidado convidado, 
                               BindingResult result, 
                               RedirectAttributes attributes,
                               HttpSession session){
        
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para adicionar convidados.");
            return "redirect:/login";
        }
        
        if(result.hasErrors()) {
            StringBuilder mensagemErro = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                mensagemErro.append(error.getDefaultMessage()).append(" ");
            });
            
            attributes.addFlashAttribute("mensagemErro", mensagemErro.toString());
            return "redirect:/detalhesEvento/{codigo}";
        }
        
        Optional<Evento> eventoOpt = er.findByCodigoAndUsuario(codigo, usuarioLogado);
        if (!eventoOpt.isPresent()) {
            attributes.addFlashAttribute("mensagemErro", "Evento não encontrado ou você não tem permissão para adicionar convidados.");
            return "redirect:/eventos";
        }
        
        Evento evento = eventoOpt.get();
        convidado.setEvento(evento);
        cr.save(convidado);
        
        attributes.addFlashAttribute("mensagemSucesso", "Convidado adicionado com sucesso!");
        return "redirect:/detalhesEvento/{codigo}";
    }
    
    @RequestMapping(value = "/deletarConvidado/{rg}", method = RequestMethod.GET)
    public String deletarConvidado(@PathVariable("rg") String rg, 
                                  RedirectAttributes attributes, HttpSession session) {
        
        Usuario usuarioLogado = verificarUsuarioLogado(session);
        if (usuarioLogado == null) {
            attributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para deletar convidados.");
            return "redirect:/login";
        }
        
        Optional<Convidado> convidadoOptional = cr.findById(rg);

        if (convidadoOptional.isPresent()) {
            Convidado convidado = convidadoOptional.get();
            Evento evento = convidado.getEvento();
            
            if (!evento.getUsuario().getId().equals(usuarioLogado.getId())) {
                attributes.addFlashAttribute("mensagemErro", "Você não tem permissão para deletar este convidado.");
                return "redirect:/eventos";
            }

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
    public String editarConvidado(@RequestBody Map<String, String> dadosConvidado, 
                                 HttpSession session) {
        try {
            Usuario usuarioLogado = verificarUsuarioLogado(session);
            if (usuarioLogado == null) {
                return "not_logged_in";
            }
            
            String rgOriginal = dadosConvidado.get("rg");
            String novoNome = dadosConvidado.get("nomeConvidado");
            String novoRg = dadosConvidado.get("novoRg");
            
            if (novoRg == null || novoRg.isEmpty()) {
                novoRg = rgOriginal;
            }
            
            Optional<Convidado> convidadoOptional = cr.findById(rgOriginal);
            
            if (convidadoOptional.isPresent()) {
                Convidado convidadoExistente = convidadoOptional.get();
                
                if (!convidadoExistente.getEvento().getUsuario().getId().equals(usuarioLogado.getId())) {
                    return "no_permission";
                }
                
                if (!rgOriginal.equals(novoRg)) {
                    Optional<Convidado> convidadoComNovoRg = cr.findById(novoRg);
                    if (convidadoComNovoRg.isPresent()) {
                        return "rg_exists";
                    }
                    
                    Convidado novoConvidado = new Convidado();
                    novoConvidado.setRg(novoRg);
                    novoConvidado.setNomeConvidado(novoNome);
                    novoConvidado.setEvento(convidadoExistente.getEvento());
                    
                    cr.save(novoConvidado);
                    cr.delete(convidadoExistente);
                } else {
                    convidadoExistente.setNomeConvidado(novoNome);
                    cr.save(convidadoExistente);
                }
                
                return "success";
            } else {
                return "not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
    
    @RequestMapping(value = "/editarEvento", method = RequestMethod.POST)
    @ResponseBody
    public String editarEvento(@RequestBody Map<String, String> dadosEvento, 
                              HttpSession session) {
        try {
            Usuario usuarioLogado = verificarUsuarioLogado(session);
            if (usuarioLogado == null) {
                return "not_logged_in";
            }
            
            long codigo = Long.parseLong(dadosEvento.get("codigo"));
            String novoNome = dadosEvento.get("nome");
            String novoLocal = dadosEvento.get("local");
            String novaData = dadosEvento.get("data");
            String novoHorario = dadosEvento.get("horario");

            Optional<Evento> eventoOptional = er.findByCodigoAndUsuario(codigo, usuarioLogado);

            if (eventoOptional.isPresent()) {
                Evento eventoExistente = eventoOptional.get();
                eventoExistente.setNome(novoNome);
                eventoExistente.setLocal(novoLocal);
                eventoExistente.setData(novaData);
                eventoExistente.setHorario(novoHorario);
                er.save(eventoExistente);
                return "success";
            } else {
                return "not_found_or_no_permission";
            }
        } catch (NumberFormatException e) {
            return "error: Invalid Code";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

	@RequestMapping(value = "/editarDescricaoEvento", method = RequestMethod.POST)
	@ResponseBody
	public String editarDescricaoEvento(@RequestBody Map<String, String> dadosEvento, 
	                                   HttpSession session) {
	    try {
	        Usuario usuarioLogado = verificarUsuarioLogado(session);
	        if (usuarioLogado == null) {
	            return "not_logged_in";
	        }
	        
	        long codigo = Long.parseLong(dadosEvento.get("codigo"));
	        String novaDescricao = dadosEvento.get("descricao");
	
	        Optional<Evento> eventoOptional = er.findByCodigoAndUsuario(codigo, usuarioLogado);
	
	        if (eventoOptional.isPresent()) {
	            Evento eventoExistente = eventoOptional.get();
	            eventoExistente.setDescricao(novaDescricao);
	            er.save(eventoExistente);
	            return "success";
	        } else {
	            return "not_found_or_no_permission";
	        }
	    } catch (NumberFormatException e) {
	        return "error: Invalid Code";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "error: " + e.getMessage();
	    }
	}
   }