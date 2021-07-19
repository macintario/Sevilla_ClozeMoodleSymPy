package com.uam.auxiliar;

/**
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public enum Relacion {
    MAYOR_QUE(">"),
    MENOR_QUE("<"),
    MAYOR_O_IGUAL_QUE(">="),
    MENOR_O_IGUAL_QUE("<="),
    SIN_RELACION("SIN_RELACION");
    
    private final String relacion; 
    
    Relacion(String relacion){
        this.relacion = relacion;
    }
    
    public String getString(){
        return this.relacion;
    }
}
