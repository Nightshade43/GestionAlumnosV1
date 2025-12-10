package com.docente.gestionnotas.service;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.repository.AlumnoRepository;
import com.docente.gestionnotas.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;

    // Inyección de dependencias por constructor
    public AlumnoService(AlumnoRepository alumnoRepository, CursoRepository cursoRepository) {
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
    }

    // --- Métodos CRUD Básicos ---

    @Transactional(readOnly = true)
    public List<Alumno> findAll() {
        return alumnoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Alumno findById(Long id) {
        Optional<Alumno> optionalAlumno = alumnoRepository.findById(id);

        if (optionalAlumno.isEmpty()) { // o isPresent() si usas una versión anterior a Java 11
            throw new NoSuchElementException("Alumno con ID " + id + " no encontrado.");
        }

        return optionalAlumno.get();
    }

    @Transactional
    public Alumno save(Alumno alumno) {
        // Regla de Negocio: Validar que el ID sea único antes de guardar (si no es autogenerado)
        if (alumnoRepository.existsById(alumno.getDni()) && alumnoRepository.findById(alumno.getDni()).isEmpty()) {
            throw new IllegalArgumentException("Ya existe un alumno con el DNI: " + alumno.getDni());
        }
        return alumnoRepository.save(alumno);
    }

    // --- Lógica de Negocio Específica ---

    /**
     * Inscribe un alumno existente a un curso existente.
     * Maneja la relación Muchos a Muchos.
     */
    @Transactional
    public Alumno inscribirAlumnoACurso(Long alumnoId, Long cursoId) {
        Alumno alumno = findById(alumnoId);

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException("Curso con ID " + cursoId + " no encontrado."));

        // 1. Agregar el curso a la lista de cursos del alumno
        if (!alumno.getCursos().contains(curso)) {
            alumno.getCursos().add(curso);
        }

        // 2. (Opcional, para mantener consistencia bidireccional)
        //    Agregar el alumno a la lista de alumnos del curso
        if (!curso.getAlumnos().contains(alumno)) {
            curso.getAlumnos().add(alumno);
            cursoRepository.save(curso);
        }

        // El @Transactional guarda automáticamente los cambios en 'alumno'
        return alumnoRepository.save(alumno);
    }

    /**
     * Desinscribe a un alumno de un curso existente.
     * Maneja la relación Muchos a Muchos.
     */
    @Transactional
    public void desinscribirAlumnoDeCurso(Long alumnoId, Long cursoId) {
        Alumno alumno = findById(alumnoId);

        // El curso NO necesita ser buscado si solo modificamos la lista del alumno,
        // pero es buena práctica para validar que el curso exista.
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException("Curso con ID " + cursoId + " no encontrado."));

        // 1. Remover el curso de la lista de cursos del alumno.
        boolean cursoRemovido = alumno.getCursos().removeIf(c -> c.getId().equals(cursoId));

        if (!cursoRemovido) {
            throw new IllegalArgumentException("El alumno no está inscrito en el curso especificado.");
        }

        // 2. (Opcional, para mantener consistencia bidireccional)
        //    Remover el alumno de la lista de alumnos del curso.
        curso.getAlumnos().removeIf(a -> false);
        cursoRepository.save(curso);

        // 3. Persistir el cambio en el alumno.
        alumnoRepository.save(alumno);
    }

    @Transactional
    public void deleteById(String id) {
        if (!alumnoRepository.existsById(id)) {
            throw new NoSuchElementException("Alumno con ID " + id + " no encontrado.");
        }
        // Debido a que 'Alumno' es la entidad dueña de la relación M:M,
        // al eliminar el alumno, las entradas correspondientes en 'alumno_curso' se eliminan.
        alumnoRepository.deleteById(id);
    }
}