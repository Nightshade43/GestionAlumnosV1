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
public class NucleoPedagogico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tema;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Relación Uno a Muchos: Un núcleo tiene muchas notas.
    // CascadeType.ALL: Si se borra el núcleo, se borran sus notas asociadas.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "nucleo_id") // Clave foránea en la tabla 'Nota'
    private List<Nota> notas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id") // <--- Asegúrate que el nombre de columna es correcto
    private Curso curso;
}