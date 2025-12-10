package com.docente.gestionnotas.repository;

import com.docente.gestionnotas.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, String> {

    // Ejemplo de método de búsqueda personalizado
    // Spring Data genera automáticamente la consulta: SELECT a FROM Alumno a WHERE a.apellido = ?1
    List<Alumno> findByApellido(String apellido);

    Alumno findById(Long id);
}