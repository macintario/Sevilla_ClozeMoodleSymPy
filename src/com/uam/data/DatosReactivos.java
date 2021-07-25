package com.uam.data;

import com.uam.utilidades.Utilidades;

/**
 * Contiene el banco de datos a utilizar por los generadores de reactivos.
 *
 * @author Iván Gutiérrez
 */
public class DatosReactivos {


    public static final String[] PARES_VARIABLES = {
        "xy"
            //,"zw","rs","st","wx","yz"
            //,"\u03b8\u03a9","\u03b3\u03c9"
    };
    
    public static String obtenerParesVariables(){
        return Utilidades.obtenerAleatorioDeArreglo(PARES_VARIABLES);
    }
}
