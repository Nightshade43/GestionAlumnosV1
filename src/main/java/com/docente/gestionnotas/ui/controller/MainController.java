package com.docente.gestionnotas.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/") // Mapea la URL raíz de la aplicación
    public String mainDashboard() {
        return "main_dashboard"; // Retorna el template main_dashboard.html (si está en /templates/)
    }

    // Si usas un prefijo de UI, podrías mapear también /ui
    @GetMapping("/ui")
    public String uiDashboard() {
        return "main_dashboard";
    }
}