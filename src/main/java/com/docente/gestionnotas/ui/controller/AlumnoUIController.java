package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.service.AlumnoService;
import com.docente.gestionnotas.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/ui/alumnos") // Prefijo para las rutas de la UI
public class AlumnoUIController {

    private final AlumnoService alumnoService;
    private final CursoService cursoService;

    public AlumnoUIController(AlumnoService alumnoService, CursoService cursoService) {
        this.alumnoService = alumnoService;
        this.cursoService = cursoService;
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


    /**
     * Procesa la solicitud POST para guardar un nuevo Alumno.
     * Mapea a POST /ui/alumnos/guardar
     */
    @PostMapping("/guardar")
    public String guardarAlumno(@Valid @ModelAttribute("alumno") Alumno alumno,
                                BindingResult result,
                                RedirectAttributes ra) {

        // 1. Manejo de Errores de Validación (Si el usuario olvidó un campo @NotBlank)
        if (result.hasErrors()) {
            // Vuelve al formulario para mostrar errores
            return "alumnos/crear";
        }

        try {
            // 2. Lógica de Negocio: Guardar el objeto Alumno
            alumnoService.save(alumno);

            // 3. Redirección con Mensaje de Éxito
            ra.addFlashAttribute("success",
                    "Alumno '" + alumno.getNombre() + " " + alumno.getApellido() + "' registrado con éxito.");

            // Redirige al listado principal de alumnos
            return "redirect:/ui/alumnos";

        } catch (Exception e) {
            // Manejo de errores de DNI/Email duplicado, etc.
            ra.addFlashAttribute("error",
                    "Error al registrar el alumno: " + e.getMessage());

            // Si hay un error, redirigir al formulario para que el usuario pueda reintentar
            return "redirect:/ui/alumnos/crear";
        }
    }

    // NUEVO MÉTODO: POST para eliminar un Alumno
    @PostMapping("/eliminar/{id}")
    public String eliminarAlumno(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alumnoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Alumno eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/ui/alumnos"; // Redireccionar a la lista de alumnos
    }

    /**
     * Muestra los detalles de un Alumno específico, incluyendo sus cursos inscritos.
     * GET /ui/alumnos/{id}
     */
    @GetMapping("/{id}")
    public String mostrarDetallesAlumno(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            // 1. Buscar el alumno por ID (asumiendo que AlumnoService tiene findById)
            Alumno alumno = alumnoService.findById(id);

            // 2. Pasar el alumno al modelo (con su lista de cursos cargada por defecto)
            model.addAttribute("alumno", alumno);

            // 3. Muestra listado de cursos
            List<Curso> cursosDisponibles = cursoService.findAll(); // ASUMO que tienes un CursoService
            model.addAttribute("cursosDisponibles", cursosDisponibles);

            // Necesitas asegurarte de que tu clase Alumno esté anotada con @Entity
            // y que la relación @ManyToMany con Curso esté configurada correctamente.

            return "alumnos/detalles";

        } catch (NoSuchElementException e) {
            // Si el ID es inválido, redirige a la lista con un error
            ra.addFlashAttribute("error", "Error: El alumno con ID " + id + " no fue encontrado.");
            return "redirect:/ui/alumnos";
        }
    }

    /**
            * Muestra el formulario precargado para editar un alumno.
            * Mapea a GET /ui/alumnos/{id}/editar
     **/
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            // Usamos el findById que ya corregimos
            Alumno alumno = alumnoService.findById(id);
            model.addAttribute("alumno", alumno);

            // Usaremos la misma plantilla de creación para la edición
            return "alumnos/crear";

        } catch (NoSuchElementException e) {
            ra.addFlashAttribute("error", "El alumno a editar no fue encontrado.");
            return "redirect:/ui/alumnos";
        }
    }

    // Dentro de AlumnoUIController.java

    /**
     * Procesa la matriculación de un alumno a un curso específico.
     * Mapea a POST /ui/alumnos/matricular
     */
    @PostMapping("/matricular")
    public String matricularAlumnoACurso(@RequestParam Long alumnoId,
                                         @RequestParam Long cursoId,
                                         RedirectAttributes ra) {
        try {
            // 1. Delegar la lógica de negocio al servicio
            alumnoService.matricular(alumnoId, cursoId);

            ra.addFlashAttribute("success", "El alumno fue matriculado con éxito al curso.");

            // 2. Redirigir a la vista de detalles del alumno
            return "redirect:/ui/alumnos/" + alumnoId;

        } catch (NoSuchElementException e) {
            // Alumno o Curso no encontrado
            ra.addFlashAttribute("error", "Error de matriculación: " + e.getMessage());
            return "redirect:/ui/alumnos/" + alumnoId;

        } catch (IllegalArgumentException e) {
            // Error de negocio: Ya está inscrito
            ra.addFlashAttribute("error", "Error de matriculación: " + e.getMessage());
            return "redirect:/ui/alumnos/" + alumnoId;

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error inesperado al matricular: " + e.getMessage());
            return "redirect:/ui/alumnos/" + alumnoId;
        }
    }
}