const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

const PROTO_PATH = path.join(
    __dirname,
    '../servidor-central/servidor-central/src/main/proto/calificaciones.proto'
);

const packageDef = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true
});

const proto = grpc.loadPackageDefinition(packageDef);

function enviarLoteCalificaciones(call, callback) {
    const lote = call.request;

    console.log('\n[Mock SCE] ══════════ LOTE RECIBIDO ══════════');
    console.log(`[Mock SCE] Timestamp : ${lote.timestamp}`);
    console.log(`[Mock SCE] Registros : ${lote.items.length}`);
    lote.items.forEach((item, i) => {
        console.log(
            `[Mock SCE]   ${i + 1}. ${item.estudiante} | ` +
            `${item.materia} | Cal: ${item.calificacion}`
        );
    });
    console.log('[Mock SCE] ════════════════════════════════════\n');

    callback(null, {
        exito: true,
        registros_procesados: lote.items.length,
        mensaje: `SCE procesó ${lote.items.length} registros correctamente`
    });
}

const server = new grpc.Server();
server.addService(proto.SincronizadorSCE.service, {
    EnviarLoteCalificaciones: enviarLoteCalificaciones
});

server.bindAsync(
    '0.0.0.0:50051',
    grpc.ServerCredentials.createInsecure(),
    (err, port) => {
        if (err) { console.error('[Mock SCE] Error:', err); return; }
        console.log(`[Mock SCE] Servidor gRPC escuchando en :${port}`);
    }
);