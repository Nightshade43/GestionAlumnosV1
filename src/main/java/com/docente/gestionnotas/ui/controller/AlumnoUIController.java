package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.service.AlumnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/ui/alumnos") // Prefijo para las rutas de la UI
public class AlumnoUIController {

    private final AlumnoService alumnoService;

    public AlumnoUIController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    // Ruta: /ui/alumnos
    @GetMapping
    public String listarAlumnos(Model model) {
        // 1. Obtener los datos del servicio
        List<Alumno> alumnos = alumnoService.findAll();

        // 2. Agregar los datos al modelo para que Thymeleaf los use
        model.addAttribute("alumnos", alumnos);

        // 3. Devolver el nombre de la plantilla HTML (src/main/resources/templates/alumnos/lista.html)
        return "alumnos/lista";
    }

    // Ruta: /ui/alumnos/crear
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        // Objeto vacío para que Thymeleaf pueda enlazar los campos del formulario
        model.addAttribute("alumno", new Alumno());
        return "alumnos/crear";
    }

    // POST /ui/alumnos: Procesa el formulario y guarda
    @PostMapping
    public String guardarAlumno(Alumno alumno, Model model) {
        try {
            // 1. Llama al servicio para guardar la entidad
            alumnoService.save(alumno);

            // 2. Redirecciona a la lista de alumnos
            // "redirect:..." genera una nueva petición GET a la URL especificada.
            return "redirect:/ui/alumnos";

        } catch (Exception e) {
            // Manejo básico de errores (ej. ID duplicado o campo nulo)
            model.addAttribute("error", "Error al guardar el alumno: " + e.getMessage());
            // Si hay un error, vuelve a la vista de creación manteniendo los datos
            model.addAttribute("alumno", alumno);
            return "alumnos/crear";
        }
    }

    // NUEVO MÉTODO: POST para eliminar un Alumno
    @PostMapping("/eliminar/{id}")
    public String eliminarAlumno(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            alumnoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Alumno eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/ui/alumnos"; // Redireccionar a la lista de alumnos
    }
}