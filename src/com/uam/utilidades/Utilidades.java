package com.uam.utilidades;

import java.util.Random;

/**
 * Clase con métodos para dar soporte a los generadores de reactivos moodle.
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class Utilidades {

    public static final Random RANDOM = new Random();
    private static final int MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO = 500;


    public static String generaComentario(String prefijo, int numeroReactivo, int posiciones){
        return prefijo + Utilidades.digitoACadena(numeroReactivo, posiciones);
    }
    

    public static String digitoACadena(int digito, int posiciones) {
        StringBuilder cadena = new StringBuilder(Integer.toString(digito));
        if (cadena.length() < posiciones) {
            int deficit = posiciones - cadena.length();
            for (int i = 0; i < deficit; i++) {
                cadena.insert(0, "0");
            }
        }
        return cadena.toString();
    }

    /**
     * Genera un numero aleatorio entero entre inferior y superior incluyendo
     * los extremos.
     *
     * @param inferior lower bound of random number
     * @param superior upper bound of random number
     * @return random number between  lower and upper bound
     */
    public static int obtenerEnteroAleatorio(int inferior, int superior) {
        return (int) (RANDOM.nextDouble() * ((superior - inferior) + 1) + inferior);
    }
    public static int obtenerParAleatorio(int inferior, int superior) {
        int candidato=(int) (RANDOM.nextDouble() * ((superior - inferior) + 1) + inferior);
        if(candidato%2==1){
            candidato++;
        }
        return candidato;
    }
    public static int obtenerImparAleatorio(int inferior, int superior) {
        int candidato=(int) (RANDOM.nextDouble() * ((superior - inferior) + 1) + inferior);
        if(candidato%2==0){
            candidato++;
        }
        return candidato;
    }

    public static int obtenerImparAleatorioDistintoDe(int inferior, int superior, int distintoDe){
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        int aleatorio;
        do{
            aleatorio = obtenerImparAleatorio(inferior, superior);
            intentos--;
        }while(intentos>=0 && aleatorio == distintoDe);
        if(intentos<0){
            throw new RuntimeException("No se ha podido encontrar un numero aleatorio distinto de "+distintoDe+
                    " usando la cota ["+inferior+"-"+superior+"]");
        }
        return aleatorio;
    }

    public static int obtenerEnteroAleatorioDistintoDe(int inferior, int superior, int distintoDe){
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        int aleatorio;
        do{
            aleatorio = obtenerEnteroAleatorio(inferior, superior);  
            intentos--;
        }while(intentos>=0 && aleatorio == distintoDe);
        if(intentos<0){
            throw new RuntimeException("No se ha podido encontrar un numero aleatorio distinto de "+distintoDe+
                    " usando la cota ["+inferior+"-"+superior+"]");
        }
        return aleatorio;
    }

    public static void main(String... f)  {
        for(int i=0; i<10;i++){
            System.out.println(Utilidades.eventoAleatorioDentroDeProbabilidad(50));
        }
    }

    @SafeVarargs
    public static <T> T obtenerAleatorioDeArreglo(T... dominio) {
        T aleatorio =null;
        if (dominio.length > 0) {
            int indiceAleatorio = Utilidades.obtenerEnteroAleatorio(0, dominio.length - 1);
            aleatorio = dominio[indiceAleatorio];
        }
        return aleatorio;
    }


    public static boolean eventoAleatorioDentroDeProbabilidad(int probabilidad){
        validarPorcentaje(probabilidad);
        int aleatorio = obtenerEnteroAleatorio(1, 100);
        return probabilidad > 0 && aleatorio <= probabilidad;
    }

    public static void validarPorcentaje(int porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new RuntimeException("Los porcentajes de probabilidad configurados deben estar entre 0 y 100. "
                    + "Encontrado: "+porcentaje);
        }
    }

    /**
     *  Máximo Común Divisor de dos enteros, útil para reducción de fracciones
      * @param a primer número
     * @param b segundo número
     * @return MCD
     * @author Iván Gutiérrez
     */
    public static Integer maximoComunDivisor(int a, int b){
        if(b==0)
            return a;
        else
            return maximoComunDivisor(b,a%b);
    }

}
