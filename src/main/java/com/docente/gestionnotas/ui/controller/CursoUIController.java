package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.service.AlumnoService;
import com.docente.gestionnotas.service.CursoService;
import com.docente.gestionnotas.service.NucleoPedagogicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controlador de UI para gestionar Cursos.
 * Maneja las vistas relacionadas con la creación, visualización y eliminación de cursos.
 */
@Controller
@RequestMapping("/ui/cursos")
public class CursoUIController {

    private final CursoService cursoService;
    private final NucleoPedagogicoService nucleoService;
    private final AlumnoService alumnoService;

    public CursoUIController(
            CursoService cursoService,
            NucleoPedagogicoService nucleoService,
            AlumnoService alumnoService) {
        this.cursoService = cursoService;
        this.nucleoService = nucleoService;
        this.alumnoService = alumnoService;
    }

    /**
     * Muestra el formulario de creación de curso.
     * GET /ui/cursos/crear
     */
// Dentro de @RequestMapping("/ui/cursos") en CursoUIController.java

    @GetMapping("/crear") // Mapea a la URL /ui/cursos/crear
    public String mostrarFormularioCreacion(Model model) {
        // Es CRÍTICO inyectar un objeto Curso vacío para que el formulario funcione
        model.addAttribute("curso", new Curso());

        // Retorna el template que acabas de crear
        return "cursos/crear";
    }

    /**
     * Guarda un nuevo curso.
     * POST /ui/cursos
     */
    /**
     * Procesa la solicitud POST del formulario de creación, valida y guarda el curso.
     * (POST /ui/cursos/guardar)
     */
    @PostMapping("/guardar")
    public String guardarCurso(@Valid @ModelAttribute("curso") Curso curso,
                               BindingResult result,
                               RedirectAttributes ra) {

        // 1. Manejo de Errores de Validación (Revisa anotaciones en Curso.java)
        if (result.hasErrors()) {
            // Si hay errores de @NotBlank, @Min, etc., regresa a la vista para mostrarlos
            return "cursos/crear";
        }

        try {
            // 2. Ejecuta la lógica de negocio (guardar en la DB)
            // El @PrePersist en Curso.java se encargará de generar el nombreCompleto
            cursoService.save(curso);

            // 3. Redirección con Mensaje de Éxito
            ra.addFlashAttribute("success",
                    "Curso '" + curso.getNombreMateria() + " - " + curso.getDivision() + "' creado con éxito.");

            // Redirige al listado principal de cursos (GET /ui/cursos)
            return "redirect:/ui/cursos";

        } catch (Exception e) {
            // Manejo de errores de base de datos o duplicados
            // Redirigir de vuelta al formulario de creación para que el usuario pueda reintentar
            ra.addFlashAttribute("error",
                    "Error al guardar el curso: " + e.getMessage());

            return "redirect:/ui/cursos/crear";
        }
    }

