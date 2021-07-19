package com.uam.data;

import com.uam.auxiliar.ObjetoComprable;
import com.uam.utilidades.Utilidades;

/**
 * Contiene el banco de datos a utilizar por los generadores de reactivos.
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class DatosReactivos {

    /**
     * El arreglo con nombres de tiendas que se usarán en la generación de 
     * reactivos que utilicen nombres de tiendas. Las tildes deben ser expresadas en código utf8.
     * Se pueden agregar, eliminar o editar los nombres de este arreglo a necesidad.
     */
    public static final String[] NOMBRES_TIENDAS = {"La Rebaja", "La Esperanza",
        "De Todo un Poco", "La Luz", "San Crist\u00f3bal", "Madrid"};

    /** 
     * El arreglo con objetos comprables que se usarán en la generación de
     * reactivos que utilicen objetos comprables. Las tildes deben ser expresadas en codigo utf8.
     * Se pueden agregar, eliminar o editar los nombres de este arreglo a necesidad.
     * ObjetoComprable(nombre en singular, nombre en plural, arreglo con los posibles precios para el objeto)
     * Los precios pueden ser enteros o decimales, si son enteros deben llevar el decimal en cero eg 4.0, 8.0.
     */
    public static final ObjetoComprable[] OBJETOS_COMPRABLES 
            = {new ObjetoComprable("l\u00e1piz", "l\u00e1pices", new Double[]{1.0, 1.5, 2.0, 2.5, 3.0}),
               new ObjetoComprable("bol\u00edgrafo", "bol\u00edgrafos", new Double[]{2.0, 2.5, 3.0, 3.5, 4.0}),
               new ObjetoComprable("goma", "gomas", new Double[]{2.0, 2.5, 3.0}),
               new ObjetoComprable("cuaderno", "cuadernos", new Double[]{6.0, 6.5, 7.0, 7.5, 9.0, 10.5, 12.0})};

    /**
     * El arreglo con nombres masculinos que se usarán en la generación de 
     * reactivos que utilicen nombres masculinos. Las tildes deben ser expresadas en código utf8.
     * Se pueden agregar, eliminar o editar los nombres de este arreglo a necesidad.
     */
    public static final String[] NOMBRES_MASCULINOS = {"Eduardo", "Jos\u00e9", "Edgar",
        "Gilberto", "Diego", "Omar", "V\u00edctor", "Aldo", "Francisco", "Joaqu\u00edn",
        "Felipe", "Benjam\u00edn", "Juan", "Pablo", "Fernando", "Roberto", "Jaime", "Federico",
        "Pepe", "Gustavo", "Sergio", "Alfonso", "Memo", "Guillermo", "Yair", "Luis", "Ram\u00f3n",
        "Tom\u00e1s", "Pedro", "Paco", "Gabriel", "H\u00e9ctor", "Javier", "Manuel", "Carlos",
        "Mart\u00edn", "Jorge", "Ra\u00fal", "Jes\u00fas", "Rub\u00e9n", "Gerardo", "Edgardo", "Efra\u00edn",
        "Efr\u00e9n", "Eric", "Ar\u00f3n", "Abraham", "Andr\u00e9s", "Adolfo", "Adri\u00e1n", "\u00c1ngel", "Ad\u00e1n",
        "Antonio", "Agust\u00edn", "Alejandro", "Ulises", "\u00c1lvaro", "Bernardo", "Beto", "Horacio", "\u00c1xel",
        "Braulio", "Fidel", "Fabi\u00e1n", "Esteban", "Alfredo", "Bruno", "F\u00e9lix", "Hern\u00e1n", "Iv\u00e1n",
        "Jacobo", "Hugo", "Israel", "Gonzalo", "Enrique", "Ismael", "Ren\u00e9"};

    /** 
     * El arreglo con nombres femeninos que se usarán en la generación de 
     * reactivos que utilizen nombres femeninos. Las tildes deben ser expresadas en código utf8.
     * Se pueden agregar, eliminar o editar los nombres de este arreglo a necesidad.
     */
    public static final String[] NOMBRES_FEMENINOS = {"Laura", "Leticia", "Marcela", "Itzel",
        "Silvia", "Lorena", "Georgina", "Alicia", "Marisol", "Romina", "Guadalupe", "Norma", "Beatriz",
        "Gloria", "Mercedes", "Fernanda", "Ana", "Gabriela", "Rosa", "Julieta", "Pilar", "Andrea",
        "Alejandra", "Elisa", "Erica", "Estela", "Blanca", "Sara", "Daniela", "Jocelyn", "Pamela", "Tania",
        "Carmen", "Teresa", "Susana", "Flor", "Fabiola", "Adriana", "Berenice", "Sandra", "Marina", "Priscila",
        "Carolina", "Clara", "Samantha", "Miriam", "Cristina", "Araceli", "Elvia", "In\u00e9s", "Isabel",
        "Mariana", "Janet", "Lizeth", "Lupe", "Mar\u00eda", "Matilde", "Mayra", "Yunuen", "Merlina",
        "Mirna", "Olga", "Nayeli", "Karina", "Olivia", "Raquel", "Regina", "Roxana", "Selena", "Tamara",
        "Viviana", "Yazm\u00edn", "Brenda", "Berta", "Karen", "Irma", "Sabrina", "Rebeca", "Pen\u00e9lope",
        "Frida", "Cecilia", "Karla", "Esmeralda", "Ester"};

    
    public static final String obtenerNombreAleatorio(int probabilidadFemenino) {
        String nombre = null;
        if (Utilidades.eventoAleatorioDentroDeProbabilidad(probabilidadFemenino)) {
            nombre = Utilidades.obtenerAleatorioDeArreglo(NOMBRES_FEMENINOS);
        } else {
            nombre = Utilidades.obtenerAleatorioDeArreglo(NOMBRES_MASCULINOS);
        }
        return nombre;
    }
    public static final String[] PARES_VARIABLES = {
        "xy"
            //,"zw","rs","st","wx","yz"
            //,"\u03b8\u03a9","\u03b3\u03c9"
    };
    
    public static String obtenerNombreTiendaAleatorio() {
        return Utilidades.obtenerAleatorioDeArreglo(NOMBRES_TIENDAS);
    }
    public static String obtenerParesVariables(){
        return Utilidades.obtenerAleatorioDeArreglo(PARES_VARIABLES);
    }
}
