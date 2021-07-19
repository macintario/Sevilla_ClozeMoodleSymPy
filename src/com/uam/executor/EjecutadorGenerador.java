package com.uam.executor;

import com.uam.auxiliar.GeneradorReactivoCloze;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class EjecutadorGenerador {
    
    public static void generarReactivos(String nombreArchivo, int numeroReactivos, GeneradorReactivoCloze generador) {
        try {
            try (PrintWriter writer = new PrintWriter(nombreArchivo, "UTF-8")) {
                for(int i = 0; i<numeroReactivos; i++){
                    writer.print(generador.generarReactivoCloze(i));
                }
            }
        }catch (IOException e){
            System.out.print("Error al generar el archivo de salida: "+e.getMessage());
        }

    }
}



