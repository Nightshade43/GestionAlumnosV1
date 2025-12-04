package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Núcleo Pedagógico.
 * Un núcleo agrupa múltiples notas y pertenece a un curso.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NucleoPedagogico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tema es obligatorio")
    @Size(min = 3, max = 200, message = "El tema debe tener entre 3 y 200 caracteres")
    private String tema;

    @Column(columnDefinition = "TEXT")
    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
    private String descripcion;

    // Relación Uno a Muchos: Un núcleo tiene muchas notas
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "nucleo_id")
    private List<Nota> notas = new ArrayList<>();

    // Relación Muchos a Uno: Un núcleo pertenece a un curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    /**
     * Añade una nota al núcleo pedagógico.
     */
    public void addNota(Nota nota) {
        if (notas == null) {
            notas = new ArrayList<>();
        }
        notas.add(nota);
    }

    /**
     * Remueve una nota del núcleo pedagógico.
     */
    public void removeNota(Nota nota) {
        if (notas != null) {
            notas.remove(nota);
        }
    }

    /**
     * Calcula el promedio de las notas de este núcleo.
     */
    public double calcularPromedio() {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }
        return notas.stream()
                .mapToInt(Nota::getValor)
                .average()
                .orElse(0.0);
    }
}