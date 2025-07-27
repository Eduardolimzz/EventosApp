package com.eventosapp.controllers;

import com.eventosapp.models.Usuario;
import com.eventosapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/eventos";
        }
        return "index";
    }
    
    @GetMapping("/cadastro")
    public String mostrarCadastro(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/eventos";
        }
        
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }
    
    @PostMapping("/cadastro")
    public String processarCadastro(@Valid @ModelAttribute Usuario usuario,
                                   BindingResult result,
                                   @RequestParam String confirmaSenha,
                                   RedirectAttributes attributes,
                                   Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("erro", "Por favor, corrija os erros no formulário");
            return "cadastro";
        }
        
        if (!usuario.getSenha().equals(confirmaSenha)) {
            model.addAttribute("erro", "Senhas não coincidem");
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }
        
        try {
            authService.cadastrarUsuario(usuario);
            
            attributes.addFlashAttribute("sucesso", "Conta criada com sucesso! Faça login para continuar.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }
    }
    
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/eventos";
        }
        
        return "login";
    }
    
    @PostMapping("/login")
    public String processarLogin(@RequestParam String email,
                                @RequestParam String senha,
                                HttpSession session,
                                RedirectAttributes attributes,
                                Model model) {
        
        try {
            Usuario usuario = authService.login(email, senha);
            
            session.setAttribute("usuarioLogado", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNome", usuario.getNome());
            session.setAttribute("usuarioEmail", usuario.getEmail());
            
            return "redirect:/eventos";
            
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes attributes) {
        session.invalidate();
        attributes.addFlashAttribute("sucesso", "Logout realizado com sucesso!");
        return "redirect:/";
    }
    
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        return "perfil";
    }
    
    @PostMapping("/perfil")
    public String atualizarPerfil(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,
                                 HttpSession session,
                                 RedirectAttributes attributes,
                                 Model model) {
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("erro", "Por favor, corrija os erros no formulário");
            return "perfil";
        }
        
        try {
            usuario.setId(usuarioLogado.getId());
            usuario.setDataCriacao(usuarioLogado.getDataCriacao());
            usuario.setUltimoLogin(usuarioLogado.getUltimoLogin());
            
            Usuario usuarioAtualizado = authService.atualizarUsuario(usuario);
            
            session.setAttribute("usuarioLogado", usuarioAtualizado);
            session.setAttribute("usuarioNome", usuarioAtualizado.getNome());
            session.setAttribute("usuarioEmail", usuarioAtualizado.getEmail());
            
            attributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso!");
            return "redirect:/perfil";
            
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "perfil";
        }
    }
    
    @PostMapping("/api/verificar-email")
    @ResponseBody
    public boolean verificarEmail(@RequestParam String email) {
        return authService.emailExiste(email);
    }
    
    @GetMapping("/recuperar-senha")
    public String recuperarSenha() {
        return "recuperar-senha";
    }
}