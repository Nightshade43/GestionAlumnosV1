# 🎓 Sistema de Gestión de Alumnos

Sistema completo para la gestión académica de alumnos, cursos, núcleos pedagógicos y notas. Desarrollado con Spring Boot 3.5.8 y Java 21.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Uso](#-uso)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API REST](#-api-rest)
- [Base de Datos](#️-base-de-datos)
- [Contribuir](#-contribuir)
- [Roadmap](#-roadmap)
- [Autor](#-autor)
- [Licencia](#-licencia)

---

## 📝 Descripción

El **Sistema de Gestión de Alumnos** es una aplicación web desarrollada para instituciones educativas que permite:

- 👥 Gestionar información de alumnos (datos personales, inscripciones)
- 📚 Administrar cursos y divisiones
- 📖 Crear núcleos pedagógicos (temas/unidades de enseñanza)
- 📊 Registrar y visualizar notas de los alumnos
- 📈 Calcular promedios automáticamente
- 🔍 Buscar y filtrar información de manera eficiente

El sistema está diseñado siguiendo el patrón **MVC** (Model-View-Controller) y ofrece tanto una **interfaz web** amigable como una **API REST** para integraciones.

---

## ✨ Características

### Gestión de Alumnos
- ✅ Registro completo de datos personales (nombre, apellido, DNI, email, fecha de nacimiento, dirección)
- ✅ Validación de datos con Bean Validation
- ✅ Inscripción múltiple a cursos
- ✅ Vista de historial académico
- ✅ Edición y eliminación de alumnos

### Gestión de Cursos
- ✅ Creación de cursos por materia, año y división
- ✅ Generación automática de nombre completo (ej: "Matemática - 4º A")
- ✅ Administración de alumnos inscritos
- ✅ Gestión de núcleos pedagógicos por curso

### Núcleos Pedagógicos
- ✅ Organización de contenidos por temas
- ✅ Registro de múltiples notas por núcleo
- ✅ Cálculo automático de promedios
- ✅ Descripción detallada de cada unidad

### Sistema de Notas
- ✅ Registro de notas del 1 al 10
- ✅ Descripción de cada evaluación
- ✅ Cálculo de promedios por núcleo
- ✅ Validación de rangos de notas

### Interfaz de Usuario
- ✅ Diseño responsive con Bootstrap 5
- ✅ Dashboard principal con acceso rápido
- ✅ Mensajes de éxito/error con feedback visual
- ✅ Formularios con validación en tiempo real
- ✅ Modales de confirmación para acciones críticas

---

## 🛠️ Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.8** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - API REST y controladores MVC
- **Hibernate** - ORM (Object-Relational Mapping)
- **Bean Validation** - Validación de datos
- **Lombok** - Reducción de código boilerplate

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5.3** - Framework CSS
- **HTML5 / CSS3** - Maquetado y estilos
- **JavaScript** - Interactividad del cliente

### Base de Datos
- **H2 Database** - Base de datos en memoria/archivo (desarrollo)
- Compatible con **PostgreSQL** y **MySQL** (producción)

### Herramientas de Desarrollo
- **Maven** - Gestión de dependencias
- **Git** - Control de versiones
- **Spring Boot DevTools** - Hot reload en desarrollo

---

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ☕ **Java 21 JDK** o superior
  ```bash
  java -version
  ```
  
- 📦 **Maven 3.8+** (opcional, el proyecto incluye Maven Wrapper)
  ```bash
  mvn -version
  ```

- 🔧 **Git** para clonar el repositorio
  ```bash
  git --version
  ```

- 💻 **IDE recomendado:** IntelliJ IDEA, Eclipse o VS Code con extensiones de Java

---

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Nightshade43/GestionAlumnosV1.git
cd GestionAlumnosV1
```

### 2. Compilar el Proyecto

**Usando Maven Wrapper (recomendado):**

```bash
# En Linux/Mac
./mvnw clean install

# En Windows
mvnw.cmd clean install
```

**Usando Maven instalado globalmente:**

```bash
mvn clean install
```

### 3. Ejecutar la Aplicación

```bash
# Con Maven Wrapper
./mvnw spring-boot:run

# Con Maven global
mvn spring-boot:run

# O ejecutar el JAR generado
java -jar target/Gestion-Alumnos-0.0.1-SNAPSHOT.jar
```

### 4. Acceder a la Aplicación

Abre tu navegador y visita:

```
http://localhost:8080
```

---

## ⚙️ Configuración

### Archivo `application.properties`

El archivo principal de configuración se encuentra en `src/main/resources/application.properties`.

#### Configuración de Base de Datos

**Modo Desarrollo (Base de datos en archivo):**
```properties
spring.datasource.url=jdbc:h2:file:./data/gestiondb
spring.jpa.hibernate.ddl-auto=update
```

**Modo Testing (Base de datos en memoria):**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Consola H2 Database

Accede a la consola web de H2 para visualizar y gestionar la base de datos:

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/gestiondb
Usuario: sa
Password: password
```

#### Configuración del Servidor

```properties
server.port=8080
spring.thymeleaf.cache=false  # Desactivar caché en desarrollo
logging.level.root=INFO
```

---

## 💡 Uso

### 1. Dashboard Principal

Al iniciar la aplicación, verás el dashboard con dos opciones principales:

- **📚 Cursos y Notas:** Administra cursos, núcleos pedagógicos y notas
- **👥 Alumnos:** Gestiona la información de los estudiantes

### 2. Gestión de Alumnos

#### Crear un Alumno
1. Navega a **Alumnos → Crear Nuevo Alumno**
2. Completa el formulario con los datos personales
3. Campos obligatorios: Nombre, Apellido, DNI, Email
4. Haz clic en **Guardar Alumno**

#### Ver Detalles de un Alumno
1. En la lista de alumnos, haz clic en **Ver Detalles**
2. Visualiza información personal y cursos inscritos
3. Desde aquí puedes:
   - Matricular al alumno en nuevos cursos
   - Editar información personal
   - Eliminar al alumno

### 3. Gestión de Cursos

#### Crear un Curso
1. Navega a **Cursos → Crear Nuevo Curso**
2. Ingresa:
   - Nombre de la materia (ej: "Matemática")
   - Año (ej: 4)
   - División (ej: "A")
   - Descripción (opcional)
3. El sistema generará automáticamente el nombre completo: "Matemática - 4º A"

#### Agregar Núcleos Pedagógicos
1. Desde los detalles del curso, haz clic en **Añadir Núcleo Pedagógico**
2. Ingresa el tema principal y descripción
3. Guarda el núcleo

#### Registrar Notas
1. En los detalles del curso, dentro de cada núcleo pedagógico
2. Completa el formulario de nueva nota:
   - Valor (1-10)
   - Descripción de la evaluación
3. El sistema calculará automáticamente el promedio

### 4. Inscripción de Alumnos a Cursos

**Opción 1 - Desde el Alumno:**
1. Ve a **Alumnos → Detalles del Alumno**
2. Haz clic en **Matricular a Curso**
3. Selecciona el curso en el modal
4. Confirma la inscripción

**Opción 2 - Desde el Curso:**
1. Ve a **Cursos → Detalles del Curso**
2. Haz clic en **Inscribir Alumno**
3. Selecciona al alumno disponible
4. Confirma la inscripción

---

## 📁 Estructura del Proyecto

```
GestionAlumnosV1/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/docente/gestionnotas/
│   │   │       ├── controller/          # Controladores REST
│   │   │       │   ├── AlumnoController.java
│   │   │       │   ├── CursoController.java
│   │   │       │   ├── NotaController.java
│   │   │       │   └── NucleoPedagogicoController.java
│   │   │       ├── ui/
│   │   │       │   └── controller/      # Controladores MVC
│   │   │       │       ├── AlumnoUIController.java
│   │   │       │       ├── CursoUIController.java
│   │   │       │       ├── MainController.java
│   │   │       │       ├── NotaUIController.java
│   │   │       │       └── NucleoUIController.java
│   │   │       ├── model/               # Entidades JPA
│   │   │       │   ├── Alumno.java
│   │   │       │   ├── Curso.java
│   │   │       │   ├── Nota.java
│   │   │       │   └── NucleoPedagogico.java
│   │   │       ├── repository/          # Repositorios
│   │   │       │   ├── AlumnoRepository.java
│   │   │       │   ├── CursoRepository.java
│   │   │       │   ├── NotaRepository.java
│   │   │       │   └── NucleoPedagogicoRepository.java
│   │   │       ├── service/             # Capa de negocio
│   │   │       │   ├── AlumnoService.java
│   │   │       │   ├── CursoService.java
│   │   │       │   ├── NotaService.java
│   │   │       │   └── NucleoPedagogicoService.java
│   │   │       └── GestionAlumnosApplication.java
│   │   └── resources/
│   │       ├── templates/               # Vistas Thymeleaf
│   │       │   ├── alumnos/
│   │       │   │   ├── crear.html
│   │       │   │   ├── detalles.html
│   │       │   │   └── lista.html
│   │       │   ├── cursos/
│   │       │   │   ├── crear.html
│   │       │   │   ├── crear_nucleo.html
│   │       │   │   ├── detalles.html
│   │       │   │   ├── index.html
│   │       │   │   ├── inscribir_alumno.html
│   │       │   │   └── lista.html
│   │       │   └── main_dashboard.html
│   │       └── application.properties
│   └── test/                            # Tests unitarios
├── .gitignore
├── pom.xml                              # Configuración Maven
└── README.md
```

---

## 🔌 API REST

El sistema expone una API REST para integraciones externas.

### Endpoints de Alumnos

#### Obtener todos los alumnos
```http
GET /api/alumnos
```

#### Obtener alumno por ID
```http
GET /api/alumnos/{id}
```

#### Crear nuevo alumno
```http
POST /api/alumnos
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "dni": "12345678",
  "email": "juan.perez@mail.com",
  "fechaNacimiento": "2005-05-15",
  "direccion": "Calle Falsa 123"
}
```

#### Inscribir alumno a curso
```http
POST /api/alumnos/{alumnoId}/inscribir/{cursoId}
```

### Endpoints de Cursos

#### Crear nuevo curso
```http
POST /api/cursos
Content-Type: application/json

{
  "nombreMateria": "Matemática",
  "anio": 4,
  "division": "A",
  "descripcion": "Curso de matemática avanzada"
}
```

#### Obtener curso por ID
```http
GET /api/cursos/{id}
```

#### Agregar núcleo pedagógico
```http
POST /api/cursos/{cursoId}/nucleos
Content-Type: application/json

{
  "tema": "Álgebra Lineal",
  "descripcion": "Matrices y vectores"
}
```

#### Obtener promedio de núcleo
```http
GET /api/cursos/{cursoId}/promedio/{nucleoId}
```

### Endpoints de Notas

#### Agregar nota a núcleo
```http
POST /api/nucleos/{nucleoId}/notas
Content-Type: application/json

{
  "valor": 8,
  "descripcion": "Examen Parcial"
}
```

### Códigos de Respuesta

- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado exitosamente
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 🗄️ Base de Datos

### Modelo de Datos

```
┌─────────────┐         ┌──────────────┐         ┌─────────────────────┐
│   ALUMNO    │◄───────►│ ALUMNO_CURSO │────────►│       CURSO         │
├─────────────┤ N     M ├──────────────┤ M     1 ├─────────────────────┤
│ id (PK)     │         │ alumno_id(FK)│         │ id (PK)             │
│ nombre      │         │ curso_id(FK) │         │ nombreMateria       │
│ apellido    │         └──────────────┘         │ nombreCompleto      │
│ dni (UNIQUE)│                                  │ descripcion         │
│ email       │                                  │ anio                │
│ fechaNac... │                                  │ division            │
│ direccion   │                                  └─────────────────────┘
└─────────────┘                                            │ 1
                                                           │
                                                           │ N
                                              ┌────────────────────────┐
                                              │  NUCLEO_PEDAGOGICO     │
                                              ├────────────────────────┤
                                              │ id (PK)                │
                                              │ tema                   │
                                              │ descripcion            │
                                              │ curso_id (FK)          │
                                              └────────────────────────┘
                                                           │ 1
                                                           │
                                                           │ N
                                                  ┌────────────────┐
                                                  │     NOTA       │
                                                  ├────────────────┤
                                                  │ id (PK)        │
                                                  │ valor (1-10)   │
                                                  │ descripcion    │
                                                  │ nucleo_id (FK) │
                                                  └────────────────┘
```

### Relaciones

- **Alumno ↔ Curso:** Muchos a Muchos (un alumno puede estar en varios cursos, un curso tiene varios alumnos)
- **Curso ↔ NucleoPedagogico:** Uno a Muchos (un curso tiene varios núcleos, un núcleo pertenece a un curso)
- **NucleoPedagogico ↔ Nota:** Uno a Muchos (un núcleo tiene varias notas, una nota pertenece a un núcleo)

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Si deseas mejorar este proyecto:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guía de Contribución

- Sigue las convenciones de código existentes
- Escribe tests para nuevas funcionalidades
- Actualiza la documentación si es necesario
- Asegúrate de que el código compile sin errores

---

## 🗺️ Roadmap

### Versión 1.1 (Próxima)
- [ ] Implementar DTOs y Mappers
- [ ] Agregar paginación y búsqueda avanzada
- [ ] Optimización de queries (N+1)
- [ ] Tests unitarios y de integración
- [ ] Manejo centralizado de errores

### Versión 1.2
- [ ] Sistema de autenticación (Spring Security)
- [ ] Roles de usuario (Admin, Docente, Alumno)
- [ ] Dashboard con estadísticas
- [ ] Exportación a Excel/PDF
- [ ] Sistema de asistencia

### Versión 2.0
- [ ] Migración a PostgreSQL/MySQL
- [ ] API REST completa con Swagger
- [ ] Sistema de notificaciones por email
- [ ] Portal para padres
- [ ] Aplicación móvil (React Native)

---

## 👨‍💻 Autor

**Nightshade43**
- GitHub: [@Nightshade43](https://github.com/Nightshade43)
- Proyecto: [GestionAlumnosV1](https://github.com/Nightshade43/GestionAlumnosV1)

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

---

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- Bootstrap Team por el framework CSS
- Thymeleaf Team por el motor de plantillas
- Comunidad de desarrolladores Java

---

## 📞 Soporte

Si encuentras algún bug o tienes alguna sugerencia:

1. Abre un [Issue](https://github.com/Nightshade43/GestionAlumnosV1/issues)
2. Describe el problema detalladamente
3. Incluye capturas de pantalla si es posible

---

## 📚 Documentación Adicional

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Bootstrap Documentation](https://getbootstrap.com/docs/)
- [H2 Database Documentation](http://www.h2database.com/html/main.html)

---

<p align="center">
  Hecho con ❤️ por Nightshade43
</p>

<p align="center">
  ⭐ Si te gusta este proyecto, no olvides darle una estrella en GitHub
</p>