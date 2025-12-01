package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {

    // ID único, obligatorio, usado como clave primaria
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String email;

    // Relación Muchos a Muchos: Un alumno está en múltiples cursos.
    @ManyToMany
    @JoinTable(
            name = "alumno_curso", // Tabla de unión
            joinColumns = @JoinColumn(name = "alumno_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos;
}