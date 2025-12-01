package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.service.CursoService;
import com.docente.gestionnotas.service.NotaService;
import com.docente.gestionnotas.service.NucleoPedagogicoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/ui/notas")
public class NotaUIController {

    private final NotaService notaService;
    private final NucleoPedagogicoService nucleoService; // <-- Nuevo
    private final CursoService cursoService; // <-- Nuevo (Para el redirect)

    public NotaUIController(NotaService notaService, NucleoPedagogicoService nucleoService, CursoService cursoService) {
        this.notaService = notaService;
        this.nucleoService = nucleoService;
        this.cursoService = cursoService;
    }

    // POST /ui/notas/guardar
    @PostMapping("/guardar")
    public String guardarNota(
            @RequestParam Long cursoId, // Recibido del campo oculto
            @RequestParam Long nucleoId, // Recibido del campo oculto
            Nota nota,
            RedirectAttributes redirectAttributes) {

        // La URL de redirección final
        String redirectUrl = "redirect:/ui/cursos/" + cursoId + "/detalles";

        try {
            // La lógica de negocio solo necesita la Nota y el ID del Núcleo
            notaService.agregarNotaANucleo(nucleoId, nota);

            redirectAttributes.addFlashAttribute("success", "Nota agregada exitosamente.");
            return redirectUrl;

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: El núcleo no existe.");
            return redirectUrl;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Muestra 'Nota debe estar entre 1 y 10'
            return redirectUrl;
        }
    }

    // NUEVO MÉTODO: POST para eliminar una Nota
    @PostMapping("/eliminar-nota")
    public String eliminarNota(@RequestParam Long notaId,
                               @RequestParam Long cursoId,
                               RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/ui/cursos/" + cursoId + "/detalles";

        try {
            notaService.deleteById(notaId);
            redirectAttributes.addFlashAttribute("success", "Nota eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la nota.");
        }
        return redirectUrl;
    }

    // NUEVO MÉTODO: POST para eliminar un Núcleo
    @PostMapping("/eliminar-nucleo")
    public String eliminarNucleo(@RequestParam Long nucleoId,
                                 @RequestParam Long cursoId,
                                 RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/ui/cursos/" + cursoId + "/detalles";

        try {
            nucleoService.deleteById(nucleoId);
            redirectAttributes.addFlashAttribute("success", "Núcleo pedagógico eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el núcleo. Detalle: " + e.getMessage());
        }
        return redirectUrl;
    }
}