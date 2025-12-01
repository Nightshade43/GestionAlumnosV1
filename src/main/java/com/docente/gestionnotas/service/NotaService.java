package com.docente.gestionnotas.service;

import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final NucleoPedagogicoService nucleoService;

    public NotaService(NotaRepository notaRepository, NucleoPedagogicoService nucleoService) {
        this.notaRepository = notaRepository;
        this.nucleoService = nucleoService;
    }

    /**
     * Agrega una nota a un núcleo pedagógico existente.
     */
    @Transactional
    public NucleoPedagogico agregarNotaANucleo(Long nucleoId, Nota nota) {
        NucleoPedagogico nucleo = nucleoService.findById(nucleoId);

        // Regla de Negocio: La nota debe estar en el rango de 1 a 10.
        if (nota.getValor() < 1 || nota.getValor() > 10) {
            throw new IllegalArgumentException("El valor de la nota debe estar entre 1 y 10.");
        }

        // 1. Guardar la nota para asignarle un ID.
        Nota nuevaNota = notaRepository.save(nota);

        // 2. Añadir la nota a la lista del núcleo.
        List<Nota> notas = nucleo.getNotas();
        if (notas == null) {
            notas = new java.util.ArrayList<>();
            nucleo.setNotas(notas);
        }
        notas.add(nuevaNota);

        // 3. Guardar el núcleo (lo que persiste la relación Uno a Muchos).
        // Nota: Si el NucleoPedagogico ya tiene un CASCADE.ALL con Curso,
        // y Nota tiene un CASCADE.ALL con Nucleo, esto podría ser redundante,
        // pero asegura que la referencia sea persistida si no hay un CASCADE completo.
        return nucleoService.save(nucleo);
    }

    @Transactional
    public void deleteById(Long id) {
        notaRepository.deleteById(id);
    }
}