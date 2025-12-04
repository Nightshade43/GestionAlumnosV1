package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Alumno.
 * Un alumno puede estar inscrito en múltiples cursos.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {

    @Id
    @NotBlank(message = "El ID es obligatorio")
    @Size(max = 50, message = "El ID no puede exceder 50 caracteres")
    @Column(nullable = false, unique = true)
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String apellido;

    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    // Relación Muchos a Muchos: Un alumno está en múltiples cursos
    @ManyToMany
    @JoinTable(
            name = "alumno_curso",
            joinColumns = @JoinColumn(name = "alumno_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos = new ArrayList<>();

    /**
     * Añade un curso a la lista de cursos del alumno.
     * Mantiene la bidireccionalidad de la relación.
     */
    public void addCurso(Curso curso) {
        if (cursos == null) {
            cursos = new ArrayList<>();
        }
        if (!cursos.contains(curso)) {
            cursos.add(curso);
            if (!curso.getAlumnos().contains(this)) {
                curso.getAlumnos().add(this);
            }
        }
    }

    /**
     * Remueve un curso de la lista de cursos del alumno.
     * Mantiene la bidireccionalidad de la relación.
     */
    public void removeCurso(Curso curso) {
        if (cursos != null) {
            cursos.remove(curso);
            if (curso.getAlumnos().contains(this)) {
                curso.getAlumnos().remove(this);
            }
        }
    }

    /**
     * Obtiene el nombre completo del alumno.
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}