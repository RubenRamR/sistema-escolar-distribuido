let tokenJWT = null;
let socket = null;

// 1. INICIO DE SESIÓN
async function iniciarSesion() {
  // Tomamos los valores de los inputs del HTML
  const usuario = document.getElementById("in-usuario").value;
  const password = document.getElementById("in-password").value;

  try {
    const response = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ usuario: usuario, password: password }),
    });

    const data = await response.json();

    if (response.ok) {
      tokenJWT = data.token; // Guardamos el gafete
      alert("Éxito: " + data.mensaje);

      // Ocultar login y mostrar dashboard
      document.getElementById("seccion-login").style.display = "none";
      document.getElementById("seccion-dashboard").style.display = "block";

      iniciarWebSocket();
    } else {
      alert("Error: " + data.error);
    }
  } catch (error) {
    alert("Error de red al iniciar sesión.");
  }
}

// 2. WEBSOCKET
function iniciarWebSocket() {
  socket = new WebSocket(`ws://localhost:8080/alertas?token=${tokenJWT}`);
  socket.onopen = () => console.log("WebSocket conectado.");
  socket.onmessage = (event) => alert("⚠️ ALERTA ACADÉMICA \n\n" + event.data);
}

// 3. RUTAS PROTEGIDAS (AQUÍ ENVIAMOS EL TOKEN JWT)
async function consultarProgreso() {
  const btn = document.querySelector(".btn-consultar");
  btn.innerText = "Consultando...";
  btn.disabled = true;

  try {
    const response = await fetch("http://localhost:8080/api/padres/progreso", {
      method: "GET",
      headers: {
        Authorization: "Bearer " + tokenJWT,
      },
    });

    if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

    const data = await response.json();
    renderizarInterfaz(data);
  } catch (error) {
    alert("Error: No autorizado o el servidor no responde.");
  } finally {
    btn.innerText = "Actualizar Calificaciones";
    btn.disabled = false;
  }
}

async function avalarTarea() {
  try {
    const response = await fetch(
      "http://localhost:8080/api/interacciones/avalar-tarea",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + tokenJWT,
        },
        body: JSON.stringify({ idTarea: "T-998", firmaPadre: "Aprobado" }),
      },
    );
    const data = await response.json();
    alert("Éxito: " + data.mensaje);
  } catch (error) {
    alert("Error al avalar la tarea.");
  }
}

async function enviarMensaje() {
  const mensaje = prompt("Escribe el mensaje para el profesor:");
  if (!mensaje) return;

  try {
    const response = await fetch(
      "http://localhost:8080/api/interacciones/mensaje",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + tokenJWT,
        },
        body: JSON.stringify({ idProfesor: "P-101", contenido: mensaje }),
      },
    );
    const data = await response.json();
    alert("Éxito: " + data.mensaje);
  } catch (error) {
    alert("Error al enviar el mensaje.");
  }
}

async function sincronizarBatch() {
  try {
    const res = await fetch("http://localhost:8080/api/batch/sincronizar", {
      method: "POST",
    });
    const data = await res.json();
    alert(data.mensaje || "Batch enviado. Revisa la consola.");
  } catch (e) {
    alert("Error al sincronizar.");
  }
}

// 4. RENDERIZADO VISUAL
function renderizarInterfaz(data) {
  document.getElementById("contenedor-resultados").style.display = "block";
  document.getElementById("ui-nombre").innerText = data.nombre;
  document.getElementById("ui-periodo").innerText = data.periodo;

  const listaMaterias = document.getElementById("ui-lista-materias");
  listaMaterias.innerHTML = "";

  let materiasEnRiesgo = [];

  data.materias.forEach((materia) => {
    const li = document.createElement("li");
    li.className = "materia-item";
    const claseColor = materia.calificacion < 6.0 ? "alerta-baja" : "";

    li.innerHTML = `
            <span>${materia.nombre}</span>
            <span class="${claseColor}">${materia.calificacion}</span>
        `;
    listaMaterias.appendChild(li);

    if (materia.calificacion < 6.0) {
      materiasEnRiesgo.push(
        `${materia.nombre} (Calificación: ${materia.calificacion})`,
      );
    }
  });

  if (materiasEnRiesgo.length > 0) {
    alert(
      "!!ALERTA ACADÉMICA DEL SISTEMA!! \n\nEl estudiante presenta bajo rendimiento en:\n- " +
        materiasEnRiesgo.join("\n- "),
    );
  }
}
