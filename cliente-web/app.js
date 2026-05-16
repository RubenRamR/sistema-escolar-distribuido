// --- Conexión WebSocket para notificaciones Push en tiempo real ---
const socket = new WebSocket('ws://localhost:8080/ws/alertas');

socket.onopen = function() {
    console.log("Conectado exitosamente al Servicio de Alertas (WebSocket)");
};

socket.onmessage = function(event) {
    // Cuando el servidor empuja un mensaje, lo mostramos inmediatamente como una alerta visual
    alert(" ALERTA ACADÉMICA DEL SISTEMA \n\n" + event.data);
};

socket.onerror = function(error) {
    console.error("Error en la conexión del WebSocket:", error);
};


async function consultarProgreso() {
    const btn = document.querySelector('.btn-consultar');
    btn.innerText = "Consultando...";
    btn.disabled = true;

    try {
        // Petición REST al Servidor Central (API Gateway)
        const response = await fetch('http://localhost:8080/api/padres/progreso');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        renderizarInterfaz(data);
        
    } catch (error) {
        console.error("Error de conexión:", error);
        alert("No se pudo contactar al servidor central. Verifica que esté corriendo en el puerto 8080.");
    } finally {
        btn.innerText = "Actualizar Calificaciones";
        btn.disabled = false;
    }
}

function renderizarInterfaz(data) {
    document.getElementById('contenedor-resultados').style.display = 'block';
    document.getElementById('ui-nombre').innerText = data.nombre;
    document.getElementById('ui-periodo').innerText = data.periodo;

    const listaMaterias = document.getElementById('ui-lista-materias');
    listaMaterias.innerHTML = ''; // Limpiamos la lista

    data.materias.forEach(materia => {
        const li = document.createElement('li');
        li.className = 'materia-item';
        
        // Lógica de negocio visual: detectar calificación baja
        const claseColor = materia.calificacion < 6.0 ? 'alerta-baja' : '';

        li.innerHTML = `
            <span>${materia.nombre}</span>
            <span class="${claseColor}">${materia.calificacion}</span>
        `;
        listaMaterias.appendChild(li);
    });
}

async function sincronizarBatch() {
    try {
        const res = await fetch('http://localhost:8080/api/batch/sincronizar',
            { method: 'POST' });
        const data = await res.json();
        console.log('[gRPC Batch] Respuesta:', data);
        alert('Batch enviado. Revisa la consola del Mock SCE (Terminal 2).');
    } catch (e) {
        alert('Error: ' + e.message);
    }
}

async function avalarTarea() {
    try {
        const response = await fetch('http://localhost:8080/api/interacciones/avalar-tarea', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idTarea: "T-998", firmaPadre: "Aprobado" })
        });
        const data = await response.json();
        alert("Éxito: " + data.mensaje);
    } catch (error) {
        alert("Error al avalar la tarea: " + error.message);
    }
}

async function enviarMensaje() {
    const mensaje = prompt("Escribe el mensaje para el profesor de Sistemas Distribuidos:");
    if (!mensaje) return;

    try {
        const response = await fetch('http://localhost:8080/api/interacciones/mensaje', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idProfesor: "P-101", contenido: mensaje })
        });
        const data = await response.json();
        alert("Éxito: " + data.mensaje);
    } catch (error) {
        alert("Error al enviar el mensaje: " + error.message);
    }
}