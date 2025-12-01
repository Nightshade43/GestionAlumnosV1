package com.docente.gestionnotas.repository;

import com.docente.gestionnotas.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Buscar curso por el nombre completo (ej: "Informática I - 1º A")
    Curso findByNombreCompleto(String nombreCompleto);
}