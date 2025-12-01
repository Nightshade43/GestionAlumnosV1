package com.docente.gestionnotas.repository;

import com.docente.gestionnotas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    // La gestión de notas estará principalmente ligada a un NucleoPedagogico,
    // por lo que los métodos básicos son suficientes.
}