package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.repository.NucleoPedagogicoRepository;
import com.docente.gestionnotas.service.AlumnoService;
import com.docente.gestionnotas.service.CursoService;
import com.docente.gestionnotas.service.NotaService;
import com.docente.gestionnotas.service.NucleoPedagogicoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.*;

@Controller
@RequestMapping("/ui/cursos")
public class CursoUIController {

    private final CursoService cursoService;
    private final NucleoPedagogicoService nucleoService; // Necesario para añadir núcleos
    private final AlumnoService alumnoService; // <-- Asegúrate de que AlumnoService está inyectado
    private final NotaService notaService;
    private final NucleoPedagogicoRepository nucleoRepository;


    public CursoUIController(CursoService cursoService, NucleoPedagogicoService nucleoService, AlumnoService alumnoService, NotaService notaService, NucleoPedagogicoRepository nucleoRepository) {
        this.cursoService = cursoService;
        this.nucleoService = nucleoService;
        this.alumnoService = alumnoService; // <-- Inicializar
        this.notaService = notaService;
        this.nucleoRepository = nucleoRepository;
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
    // En com.docente.gestionnotas.ui.controller.CursoUIController.java

    @GetMapping("/{cursoId}/detalles")
    public String mostrarDetallesCurso(@PathVariable Long cursoId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Curso curso = cursoService.findById(cursoId);

            // 1. Inyectar el curso
            model.addAttribute("curso", curso);

            // 2. Inyectar 'promedios' (CORRECCIÓN ANTERIOR)
            // Usa tu lógica de cálculo real. Si no existe, usa un HashMap vacío.
            Map<Long, Double> promedios = new HashMap<>(); // Ejemplo temporal
            model.addAttribute("promedios", promedios);

            // 3. Inyectar 'nuevaNota' (NUEVA CORRECCIÓN)
            // Debes inicializar el objeto que Thymeleaf necesita para el formulario de la nota.
            model.addAttribute("nuevaNota", new Nota()); // Reemplaza 'Nota' con el nombre real de tu clase de entidad/form.

            return "cursos/detalles";

        } catch (java.util.NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: Curso no encontrado.");
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

    // En com.docente.gestionnotas.ui.controller.CursoUIController.java

    @PostMapping("/{cursoId}/nucleos/crear")
    public String guardarNucleo(@PathVariable Long cursoId,
                                NucleoPedagogico nucleo,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        try {
            nucleoService.crearNucleo(cursoId, nucleo);

            redirectAttributes.addFlashAttribute("success", "Núcleo creado exitosamente.");
            return "redirect:/ui/cursos/" + cursoId + "/detalles";

        }
        catch (Exception e) {

            // --- Bloque de Manejo de Error ---

            // A. Cargar el Curso para el encabezado
            try {
                Curso curso = cursoService.findById(cursoId);
                model.addAttribute("curso", curso);
            } catch (java.util.NoSuchElementException ignored) {
                redirectAttributes.addFlashAttribute("error", "Error: Curso no encontrado.");
                return "redirect:/ui/cursos";
            }

            // B. Re-inyectamos el objeto 'nucleo' (que tiene los datos que fallaron)
            // ESTO RESUELVE EL ERROR DE BINDING
            model.addAttribute("nucleo", nucleo); // <--- DEBE SER "nucleo"

            // C. Pasamos el mensaje de error (¡IMPORTANTE! Para ver la CAUSA real)
            model.addAttribute("error", "Error al guardar: " + e.getMessage());

            // D. Volvemos a la plantilla del formulario
            return "cursos/crear_nucleo";
        }
    }

    @GetMapping("/{cursoId}/nucleos/crear")
    public String mostrarFormularioCreacionNucleo(@PathVariable Long cursoId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Curso curso = cursoService.findById(cursoId);

            model.addAttribute("curso", curso);

            // El objeto vacío que necesita la plantilla para th:object="${nucleo}"
            model.addAttribute("nucleo", new NucleoPedagogico());

            return "cursos/crear_nucleo";
        } catch (java.util.NoSuchElementException e) {
            // Maneja si el curso no existe
            redirectAttributes.addFlashAttribute("error", "Error: Curso base no encontrado.");
            return "redirect:/ui/cursos";
        }
    }

    // En com.docente.gestionnotas.ui.controller.CursoUIController.java (o donde esté)

    @PostMapping("/notas/guardar")
    public String guardarNotaMejorado(
            @RequestParam Long nucleoId,
            @RequestParam Long cursoId, // Usaremos este cursoId para la redirección
            @Valid Nota nuevaNota,       // Agregar @Valid para que Spring valide el objeto
            BindingResult bindingResult, // Agregar BindingResult para capturar errores de validación
            RedirectAttributes redirectAttributes) {

        // --- 1. Manejo de Errores de Validación (Recomendado) ---
        // Si tienes anotaciones de validación (@Min, @Max, etc.) en tu clase Nota,
        // y hay errores, no debes continuar.
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error de validación en la nota. Revise los campos.");
            // Si hay errores de validación, la mejor práctica es redirigir con un error
            // para evitar un error de NullPointerException si la vista intenta acceder a datos incompletos.
            return "redirect:/ui/cursos/" + cursoId + "/detalles";
        }

        try {
            // Lógica de negocio
            notaService.agregarNotaANucleo(nucleoId, nuevaNota);

            redirectAttributes.addFlashAttribute("success", "Nota guardada exitosamente.");

            // --- 2. Corrección de Redirección (Usar el cursoId recibido) ---
            // Eliminamos la línea 'cursoId = nuevaNota.getCursoIdTemporal();'
            // Ya recibimos cursoId del formulario como @RequestParam y es más fiable.

            return "redirect:/ui/cursos/" + cursoId + "/detalles";

        } catch (Exception e) {
            // Si hay un error al guardar (ej. error de DB)
            redirectAttributes.addFlashAttribute("error", "Error al guardar la nota: " + e.getMessage());

            // --- 3. Corrección de Manejo de Excepciones (Usar el cursoId recibido) ---
            // Eliminamos la línea 'Long cursoId = cursoService.findCursoIdByNucleoId(nucleoId);'
            // Ya tenemos el cursoId disponible en el parámetro del método.

            return "redirect:/ui/cursos/" + cursoId + "/detalles";
        }
    }

    @Transactional
    public void deleteById(Long nucleoId) {
        // Si usas CascadeType.ALL en tu entidad NucleoPedagogico para las notas,
        // al eliminar el núcleo, las notas asociadas también se eliminarán automáticamente.
        nucleoRepository.deleteById(nucleoId);
    }
}