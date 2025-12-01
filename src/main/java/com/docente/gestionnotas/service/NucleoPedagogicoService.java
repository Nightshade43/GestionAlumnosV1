package com.docente.gestionnotas.service;

import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.repository.CursoRepository;
import com.docente.gestionnotas.repository.NucleoPedagogicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
public class NucleoPedagogicoService {

    public final NucleoPedagogicoRepository nucleoRepository;
    private final CursoService cursoService;
    private final CursoRepository cursoRepository;


    public NucleoPedagogicoService(NucleoPedagogicoRepository nucleoRepository, CursoService cursoService, CursoRepository cursoRepository) {
        this.nucleoRepository = nucleoRepository;
        this.cursoService = cursoService; // Usamos CursoService para buscar y guardar el Curso
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public NucleoPedagogico findById(Long id) {
        return nucleoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Núcleo Pedagógico con ID " + id + " no encontrado."));
    }

    /**
     * Crea un nuevo núcleo pedagógico y lo añade a un curso existente.
     */
    @Transactional
    public Curso agregarNucleoACurso(Long cursoId, NucleoPedagogico nucleo) {
        Curso curso = cursoService.findById(cursoId);

        // 1. Guardar el nuevo núcleo para que tenga un ID asignado.
        NucleoPedagogico nuevoNucleo = nucleoRepository.save(nucleo);

        // 2. Añadir el núcleo a la lista del curso.
        if (curso.getNucleos() == null) {
            // Esto solo debería pasar si la lista no fue inicializada en el modelo
            // pero es una buena práctica de seguridad.
            curso.setNucleos(new java.util.ArrayList<>());
        }

        curso.getNucleos().add(nuevoNucleo);

        // 3. Guardar el curso, lo que persiste la relación.
        return cursoService.save(curso);
    }

    @Transactional
    public void deleteById(Long id) {
        // La eliminación de un núcleo debería propagarse (CASCADE) a sus notas.
        nucleoRepository.deleteById(id);
    }

    // ¡CRÍTICO! Esto mantiene la sesión de Hibernate activa y gestiona las entidades
    @Transactional
    public Curso crearNucleo(Long cursoId, NucleoPedagogico nuevoNucleo) {

        // 1. Encontrar el Curso. (Este objeto está AHORA ATTACHED a la sesión)
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException("Curso no encontrado con ID: " + cursoId));

        // 2. Establecer la bidireccionalidad (Corrección anterior)
        curso.addNucleo(nuevoNucleo);
        // Recuerda que curso.addNucleo(nuevoNucleo) debe llamar a nuevoNucleo.setCurso(this);

        // 3. Persistir el Curso. Esto automáticamente guarda el nuevo Nucleo
        // gracias a CascadeType.ALL.
        return cursoRepository.save(curso);
    }
}