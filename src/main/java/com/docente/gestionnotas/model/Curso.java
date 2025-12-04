package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Curso.
 * Un curso agrupa múltiples núcleos pedagógicos y puede tener varios alumnos inscritos.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Column(unique = true, nullable = false)
    private String nombreCompleto;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre de la materia debe tener entre 2 y 100 caracteres")
    private String nombreMateria;

    @Min(value = 1, message = "El año debe ser mayor a 0")
    private int anio;

    @NotBlank(message = "La división es obligatoria")
    @Size(min = 1, max = 10, message = "La división debe tener entre 1 y 10 caracteres")
    private String division;

    // Relación Uno a Muchos: Un curso tiene muchos núcleos pedagógicos
    @OneToMany(
            mappedBy = "curso",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NucleoPedagogico> nucleos = new ArrayList<>();

    // Relación Muchos a Muchos con Alumno (Mapeado en la clase Alumno)
    @ManyToMany(mappedBy = "cursos")
    private List<Alumno> alumnos = new ArrayList<>();

    /**
     * Añade un núcleo pedagógico al curso.
     * Mantiene la bidireccionalidad de la relación.
     */
    public void addNucleo(NucleoPedagogico nucleo) {
        if (nucleos == null) {
            nucleos = new ArrayList<>();
        }
        nucleos.add(nucleo);
        nucleo.setCurso(this);
    }

    /**
     * Remueve un núcleo pedagógico del curso.
     * Mantiene la bidireccionalidad de la relación.
     */
    public void removeNucleo(NucleoPedagogico nucleo) {
        if (nucleos != null) {
            nucleos.remove(nucleo);
            nucleo.setCurso(null);
        }
    }

    /**
     * Genera automáticamente el nombre completo antes de persistir o actualizar.
     * Ejemplo: "Informática I - 1º A"
     */
    @PrePersist
    @PreUpdate
    private void generarNombreCompleto() {
        if (nombreMateria != null && division != null) {
            this.nombreCompleto = nombreMateria + " - " + anio + "º " + division;
        }
    }
}