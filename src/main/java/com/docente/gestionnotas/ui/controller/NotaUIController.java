package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.service.NotaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Controlador de UI para gestionar Notas.
 * Maneja las operaciones de creación y eliminación de notas.
 */
@Controller
@RequestMapping("/ui/notas")
public class NotaUIController {

    private final NotaService notaService;

    public NotaUIController(NotaService notaService) {
        this.notaService = notaService;
    }

    /**
     * Guarda una nueva nota en un núcleo pedagógico.
     * POST /ui/notas/guardar
     */
    @PostMapping("/guardar")
    public String guardarNota(
            @RequestParam Long cursoId,
            @RequestParam Long nucleoId,
            Nota nota,
            RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/ui/cursos/" + cursoId + "/detalles";

        try {
            notaService.agregarNotaANucleo(nucleoId, nota);
            redirectAttributes.addFlashAttribute("success", "Nota agregada exitosamente.");
            return redirectUrl;

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: El núcleo no existe.");
            return redirectUrl;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return redirectUrl;
        }
    }

    /**
     * Elimina una nota existente.
     * POST /ui/notas/eliminar/{notaId}
     */
    @PostMapping("/eliminar/{notaId}")
    public String eliminarNota(
            @PathVariable Long notaId,
            @RequestParam Long cursoId,
            RedirectAttributes redirectAttributes) {

        try {
            notaService.deleteById(notaId);
            redirectAttributes.addFlashAttribute("success", "Nota eliminada exitosamente.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Error: La nota no existe.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar la nota: " + e.getMessage());
        }

        return "redirect:/ui/cursos/" + cursoId + "/detalles";
    }
}