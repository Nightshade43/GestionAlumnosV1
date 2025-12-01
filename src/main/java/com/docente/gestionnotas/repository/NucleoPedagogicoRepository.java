package com.docente.gestionnotas.repository;

import com.docente.gestionnotas.model.NucleoPedagogico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NucleoPedagogicoRepository extends JpaRepository<NucleoPedagogico, Long> {
    // No necesitamos métodos personalizados por ahora, JpaRepository cubre lo básico.
}