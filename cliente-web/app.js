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