package com.uam.auxiliar;

import com.uam.utilidades.Utilidades;

/**
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class ObjetoComprable {
    private SingularPlural nombre;
    private Double precio;
    private final Double [] posiblesPrecios;

    
    public ObjetoComprable (String nombreSingular, String nombrePlural, Double[] posiblesPrecios){
        this.nombre = new SingularPlural(nombreSingular, nombrePlural);
        this.posiblesPrecios = posiblesPrecios;
        this.generarPrecio();
    }
    
    public final void generarPrecio(){
        this.precio =Utilidades.obtenerAleatorioDeArreglo(posiblesPrecios);
    }
    
    public SingularPlural getNombre(){
        return this.nombre;
    }
    
    public String getNombreString(boolean plural) {
        return Utilidades.aString(this.nombre, plural);
    }

    public void setNombre(SingularPlural nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
}
