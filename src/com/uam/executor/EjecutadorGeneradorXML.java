package com.uam.executor;

import com.uam.auxiliar.GeneradorReactivoCloze;
import static com.uam.constantes.Constantes.GLOBAL_XML_PREFIJO;
import static com.uam.constantes.Constantes.GLOBAL_XML_SUFIJO;
import java.io.IOException;
import java.io.PrintWriter;




/**
 * Crea un archivo XML para importar en Moodle
 * @author Iván Gutiérrez Rodríguez
 */
public class EjecutadorGeneradorXML {
    
    public static void generarReactivos(String nombreArchivo, int numeroReactivos, GeneradorReactivoCloze generador) {
        try {
            try (PrintWriter writer = new PrintWriter(nombreArchivo, "UTF-8")) {
                writer.print(GLOBAL_XML_PREFIJO);
                for(int i = 0; i<numeroReactivos; i++){
                    writer.print(generador.generarReactivoCloze(i));
                }
                writer.print(GLOBAL_XML_SUFIJO);                
            }
        }catch (IOException e){
            System.out.print("Error al generar el archivo de salida: "+e.getMessage());
        }

    }
}
