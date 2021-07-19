package com.uam.auxiliar;

/**
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public interface GeneradorReactivoCloze {

    /**
     * Genera el texto del reactivo tipo cloze listo para ser
     * vaciado al archivo de salida. No debe incluir el separador de
     * reactivos.
     * 
     * @param numeroReactivo El contador de reactivo.
     * @return 
     * @throws java.lang.Exception 
     */
    public String generarReactivoCloze(int numeroReactivo);
}
