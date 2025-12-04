package com.docente.gestionnotas;

import com.docente.gestionnotas.model.Alumno;
import com.docente.gestionnotas.model.Curso;
import com.docente.gestionnotas.model.Nota;
import com.docente.gestionnotas.model.NucleoPedagogico;
import com.docente.gestionnotas.service.AlumnoService;
import com.docente.gestionnotas.service.CursoService;
import com.docente.gestionnotas.service.NotaService;
import com.docente.gestionnotas.service.NucleoPedagogicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para la aplicación Gestión de Alumnos.
 * Estas pruebas verifican que los servicios funcionen correctamente
 * en conjunto con la base de datos.
 */
@SpringBootTest
@Transactional // Hace rollback después de cada test
class GestionAlumnosApplicationTests {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private NucleoPedagogicoService nucleoService;

    @Autowired
    private NotaService notaService;

    /**
     * Verifica que el contexto de Spring se cargue correctamente.
     */
    @Test
    void contextLoads() {
        assertNotNull(alumnoService);
        assertNotNull(cursoService);
        assertNotNull(nucleoService);
        assertNotNull(notaService);
    }

    /**
     * Prueba básica de CRUD de Alumno.
     */
    @Test
    void cuandoGuardarAlumno_entoncesSeGuardaCorrectamente() {
        // Arrange
        Alumno alumno = new Alumno();
        alumno.setId("TEST-001");
        alumno.setNombre("Juan");
        alumno.setApellido("Pérez");
        alumno.setEmail("juan.perez@test.com");

        // Act
        Alumno guardado = alumnoService.save(alumno);

        // Assert
        assertNotNull(guardado);
        assertEquals("TEST-001", guardado.getId());
        assertEquals("Juan", guardado.getNombre());
        assertEquals("Pérez", guardado.getApellido());
    }

    /**
     * Prueba de búsqueda de alumno por ID.
     */
    @Test
    void cuandoBuscarAlumnoPorId_entoncesLoEncuentra() {
        // Arrange
        Alumno alumno = new Alumno();
        alumno.setId("TEST-002");
        alumno.setNombre("María");
        alumno.setApellido("González");
        alumnoService.save(alumno);

        // Act
        Alumno encontrado = alumnoService.findById("TEST-002");

        // Assert
        assertNotNull(encontrado);
        assertEquals("María", encontrado.getNombre());
    }

