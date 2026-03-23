Directorio de Pacientes - Consultorio Médico (JavaFX)

Este repositorio contiene el código fuente, la documentación y los avances correspondientes al Proyecto Integrador de la asignatura Programación Estructurada.

Es una aplicación de escritorio desarrollada en JavaFX diseñada para un consultorio pequeño. Su propósito principal es administrar un directorio de pacientes de forma eficiente, asegurando que la información persista (se guarde) localmente en el archivo pacientes.csv sin necesidad de bases de datos externas.

Maestro: Eliel David Rodríguez Villalobos.
Autores (Grupo 2°D):

Luis Uriel Vargas Espino.

Alan Esteban Zariñana Arizmendi.

Características Principales (CRUD y Extras)
El sistema está diseñado con validaciones estrictas y manejo de errores para garantizar la integridad de los datos.

1. Alta (Create):
   Registro de pacientes nuevos mediante un formulario validado que incluye CURP único, nombre mayor a 5 letras, edad lógica y teléfono de 10 dígitos.

2. Consulta (Read):
   Visualización dinámica de todos los pacientes en un componente TableView que se actualiza en tiempo real.

3. Actualización (Update):
   Edición de los datos de un paciente que haya sido seleccionado previamente en la interfaz.

4. Baja Lógica (Delete):
   Implementación de Estatus (Activo/Inactivo). No se borran datos físicamente, solo se cambia su estado para conservar el historial clínico.

5. Dashboard / Resumen:
   Contadores automáticos en pantalla que muestran el total de pacientes, así como cuántos están en estado Activo e Inactivo.

6. Persistencia Local:
   Los datos se almacenan en el archivo pacientes.csv. Al abrir el programa, los datos se cargan automáticamente; al realizar modificaciones, el archivo se actualiza de inmediato.

Requisitos Previos para Ejecutar
Para correr este proyecto en tu máquina local, necesitas las siguientes herramientas instaladas:

Java Development Kit (JDK): Versión 21.

Entorno de Desarrollo (IDE): Se recomienda IntelliJ IDEA o Visual Studio Code.

JavaFX SDK: Necesario para renderizar la interfaz gráfica de usuario.

Scene Builder (Opcional): Para visualizar o editar la interfaz en el archivo hello-view.fxml.

Arquitectura y Estructura del Código
El proyecto sigue las buenas prácticas de organización de archivos y separación de responsabilidades lógicas:

Modelo (Paciente.java): Define la estructura de los datos como CURP, nombre, edad, teléfono, alergias y estatus.

Repositorio (PacienteRepository.java): Encargado de la persistencia, específicamente de la lectura y escritura del archivo pacientes.csv en la carpeta data.

Servicio (PacienteService.java): Contiene la lógica de negocio y los métodos que ejecutan el CRUD.

Controlador (HelloController.java): Conecta la interfaz de usuario con la lógica de la aplicación.

Vista (hello-view.fxml): Define el diseño visual y la disposición de los elementos en la pantalla.