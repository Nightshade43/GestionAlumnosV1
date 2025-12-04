package com.docente.gestionnotas.ui.controller;

import com.docente.gestionnotas.service.NucleoPedagogicoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Controlador de UI para gestionar Núcleos Pedagógicos.
 * Maneja las operaciones de eliminación de núcleos.
 */
@Controller
@RequestMapping("/ui/nucleos")
public class NucleoUIController {

    private final NucleoPedagogicoService nucleoService;

    public NucleoUIController(NucleoPedagogicoService nucleoService) {
        this.nucleoService = nucleoService;
    }

    /**
     * Elimina un núcleo pedagógico y todas sus notas asociadas.
     * POST /ui/nucleos/eliminar/{nucleoId}
     */
    @PostMapping("/eliminar/{nucleoId}")
    public String eliminarNucleo(
            @PathVariable Long nucleoId,
            @RequestParam Long cursoId,
            RedirectAttributes redirectAttributes) {

        try {
            nucleoService.deleteById(nucleoId);
            redirectAttributes.addFlashAttribute("success",
                    "Núcleo pedagógico eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error: El núcleo no existe.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar el núcleo: " + e.getMessage());
        }

        return "redirect:/ui/cursos/" + cursoId + "/detalles";
    }
}