    /**
     * Prueba de error al buscar alumno inexistente.
     */
    @Test
    void cuandoBuscarAlumnoInexistente_entoncesLanzaExcepcion() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            alumnoService.findById("NO-EXISTE");
        });
    }

    /**
     * Prueba de creación de Curso.
     */
    @Test
    void cuandoGuardarCurso_entoncesSeGuardaCorrectamente() {
        // Arrange
        Curso curso = new Curso();
        curso.setNombreMateria("Matemática");
        curso.setAnio(1);
        curso.setDivision("A");

        // Act
        Curso guardado = cursoService.save(curso);

        // Assert
        assertNotNull(guardado);
        assertNotNull(guardado.getId());
        assertEquals("Matemática - 1º A", guardado.getNombreCompleto());
    }

    /**
     * Prueba de inscripción de alumno a curso.
     */
    @Test
    void cuandoInscribirAlumnoACurso_entoncesSeRelacionanCorrectamente() {
        // Arrange
        Alumno alumno = new Alumno();
        alumno.setId("TEST-003");
        alumno.setNombre("Carlos");
        alumno.setApellido("Ramírez");
        alumnoService.save(alumno);

        Curso curso = new Curso();
        curso.setNombreMateria("Física");
        curso.setAnio(2);
        curso.setDivision("B");
        Curso cursoGuardado = cursoService.save(curso);

        // Act
        Alumno alumnoActualizado = alumnoService.inscribirAlumnoACurso("TEST-003", cursoGuardado.getId());

        // Assert
        assertNotNull(alumnoActualizado.getCursos());
        assertFalse(alumnoActualizado.getCursos().isEmpty());
        assertEquals(1, alumnoActualizado.getCursos().size());
        assertEquals("Física - 2º B", alumnoActualizado.getCursos().get(0).getNombreCompleto());
    }

    /**
     * Prueba de creación de núcleo pedagógico.
     */
    @Test
    void cuandoCrearNucleo_entoncesSeAsociaAlCurso() {
        // Arrange
        Curso curso = new Curso();
        curso.setNombreMateria("Química");
        curso.setAnio(3);
        curso.setDivision("C");
        Curso cursoGuardado = cursoService.save(curso);

        NucleoPedagogico nucleo = new NucleoPedagogico();
        nucleo.setTema("Tabla Periódica");
        nucleo.setDescripcion("Estudio de los elementos químicos");

        // Act
        Curso cursoActualizado = nucleoService.crearNucleo(cursoGuardado.getId(), nucleo);

        // Assert
        assertNotNull(cursoActualizado.getNucleos());
        assertEquals(1, cursoActualizado.getNucleos().size());
        assertEquals("Tabla Periódica", cursoActualizado.getNucleos().get(0).getTema());
    }

    /**
     * Prueba de validación de nota fuera de rango.
     */
    @Test
    void cuandoAgregarNotaInvalida_entoncesLanzaExcepcion() {
        // Arrange
        Curso curso = new Curso();
        curso.setNombreMateria("Historia");
        curso.setAnio(1);
        curso.setDivision("D");
        Curso cursoGuardado = cursoService.save(curso);

        NucleoPedagogico nucleo = new NucleoPedagogico();
        nucleo.setTema("Revolución Francesa");
        Curso cursoConNucleo = nucleoService.crearNucleo(cursoGuardado.getId(), nucleo);
        Long nucleoId = cursoConNucleo.getNucleos().get(0).getId();

        Nota notaInvalida = new Nota();
        notaInvalida.setValor(15); // Valor fuera de rango
        notaInvalida.setDescripcion("Examen");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            notaService.agregarNotaANucleo(nucleoId, notaInvalida);
        });
    }

    /**
     * Prueba de agregar nota válida.
     */
    @Test
    void cuandoAgregarNotaValida_entoncesSeGuardaCorrectamente() {
        // Arrange
        Curso curso = new Curso();
        curso.setNombreMateria("Literatura");
        curso.setAnio(2);
        curso.setDivision("E");
        Curso cursoGuardado = cursoService.save(curso);

        NucleoPedagogico nucleo = new NucleoPedagogico();
        nucleo.setTema("Poesía Romántica");
        Curso cursoConNucleo = nucleoService.crearNucleo(cursoGuardado.getId(), nucleo);
        Long nucleoId = cursoConNucleo.getNucleos().get(0).getId();

        Nota nota = new Nota();
        nota.setValor(8);
        nota.setDescripcion("Trabajo Práctico");

        // Act
        NucleoPedagogico nucleoActualizado = notaService.agregarNotaANucleo(nucleoId, nota);

        // Assert
        assertNotNull(nucleoActualizado.getNotas());
        assertEquals(1, nucleoActualizado.getNotas().size());
        assertEquals(8, nucleoActualizado.getNotas().get(0).getValor());
    }

    /**
     * Prueba de eliminación de alumno.
     */
    @Test
    void cuandoEliminarAlumno_entoncesSeEliminaCorrectamente() {
        // Arrange
        Alumno alumno = new Alumno();
        alumno.setId("TEST-004");
        alumno.setNombre("Ana");
        alumno.setApellido("López");
        alumnoService.save(alumno);

        // Act
        alumnoService.deleteById("TEST-004");

        // Assert
        assertThrows(NoSuchElementException.class, () -> {
            alumnoService.findById("TEST-004");
        });
    }
}