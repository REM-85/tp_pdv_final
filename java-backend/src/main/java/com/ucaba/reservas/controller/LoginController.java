package com.ucaba.reservas.controller;

import com.ucaba.reservas.dto.AuthRequest;
import com.ucaba.reservas.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("login")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña inválidos");
        }
        return "login";
    }

    @PostMapping("login")
    public String doLogin(AuthRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            com.ucaba.reservas.dto.AuthResponse auth = authService.login(request);
            String token = auth.getToken();
            Cookie cookie = new Cookie("AUTH_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            // cookie duration in seconds (same as expiresIn)
            cookie.setMaxAge((int) auth.getExpiresIn());
            response.addCookie(cookie);
            return "redirect:/";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("error", "1");
            return "redirect:/login";
        }
    }
}