    /**
     * Muestra los detalles de un curso específico.
     * GET /ui/cursos/{cursoId}/detalles
     */
    @GetMapping("/{cursoId}/detalles")
    public String mostrarDetallesCurso(
            @PathVariable Long cursoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Curso curso = cursoService.findById(cursoId);

            // Inyectar el curso
            model.addAttribute("curso", curso);

            // Inyectar promedios (puede mejorarse con lógica real de cálculo)
            Map<Long, Double> promedios = calcularPromedios(curso);
            model.addAttribute("promedios", promedios);

            // Inyectar objeto para el formulario de nueva nota
            model.addAttribute("nuevaNota", new Nota());

            return "cursos/detalles";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: Curso no encontrado.");
            return "redirect:/ui/cursos";
        }
    }

    /**
     * Muestra el formulario para crear un nuevo núcleo.
     * GET /ui/cursos/{cursoId}/nucleos/crear
     */
    @GetMapping("/{cursoId}/nucleos/crear")
    public String mostrarFormularioCreacionNucleo(
            @PathVariable Long cursoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Curso curso = cursoService.findById(cursoId);
            model.addAttribute("curso", curso);
            model.addAttribute("nucleo", new NucleoPedagogico());
            return "cursos/crear_nucleo";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: Curso no encontrado.");
            return "redirect:/ui/cursos";
        }
    }

    /**
     * Guarda un nuevo núcleo pedagógico.
     * POST /ui/cursos/{cursoId}/nucleos/crear
     */
    @PostMapping("/{cursoId}/nucleos/crear")
    public String guardarNucleo(
            @PathVariable Long cursoId,
            NucleoPedagogico nucleo,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            nucleoService.crearNucleo(cursoId, nucleo);
            redirectAttributes.addFlashAttribute("success", "Núcleo creado exitosamente.");
            return "redirect:/ui/cursos/" + cursoId + "/detalles";

        } catch (Exception e) {

            // Manejo de error: volver al formulario con los datos ingresados
            try {
                Curso curso = cursoService.findById(cursoId);
                model.addAttribute("curso", curso);
            } catch (NoSuchElementException ignored) {
                redirectAttributes.addFlashAttribute("error", "Error: Curso no encontrado.");
                return "redirect:/ui/cursos";
            }

            model.addAttribute("nucleo", nucleo);
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "cursos/crear_nucleo";
        }
    }

    /**
     * Muestra el formulario de inscripción de alumnos.
     * GET /ui/cursos/{id}/inscribir
     */
    @GetMapping("/{id}/inscribir")
    public String mostrarFormularioInscripcion(@PathVariable Long id, Model model) {
        try {
            Curso curso = cursoService.findById(id);

            // Obtener alumnos no inscritos en este curso
            List<Alumno> todosAlumnos = alumnoService.findAll();
            List<Alumno> alumnosInscritos = curso.getAlumnos();
            todosAlumnos.removeAll(alumnosInscritos);

            model.addAttribute("curso", curso);
            model.addAttribute("alumnosDisponibles", todosAlumnos);

            return "cursos/inscribir_alumno";

        } catch (NoSuchElementException e) {
            return "redirect:/ui/cursos";
        }
    }

    /**
     * Inscribe un alumno a un curso.
     * POST /ui/cursos/{id}/inscribir
     */
    @PostMapping("/{id}/inscribir")
    public String inscribirAlumno(
            @PathVariable Long id,
            @RequestParam Long alumnoId,
            RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/ui/cursos/" + id + "/detalles";

        try {
            alumnoService.inscribirAlumnoACurso(alumnoId, id);
            redirectAttributes.addFlashAttribute("success", "Alumno inscrito correctamente.");
            return redirectUrl;

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirectUrl;
        }
    }

    /**
     * Desinscribe un alumno de un curso.
     * POST /ui/cursos/{cursoId}/desinscribir
     */
    @PostMapping("/{cursoId}/desinscribir")
    public String desinscribirAlumno(
            @PathVariable Long cursoId,
            @RequestParam Long alumnoId,
            RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/ui/cursos/" + cursoId + "/detalles";

        try {
            alumnoService.desinscribirAlumnoDeCurso(alumnoId, cursoId);
            redirectAttributes.addFlashAttribute("success", "Alumno desinscrito correctamente.");
            return redirectUrl;

        } catch (NoSuchElementException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return redirectUrl;
        }
    }

    /**
     * Elimina un curso.
     * POST /ui/cursos/eliminar/{id}
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cursoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Curso eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/ui/cursos";
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Calcula los promedios de cada núcleo del curso.
     * Este es un método auxiliar privado.
     */
    private Map<Long, Double> calcularPromedios(Curso curso) {
        Map<Long, Double> promedios = new HashMap<>();

        if (curso.getNucleos() != null) {
            curso.getNucleos().forEach(nucleo -> {
                if (nucleo.getNotas() != null && !nucleo.getNotas().isEmpty()) {
                    double promedio = nucleo.getNotas().stream()
                            .mapToInt(nota -> nota.getValor())
                            .average()
                            .orElse(0.0);
                    promedios.put(nucleo.getId(), promedio);
                }
            });
        }

        return promedios;
    }

    @GetMapping("") // O la URL que uses como inicio, ejemplo: "/"
    public String listarCursos(Model model) {
        // Asume que tienes un servicio para obtener todos los cursos
        List<Curso> cursos = cursoService.findAll();

        // El nombre del atributo debe coincidir con th:each="curso : ${cursos}"
        model.addAttribute("cursos", cursos);

        return "cursos/index"; // Retorna el template index.html (asumiendo que está en /templates/cursos/)
    }
}