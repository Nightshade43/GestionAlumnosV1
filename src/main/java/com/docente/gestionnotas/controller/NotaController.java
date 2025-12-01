package com.docente.gestionnotas.controller;

import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/nucleos")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    // POST /api/nucleos/{nucleoId}/notas
    @PostMapping("/{nucleoId}/notas")
    public ResponseEntity<NucleoPedagogico> addNotaToNucleo(@PathVariable Long nucleoId, @RequestBody Nota nota) {
        try {
            NucleoPedagogico nucleoActualizado = notaService.agregarNotaANucleo(nucleoId, nota);
            return ResponseEntity.status(HttpStatus.CREATED).body(nucleoActualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 por nota fuera de rango (1-10)
        }
    }
}