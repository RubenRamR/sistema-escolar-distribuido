# sistema-escolar-distribuido

Este repositorio contiene la Prueba de Concepto (PoC) para el caso de estudio "Sistema Escuela", desarrollado para la asignatura de Sistemas Distribuidos. El objetivo principal es demostrar la viabilidad de las comunicaciones en un entorno distribuido, cumpliendo con los requerimientos de sincronización, consulta y sistemas de alertas estipulados en el diseño arquitectónico.

## Arquitectura de la Solución

El sistema implementa un enfoque de integración distribuida mediante tres nodos principales independientes:

1. Mock Moodle (Node.js - Puerto 9090): Actúa como el sistema de legado proveedor de datos. Expone un endpoint REST para proveer el estatus académico y las calificaciones estáticas de los alumnos.
2. Servidor Central / API Gateway (Spring Boot - Puerto 8080): Nodo intermediario y núcleo de la lógica de negocio. Consume el Mock de Moodle mediante peticiones síncronas (HTTP/REST) e implementa un patrón observador (WebSockets) para evaluar el rendimiento y notificar eventos asíncronos.
3. Cliente Web (HTML/JS/CSS): Interfaz de usuario ligera (simulador de la App para padres) que consume el API Gateway vía fetch y mantiene una conexión persistente bidireccional para recibir alertas en tiempo real.

## Requisitos Previos

Para ejecutar este proyecto en un entorno local, es necesario contar con el siguiente software instalado:

- Node.js (v14 o superior)
- Java Development Kit (JDK) 21
- Maven (Integrado en el proyecto mediante el Wrapper de Spring Boot)
- Navegador Web moderno (Chrome, Edge, Firefox)

## Instrucciones de Ejecución

Para levantar el entorno distribuido correctamente, los nodos deben inicializarse en el siguiente orden para evitar errores de conexión:

### Paso 1: Iniciar el Mock de Moodle (Proceso en Segundo Plano)

Este proceso simula el sistema externo de calificaciones.

1. Abre una terminal y navega al directorio del mock:
   `cd mocks`
2. Instala las dependencias necesarias:
   `npm install`
3. Ejecuta el servidor:
   `node moodle_mock.js`

El servidor indicará en la consola que está escuchando en `http://localhost:9090`. Mantenlo en ejecución.

### Paso 2: Iniciar el Servidor Central (Lógica y API Gateway)

Este proceso levanta la aplicación Java y el servidor web embebido.

1. Abre una nueva pestaña en la terminal y navega al directorio del servidor central:
   `cd servidor-central`
2. Ejecuta la aplicación utilizando el Wrapper de Maven:
   En Windows:
   `mvnw.cmd spring-boot:run`
   En macOS/Linux:
   `./mvnw spring-boot:run`

El servidor Tomcat se inicializará y la consola mostrará que está corriendo en el puerto `8080`. Mantenlo en ejecución.

### Paso 3: Ejecutar el Cliente Web (Interfaz de Usuario)

1. Utiliza el explorador de archivos de tu sistema operativo para navegar a la carpeta `cliente-web`.
2. Haz doble clic en el archivo `index.html` para abrirlo directamente en tu navegador web predeterminado (la URL mostrará la ruta local del archivo, ej. `file:///.../index.html`).
3. Abre las herramientas de desarrollador del navegador (F12) en la pestaña "Consola" para verificar la conexión exitosa del WebSocket.
4. Haz clic en el botón "Actualizar Calificaciones" para probar el flujo de comunicación distribuida completo.

## Protocolos de Comunicación Implementados

- REST / JSON: Utilizado para la comunicación síncrona en el flujo de consulta (Cliente -> Servidor Central -> Mock Moodle). Elegido por su bajo acoplamiento y estandarización en servicios web.
- WebSockets: Utilizado para la comunicación asíncrona (Push) desde el Servidor Central hacia el Cliente. Elegido para implementar el patrón observador y enviar alertas de bajo rendimiento en tiempo real sin requerir peticiones continuas (polling) por parte del cliente.
