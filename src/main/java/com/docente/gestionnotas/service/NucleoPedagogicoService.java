package com.docente.gestionnotas.service;

import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.repository.CursoRepository;
import com.docente.gestionnotas.repository.NucleoPedagogicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Servicio para gestionar Núcleos Pedagógicos.
 * Maneja la lógica de negocio relacionada con núcleos y su relación con cursos.
 */
@Service
public class NucleoPedagogicoService {

    // CORRECCIÓN 1: Cambio de 'public' a 'private'
    private final NucleoPedagogicoRepository nucleoRepository;
    private final CursoRepository cursoRepository;

    public NucleoPedagogicoService(
            NucleoPedagogicoRepository nucleoRepository,
            CursoRepository cursoRepository) {
        this.nucleoRepository = nucleoRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Guarda un núcleo pedagógico.
     */
    @Transactional
    public NucleoPedagogico save(NucleoPedagogico nucleo) {
        return nucleoRepository.save(nucleo);
    }

    /**
     * Busca un núcleo pedagógico por ID.
     * @throws NoSuchElementException si no existe
     */
    @Transactional(readOnly = true)
    public NucleoPedagogico findById(Long id) {
        return nucleoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Núcleo Pedagógico con ID " + id + " no encontrado."));
    }

    /**
     * Crea un nuevo núcleo pedagógico y lo asocia a un curso.
     * Este método mantiene la sesión de Hibernate activa y gestiona las entidades correctamente.
     *
     * @param cursoId ID del curso al que se añadirá el núcleo
     * @param nuevoNucleo Núcleo pedagógico a crear
     * @return Curso actualizado con el nuevo núcleo
     * @throws NoSuchElementException si el curso no existe
     */
    @Transactional
    public Curso crearNucleo(Long cursoId, NucleoPedagogico nuevoNucleo) {

        // 1. Buscar el curso (queda ATTACHED a la sesión de Hibernate)
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Curso no encontrado con ID: " + cursoId));

        // 2. Establecer la bidireccionalidad
        curso.addNucleo(nuevoNucleo);
        // Nota: curso.addNucleo() debe llamar internamente a nuevoNucleo.setCurso(this)

        // 3. Persistir el curso
        // Gracias a CascadeType.ALL, esto automáticamente guarda el nuevo núcleo
        return cursoRepository.save(curso);
    }

    /**
     * Elimina un núcleo pedagógico por ID.
     * Gracias a CascadeType.ALL, las notas asociadas también se eliminan.
     *
     * @param nucleoId ID del núcleo a eliminar
     * @throws NoSuchElementException si el núcleo no existe
     */
    @Transactional
    public void deleteById(Long nucleoId) {
        if (!nucleoRepository.existsById(nucleoId)) {
            throw new NoSuchElementException(
                    "Núcleo Pedagógico con ID " + nucleoId + " no encontrado.");
        }
        nucleoRepository.deleteById(nucleoId);
    }
}