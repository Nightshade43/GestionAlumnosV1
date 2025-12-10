package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Esta línea es CRÍTICA
    private Long id;

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

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 7, max = 10, message = "El DNI debe tener entre 7 y 10 caracteres")
    private String dni;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimiento;

    @Size(max = 150, message = "La direccion no puede exceder 150 caracteres")
    private String direccion;

    // Relación Muchos a Muchos: Un alumno está en múltiples cursos
    @ManyToMany
    @JoinTable(
            name = "alumno_curso",
            joinColumns = @JoinColumn(name = "alumno_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos = new ArrayList<>();

    /**
     * Obtiene el nombre completo del alumno.
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}