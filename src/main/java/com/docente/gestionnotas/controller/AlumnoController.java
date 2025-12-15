package com.docente.gestionnotas.controller;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    // GET /api/alumnos
    @GetMapping
    public List<Alumno> getAllAlumnos() {
        return alumnoService.findAll();
    }

    // GET /api/alumnos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable Long id) {
        try {
            Alumno alumno = alumnoService.findById(id);
            return ResponseEntity.ok(alumno);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // POST /api/alumnos
    @PostMapping
    public ResponseEntity<Alumno> createAlumno(@RequestBody Alumno alumno) {
        try {
            Alumno nuevoAlumno = alumnoService.save(alumno);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAlumno); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }

    // POST /api/alumnos/{alumnoId}/inscribir/{cursoId}
    @PostMapping("/{alumnoId}/inscribir/{cursoId}")
    public ResponseEntity<Alumno> inscribirAlumno(@PathVariable long alumnoId, @PathVariable Long cursoId) {
        try {
            Alumno alumnoActualizado = alumnoService.inscribirAlumnoACurso(alumnoId, cursoId);
            return ResponseEntity.ok(alumnoActualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}