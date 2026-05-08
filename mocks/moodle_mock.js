const express = require('express');
const cors = require('cors');

const app = express();
const PORT = 9090;

// Middleware
app.use(cors());
app.use(express.json());

// Datos estáticos
const mockCalificaciones = {
    estudianteId: "1001",
    nombre: "Alumno Prueba",
    periodo: "2026-1",
    materias: [
        { id: "MAT101", nombre: "Matemáticas", calificacion: 8.5 },
        { id: "HIS202", nombre: "Historia", calificacion: 9.0 },
        { id: "SIS303", nombre: "Sistemas Distribuidos", calificacion: 5.5 }
    ]
};

app.get('/api/calificaciones', (req, res) => {
    console.log(`[Mock Moodle] Petición recibida en /api/calificaciones a las ${new Date().toLocaleTimeString()}`);
    
    setTimeout(() => {
        res.status(200).json(mockCalificaciones);
    }, 300);
});

// Iniciar servidor
app.listen(PORT, () => {
    console.log(`[Mock Moodle] Servidor ejecutándose en http://localhost:${PORT}`);
    console.log(`[Mock Moodle] Endpoint disponible: GET /api/calificaciones`);
});