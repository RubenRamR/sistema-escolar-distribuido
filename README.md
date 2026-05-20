# Sistema Escolar Distribuido

Este repositorio contiene la **Prueba de Concepto (PoC)** para el caso de estudio **"Sistema Escuela"**, desarrollado para la asignatura de **Sistemas Distribuidos**.

El objetivo principal es demostrar la viabilidad de las comunicaciones en un entorno distribuido, cumpliendo con los requerimientos de:

- Sincronización
- Consulta
- Alertas
- Seguridad estricta

Todo esto mediante una arquitectura orquestada con contenedores.

---

# Arquitectura de la Solución

El sistema implementa un enfoque de integración distribuida mediante tres contenedores backend interconectados en una red virtual aislada (`escuela-net`) y un cliente externo.

## Componentes del Sistema

### Mock Moodle (Node.js - Puerto 9090)

Actúa como el sistema de legado proveedor de datos.

Expone un endpoint REST para proveer:

- Estatus académico
- Calificaciones de los alumnos

---

### Servidor Central / API Gateway (Spring Boot - Puerto 8080)

Nodo intermediario y núcleo de la lógica de negocio.

Responsabilidades:

- Orquestar peticiones
- Validar seguridad
- Consumir servicios de Moodle
- Enviar alertas en tiempo real
- Ejecutar procesos Batch nocturnos

---

### Mock SCE - Control Escolar (Node.js - Puerto 50051)

Simula el sistema central de la secretaría académica.

Expone un servidor **gRPC** fuertemente tipado para recibir lotes masivos de calificaciones mediante un túnel cifrado.

---

### Cliente Web (HTML / JS / CSS)

Interfaz ligera de usuario (simulador de la app para padres).

Funciones principales:

- Consumir el API Gateway
- Mantener conexión persistente bidireccional
- Recibir alertas en tiempo real

---

# Requisitos Previos

Gracias a la contenedorización, no es necesario instalar lenguajes ni frameworks en la máquina local.

Solo necesitas:

- Docker y Docker Compose  
  _(Recomendado: Docker Desktop)_

- Navegador web moderno:
  - Chrome
  - Edge
  - Firefox

---

# Instrucciones de Ejecución

El levantamiento del clúster se realiza mediante un único comando que:

- Compila
- Empaqueta
- Levanta toda la red de servicios

---

## Paso 1: Levantar el Clúster Backend

Abre una terminal en la raíz del proyecto, donde se encuentra el archivo:

```bash
docker-compose.yml
```

Ejecuta el siguiente comando:

```bash
docker-compose up --build
```

Espera a que la terminal confirme que los tres servicios están iniciados y escuchando peticiones:

- `moodle-mock-1`
- `sce-mock-1`
- `servidor-central-1`

---

## Paso 2: Ejecutar el Cliente Web

1. Navega a la carpeta:

```bash
cliente-web
```

2. Haz doble clic en el archivo:

```bash
index.html
```

3. Inicia sesión con las credenciales de prueba:

```text
Usuario: padre123
Contraseña: admin
```

---

# Protocolos de Comunicación y Seguridad Implementados

Para garantizar el cumplimiento normativo de la prueba de concepto, la arquitectura fue securizada por capas dependiendo del canal de comunicación.

---

## REST / JSON + JWT (Capa de Aplicación)

Utilizado para la consulta síncrona:

```text
Cliente -> Servidor Central
```

### Motivos de elección

- Estándar ampliamente utilizado en aplicaciones web
- Arquitectura stateless
- Fácil integración con frontend

### Seguridad implementada

Se utiliza autenticación mediante **JSON Web Tokens (JWT)**, garantizando que únicamente usuarios autenticados puedan consumir la API.

---

## WebSockets + Autenticación por Token

Utilizado para comunicación asíncrona en tiempo real:

```text
Servidor Central -> Cliente
```

### Beneficios

- Implementa patrón observador
- Permite envío de alertas en tiempo real
- Evita saturación de red causada por polling constante

---

## gRPC + TLS (Capa de Transporte)

Utilizado para el proceso Batch nocturno:

```text
Servidor Central -> Mock SCE
```

### Motivos de elección

- Uso de Protocol Buffers
- Serialización binaria eficiente
- Reducción significativa del ancho de banda
- Alto rendimiento para transmisión masiva de registros

### Seguridad implementada

El canal Backend-to-Backend está protegido mediante certificados **OpenSSL (TLS)** inyectados directamente en el empaquetado `.jar` de Java.

Esto garantiza:

- Cifrado de extremo a extremo
- Protección contra interceptaciones de red
- Comunicación segura entre microservicios

---

# Tecnologías Utilizadas

## Backend

- Spring Boot
- Node.js
- gRPC
- JWT
- WebSockets

## Infraestructura

- Docker
- Docker Compose

## Frontend

- HTML5
- CSS3
- JavaScript

---

# Objetivo Académico

Esta prueba de concepto fue desarrollada con fines académicos para demostrar:

- Integración de sistemas distribuidos
- Comunicación síncrona y asíncrona
- Seguridad multicapa
- Orquestación de contenedores
- Interoperabilidad entre tecnologías heterogéneas

---
