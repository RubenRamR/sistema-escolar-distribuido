/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central;

import com.itson.servidor_central.grpc.CalificacionesProto;
import com.itson.servidor_central.grpc.SincronizadorSCEGrpc;  // import separado
import com.itson.servidor_central.models.CalificacionDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.springframework.core.io.ClassPathResource;

@Service
public class SincronizadorBatchService {

    private static final String SCE_HOST = "localhost";
    private static final int SCE_PORT = 50051;

    public void enviarLote(List<CalificacionDTO.MateriaDTO> materias, String estudiante) {
        System.out.println("[gRPC] Iniciando sincronización batch con SCE...");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(SCE_HOST, SCE_PORT)
                .usePlaintext()
                .build();

        try {
            // SincronizadorSCEGrpc es clase independiente, no anidada en CalificacionesProto
            SincronizadorSCEGrpc.SincronizadorSCEBlockingStub stub =
                    SincronizadorSCEGrpc.newBlockingStub(channel);

            CalificacionesProto.LoteRequest.Builder builder =
                    CalificacionesProto.LoteRequest.newBuilder()
                            .setTimestamp(java.time.LocalDateTime.now().toString());

            for (CalificacionDTO.MateriaDTO m : materias) {
                builder.addItems(
                    CalificacionesProto.CalificacionItem.newBuilder()
                        .setMateria(m.nombre)
                        .setEstudiante(estudiante)
                        .setCalificacion(m.calificacion)
                        .build()
                );
            }

            CalificacionesProto.LoteResponse response =
                    stub.enviarLoteCalificaciones(builder.build());

            System.out.println("[gRPC] Respuesta SCE: " + response.getMensaje());
            System.out.println("[gRPC] Registros procesados: " + response.getRegistrosProcesados());

        } catch (Exception e) {
            System.err.println("[gRPC] Error al contactar SCE: " + e.getMessage());
        } finally {
            channel.shutdown();
            try { channel.awaitTermination(5, TimeUnit.SECONDS); }
            catch (InterruptedException ignored) {}
        }
    }

    // Utiliza el certificado de OpenSSL para crear de conexion seguro
    public ManagedChannel crearCanalSeguro() throws SSLException, IOException {
        // Apuntamos al certificado que acabamos de generar utilizando openssl
        File certChainFile = new ClassPathResource("server.crt").getFile();
        
        return NettyChannelBuilder.forAddress("localhost", 50051)
                // Configurar contexto SSL para que confie en el cerftificado autofirmado
                .sslContext(GrpcSslContexts.forClient().trustManager(certChainFile).build())
                .build();
    }
    
}