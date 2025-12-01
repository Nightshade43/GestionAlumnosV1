package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int valor; // Nota del 1 al 10

    private String descripcion; // Ej: "Examen unidad 1", "Trabajo práctico"

    // Relación Muchos a Uno implícita con NucleoPedagogico
    // Se manejará desde NucleoPedagogico (List<Nota>)
}