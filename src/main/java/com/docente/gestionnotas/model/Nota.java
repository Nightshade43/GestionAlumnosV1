package com.docente.gestionnotas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa una Nota.
 * Las notas están asociadas a un núcleo pedagógico.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El valor de la nota es obligatorio")
    @Min(value = 1, message = "La nota debe ser al menos 1")
    @Max(value = 10, message = "La nota no puede ser mayor a 10")
    @Column(nullable = false)
    private Integer valor;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    /**
     * Valida que el valor de la nota esté en el rango correcto antes de persistir.
     */
    @PrePersist
    @PreUpdate
    private void validarNota() {
        if (valor != null && (valor < 1 || valor > 10)) {
            throw new IllegalArgumentException(
                    "El valor de la nota debe estar entre 1 y 10. Valor recibido: " + valor);
        }
    }
}