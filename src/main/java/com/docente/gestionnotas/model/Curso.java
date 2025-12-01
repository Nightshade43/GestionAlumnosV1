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
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ejemplo: "Informática I - 1º A"
    @Column(unique = true, nullable = false)
    private String nombreCompleto;

    private String nombreMateria;
    private int anio;
    private String division;

    // Relación Uno a Muchos: Un curso tiene muchos núcleos pedagógicos.
    @OneToMany(
            mappedBy = "curso",
            cascade = CascadeType.ALL, // <--- Esto permite guardar el padre y al hijo
            orphanRemoval = true
    )
    private List<NucleoPedagogico> nucleos;

    // Relación Muchos a Muchos con Alumno (Mapeado en la clase Alumno)
    @ManyToMany(mappedBy = "cursos")
    private List<Alumno> alumnos;

    public void addNucleo(NucleoPedagogico nucleo) {
        if (nucleos == null) {
            nucleos = new java.util.ArrayList<>();
        }
        nucleos.add(nucleo);
        nucleo.setCurso(this); // Bidireccionalidad
    }
}