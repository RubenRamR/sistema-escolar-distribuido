const grpc = require("@grpc/grpc-js");
const fs = require("fs");
const protoLoader = require("@grpc/proto-loader");
const path = require("path");

// 1. Leer los certificados OpenSSL generados
const serverCert = fs.readFileSync("./server.crt");
const serverKey = fs.readFileSync("./server.key");

// 2. Crear credenciales SSL
const credentials = grpc.ServerCredentials.createSsl(null, [
  {
    cert_chain: serverCert,
    private_key: serverKey,
  },
]);

// 3. Cargar el archivo Proto (Ahora busca en la misma carpeta)
const PROTO_PATH = path.join(__dirname, "./calificaciones.proto");

const packageDef = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true,
});

const proto = grpc.loadPackageDefinition(packageDef);

// 4. Lógica de negocio (Prueba de concepto)
function enviarLoteCalificaciones(call, callback) {
  const lote = call.request;

  console.log("\n[Mock SCE] ══════════ LOTE RECIBIDO (TLS) ══════════");
  console.log(`[Mock SCE] Timestamp : ${lote.timestamp}`);
  console.log(`[Mock SCE] Registros : ${lote.items.length}`);
  lote.items.forEach((item, i) => {
    console.log(
      `[Mock SCE]   ${i + 1}. ${item.estudiante} | ` +
        `${item.materia} | Cal: ${item.calificacion}`,
    );
  });
  console.log("[Mock SCE] ══════════════════════════════════════════\n");

  callback(null, {
    exito: true,
    registros_procesados: lote.items.length,
    mensaje: `SCE procesó ${lote.items.length} registros correctamente por canal seguro`,
  });
}

// 5. Crear el servidor gRPC
const server = new grpc.Server();

server.addService(proto.SincronizadorSCE.service, {
  EnviarLoteCalificaciones: enviarLoteCalificaciones,
});

// 6. Levantar el servidor de forma SEGURA en el puerto 50051
server.bindAsync("0.0.0.0:50051", credentials, (err, port) => {
  if (err) {
    console.error("[Mock SCE] Error al iniciar: ", err);
    return;
  }
  console.log(
    `[Mock SCE] Servidor gRPC corriendo de forma SEGURA (TLS) en el puerto ${port}`,
  );
});
