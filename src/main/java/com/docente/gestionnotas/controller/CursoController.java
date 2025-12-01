package com.docente.gestionnotas.controller;

import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.service.CursoService;
import com.docente.gestionnotas.service.NucleoPedagogicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;
    private final NucleoPedagogicoService nucleoService;

    public CursoController(CursoService cursoService, NucleoPedagogicoService nucleoService) {
        this.cursoService = cursoService;
        this.nucleoService = nucleoService;
    }

    // POST /api/cursos
    @PostMapping
    public ResponseEntity<Curso> createCurso(@RequestBody Curso curso) {
        // Validación básica de datos obligatorios, si falta algún campo único/obligatorio, Spring lo gestiona.
        if (curso.getNombreCompleto() == null || curso.getNombreCompleto().isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
        Curso nuevoCurso = cursoService.save(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCurso);
    }

    // GET /api/cursos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Curso> getCursoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cursoService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/cursos/{cursoId}/nucleos
    @PostMapping("/{cursoId}/nucleos")
    public ResponseEntity<Curso> addNucleoToCurso(@PathVariable Long cursoId, @RequestBody NucleoPedagogico nucleo) {
        try {
            Curso cursoActualizado = nucleoService.agregarNucleoACurso(cursoId, nucleo);
            return ResponseEntity.ok(cursoActualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/cursos/{cursoId}/promedio/{nucleoId}
    @GetMapping("/{cursoId}/promedio/{nucleoId}")
    public ResponseEntity<Double> getPromedioNucleo(@PathVariable Long cursoId, @PathVariable Long nucleoId) {
        try {
            double promedio = cursoService.calcularPromedioNucleo(cursoId, nucleoId);
            return ResponseEntity.ok(promedio);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}