package com.docente.gestionnotas.service;

import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    // --- Métodos CRUD Básicos ---

    @Transactional
    public Curso save(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Transactional(readOnly = true)
    public Curso findById(Long id) {
        // Usamos orElseThrow para lanzar una excepción si el Optional está vacío
        return cursoRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Curso no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    // --- Lógica de Negocio Específica ---

    /**
     * Calcula el promedio de notas dentro de un núcleo pedagógico específico.
     * Regla de Negocio: Las notas son del 1 al 10.
     */
    @Transactional(readOnly = true)
    public double calcularPromedioNucleo(Long cursoId, Long nucleoId) {
        Curso curso = findById(cursoId);

        NucleoPedagogico nucleo = curso.getNucleos().stream()
                .filter(n -> n.getId().equals(nucleoId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Núcleo no encontrado en el curso " + cursoId));

        if (nucleo.getNotas() == null || nucleo.getNotas().isEmpty()) {
            return 0.0;
        }

        OptionalDouble promedio = nucleo.getNotas().stream()
                .mapToInt(Nota::getValor)
                .average();

        return promedio.orElse(0.0);
    }

    @Transactional
    public void deleteById(Long id) {
        Curso curso = findById(id); // Usa findById para asegurar que exista

        // La eliminación en cascada de los Núcleos/Notas ya se maneja por la anotación @OneToMany.

        // Lo más importante: Para la relación M:M, si Curso NO es el dueño,
        // la tabla intermedia (alumno_curso) debe actualizarse.
        // JPA debería manejar esto automáticamente, pero para garantizar que los alumnos
        // no queden con una referencia rota, vaciaremos la lista de alumnos del curso antes de eliminarlo (medida de seguridad).
        curso.getAlumnos().clear();
        cursoRepository.save(curso); // Persiste el cambio de la relación

        cursoRepository.delete(curso);
    }
}