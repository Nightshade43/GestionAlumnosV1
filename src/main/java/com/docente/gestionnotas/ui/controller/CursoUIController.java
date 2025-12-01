package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.service.AlumnoService;
import com.docente.gestionnotas.service.CursoService;
import com.docente.gestionnotas.service.NucleoPedagogicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/ui/cursos")
public class CursoUIController {

    private final CursoService cursoService;
    private final NucleoPedagogicoService nucleoService; // Necesario para añadir núcleos
    private final AlumnoService alumnoService; // <-- Asegúrate de que AlumnoService está inyectado


    public CursoUIController(CursoService cursoService, NucleoPedagogicoService nucleoService, AlumnoService alumnoService) {
        this.cursoService = cursoService;
        this.nucleoService = nucleoService;
        this.alumnoService = alumnoService; // <-- Inicializar
    }

    // GET /ui/cursos (Listar Cursos)
    @GetMapping
    public String listarCursos(Model model) {
        model.addAttribute("cursos", cursoService.findAll());
        return "cursos/lista";
    }

    // GET /ui/cursos/crear (Mostrar formulario de creación de Curso)
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("curso", new Curso());
        return "cursos/crear";
    }

    // POST /ui/cursos (Guardar nuevo Curso)
    @PostMapping
    public String guardarCurso(Curso curso, Model model) {
        try {
            // Lógica de Negocio en la capa de UI: generar el nombre completo
            String nombreCompleto = curso.getNombreMateria() + " - " + curso.getAnio() + "º " + curso.getDivision();
            curso.setNombreCompleto(nombreCompleto);

            cursoService.save(curso);
            return "redirect:/ui/cursos";

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el curso. Verifique los datos. Detalle: " + e.getMessage());
            model.addAttribute("curso", curso);
            return "cursos/crear";
        }
    }

    // POST /ui/cursos/{id}/nucleos (Guardar nuevo Núcleo)
    @PostMapping("/{id}/nucleos")
    public String guardarNucleo(@PathVariable Long id, NucleoPedagogico nucleo, Model model) {
        try {
            nucleoService.agregarNucleoACurso(id, nucleo);
            // Redireccionar al detalle del curso (ruta aún no implementada)
            return "redirect:/ui/cursos/" + id + "/detalles";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el núcleo. Detalle: " + e.getMessage());
            try {
                model.addAttribute("curso", cursoService.findById(id));
            } catch (NoSuchElementException innerE) {
                return "redirect:/ui/cursos";
            }
            model.addAttribute("nucleo", nucleo);
            return "cursos/crear_nucleo";
        }
    }

    // GET /ui/cursos/{id}/detalles
    @GetMapping("/{id}/detalles")
    public String verDetallesCurso(@PathVariable Long id, Model model) {
        try {
            Curso curso = cursoService.findById(id);

            // Mapa para guardar los promedios: Clave=ID del Núcleo, Valor=Promedio (Double)
            java.util.Map<Long, Double> promediosNucleo = new java.util.HashMap<>();

            // 1. Iterar sobre los núcleos y calcular el promedio para cada uno
            if (curso.getNucleos() != null) {
                for (NucleoPedagogico nucleo : curso.getNucleos()) {
                    double promedio = cursoService.calcularPromedioNucleo(id, nucleo.getId());
                    promediosNucleo.put(nucleo.getId(), promedio);
                }
            }

            // 2. Añadir ambos objetos al modelo
            model.addAttribute("curso", curso);
            model.addAttribute("nuevaNota", new Nota());
            model.addAttribute("promedios", promediosNucleo); // <-- NUEVO: Mapa de promedios

            return "cursos/detalles";

        } catch (NoSuchElementException e) {
            return "redirect:/ui/cursos";
        }
    }

    // GET /ui/cursos/{id}/inscribir
    @GetMapping("/{id}/inscribir")
    public String mostrarFormularioInscripcion(@PathVariable Long id, Model model) {
        try {
            Curso curso = cursoService.findById(id);

            // 1. Obtener todos los alumnos que *no* están inscritos en este curso
            List<Alumno> todosAlumnos = alumnoService.findAll();
            List<Alumno> alumnosInscritos = curso.getAlumnos();

            // Filtrar alumnos no inscritos (Lógica de Negocio en la capa de UI)
            todosAlumnos.removeAll(alumnosInscritos);

            model.addAttribute("curso", curso);
            model.addAttribute("alumnosDisponibles", todosAlumnos);

            return "cursos/inscribir_alumno";

        } catch (NoSuchElementException e) {
            return "redirect:/ui/cursos";
        }
    }

    // POST /ui/cursos/{id}/inscribir
    @PostMapping("/{id}/inscribir")
    public String inscribirAlumno(@PathVariable Long id,
                                  @RequestParam String alumnoId,
                                  RedirectAttributes redirectAttributes) {

        // URL de redirección final: Detalles del curso
        String redirectUrl = "redirect:/ui/cursos/" + id + "/detalles";

        try {
            // Llama al servicio que maneja la lógica de la relación M:M
            alumnoService.inscribirAlumnoACurso(alumnoId, id);

            redirectAttributes.addFlashAttribute("success", "Alumno inscrito correctamente.");
            return redirectUrl;

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirectUrl;
        }
    }

    // POST /ui/cursos/{cursoId}/desinscribir
    @PostMapping("/{cursoId}/desinscribir")
    public String desinscribirAlumno(@PathVariable Long cursoId,
                                     @RequestParam String alumnoId,
                                     RedirectAttributes redirectAttributes) {

        // URL de redirección final: Detalles del curso
        String redirectUrl = "redirect:/ui/cursos/" + cursoId + "/detalles";

        try {
            alumnoService.desinscribirAlumnoDeCurso(alumnoId, cursoId);

            redirectAttributes.addFlashAttribute("success", "Alumno desinscrito correctamente.");
            return redirectUrl;

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return redirectUrl;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return redirectUrl;
        }
    }

    // NUEVO MÉTODO: POST para eliminar un Curso
    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cursoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Curso eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/ui/cursos"; // Redireccionar a la lista de cursos
    }

    // Método 1: GET - Muestra el formulario para crear núcleo
    // Mapea a /ui/cursos/{id}/nucleos/crear
    @GetMapping("/{id}/nucleos/crear")
    public String mostrarFormularioNucleo(@PathVariable Long id, Model model) {
        try {
            Curso curso = cursoService.findById(id);

            model.addAttribute("curso", curso);
            model.addAttribute("nucleoPedagogico", new NucleoPedagogico()); // Objeto vacío para el formulario

            return "cursos/crear_nucleo";

        } catch (NoSuchElementException e) {
            return "redirect:/ui/cursos";
        }
    }

    // Método 2: POST - Guarda el nuevo núcleo
    // Mapea a /ui/cursos/{cursoId}/nucleos/crear
    @PostMapping("/{cursoId}/nucleos/crear")
    public String guardarNucleo(@PathVariable Long cursoId,
                                NucleoPedagogico nucleo, // Objeto Nucleo viene del formulario
                                RedirectAttributes redirectAttributes) {

        try {
            nucleoService.crearNucleo(cursoId, nucleo);

            redirectAttributes.addFlashAttribute("success", "Núcleo pedagógico creado exitosamente.");

            // Redirecciona a la página de detalles del curso
            return "redirect:/ui/cursos/" + cursoId + "/detalles";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el núcleo: Curso no encontrado.");
            return "redirect:/ui/cursos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el núcleo. Detalle: " + e.getMessage());
            return "redirect:/ui/cursos/" + cursoId + "/detalles"; // Redirigir para ver el error
        }
    }
}