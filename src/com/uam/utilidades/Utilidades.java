package com.uam.utilidades;

import com.uam.auxiliar.Fraccion;
import com.uam.auxiliar.Relacion;
import com.uam.auxiliar.SingularPlural;
import com.uam.constantes.Constantes;
import com.uam.excepcion.CondicionReactivoNoCumplidaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

/**
 * Clase con métodos para dar soporte a los generadores de reactivos moodle.
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class Utilidades {

    public static final Random RANDOM = new Random();
    private static final int MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO = 500;

    public static String generarEtiquetaReactivoHtml(String prefijo, int numeroReactivo, int posiciones) {
        StringBuilder comentarioHtml = new StringBuilder(Constantes.HTML_INICIO_COMENTARIO);
        comentarioHtml.append(prefijo);
        comentarioHtml.append(Utilidades.digitoACadena(numeroReactivo, posiciones));
        comentarioHtml.append(Constantes.HTML_FIN_COMENTARIO);
        return comentarioHtml.toString();
    }

    
    public static String generaComentario(String prefijo, int numeroReactivo, int posiciones){
      StringBuilder comentarioXML = new StringBuilder(prefijo);
        comentarioXML.append(Utilidades.digitoACadena(numeroReactivo, posiciones));
        return comentarioXML.toString();
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
     * @param inferior
     * @param superior
     * @return
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
        int aleatorio = 0;
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
        int aleatorio = 0;
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

    /**
     * Suma mimnutosASumar a la hora en formato 24 horas especificada por
     * horaInicial y minuto Inicial. Devuelve el resultado como un string en
     * formato hh:mm.
     *
     * @param horaInicial
     * @param minutoInicial
     * @param minutosASumar
     * @return La suma horaInicial:minutoInicial + minutosASumar.
     */
    public static String sumarMinutos(int horaInicial, int minutoInicial, int minutosASumar) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(Calendar.HOUR_OF_DAY, horaInicial);
        calendarDate.set(Calendar.MINUTE, minutoInicial);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
        calendarDate.add(Calendar.MINUTE, minutosASumar);
        StringBuilder horaMinutoResultante = new StringBuilder();
        horaMinutoResultante.append(calendarDate.get(Calendar.HOUR_OF_DAY));
        horaMinutoResultante.append(":");
        horaMinutoResultante.append(Utilidades.digitoACadena(calendarDate.get(Calendar.MINUTE), 2));
        return horaMinutoResultante.toString();
    }

    public static void main(String... f) throws Exception {
        for(int i=0; i<10;i++){
            System.out.println(Utilidades.eventoAleatorioDentroDeProbabilidad(50));
        }
    }

    public static String calcularPorcentajeAPagarConDescuentosAnidados(int numeroDecimalesResultado, int... porcentajesDesc) {
        String porcentajeAPagarString = "100";
        if (porcentajesDesc.length > 0) {
            float porcentajeAPagar = (float) 100 - porcentajesDesc[0];
            for (int i = 1; i < porcentajesDesc.length; i++) {
                porcentajeAPagar = porcentajeAPagar - (porcentajeAPagar * ((float) porcentajesDesc[i] / 100));
            }            
            porcentajeAPagarString = aStringDecimal(porcentajeAPagar, numeroDecimalesResultado);
        }
        return porcentajeAPagarString;
    }

    public static String obtenerSignoComparativo(Fraccion fraccionA, Fraccion fraccionB) {
        String signoComparativo = null;
        int comparacion = fraccionA.compareTo(fraccionB);
        if (comparacion == 0) {
            signoComparativo = "=";
        } else if (comparacion > 0) {
            signoComparativo = ">";
        } else {
            signoComparativo = "<";
        }
        return signoComparativo;
    }

    public static <T> T obtenerAleatorioDeArreglo(T... dominio) {
        T aleatorio =null;
        if (dominio.length > 0) {
            int indiceAleatorio = Utilidades.obtenerEnteroAleatorio(0, dominio.length - 1);
            aleatorio = dominio[indiceAleatorio];
        }
        return aleatorio;
    }

    public static Fraccion obtenerFraccionAleatoriaMenorQue(Fraccion criterio, int[] cotaNumerador, int[] cotaDenominador, Relacion relacion) throws CondicionReactivoNoCumplidaException {
        Fraccion fraccion = new Fraccion(cotaNumerador, cotaDenominador, relacion);
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        while (intentos >= 0 && fraccion.compareTo(criterio) >= 0) {
            fraccion.generarNumeradorDenominador(relacion);
            intentos--;
        }
        if (fraccion.compareTo(criterio) >= 0) {
            throw new CondicionReactivoNoCumplidaException("Para asegurar la no negatividad del resultado del problema, se requiere "
                    + "generar una fracción aleatoria que sea menor que " + criterio + ", la cual no pudo ser generada usando las cotas: "
                    + "numerador (" + cotaNumerador[0] + "-" + cotaNumerador[1] + ") denominador ("
                    + cotaDenominador[0] + "-" + cotaDenominador[1] + ") Además la fracción debe cumplir con la relación configurada: "
                    + "[numerador "+relacion+" denominador].");
        }
        return fraccion;
    }
    
    
    public static Fraccion obtenerFraccionAleatoriaMayorQue(Fraccion criterio, int[] cotaNumerador, int[] cotaDenominador, Relacion relacion) throws CondicionReactivoNoCumplidaException {
        Fraccion fraccion = new Fraccion(cotaNumerador, cotaDenominador, relacion);
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        while (intentos >= 0 && fraccion.compareTo(criterio) <= 0) {
            fraccion.generarNumeradorDenominador(relacion);
            intentos--;
        }
        if (fraccion.compareTo(criterio) <= 0) {
            throw new CondicionReactivoNoCumplidaException("Para asegurar la no negatividad del resultado del problema, se requiere "
                    + "generar una fracción aleatoria que sea mayor que " + criterio + ", la cual no pudo ser generada usando las cotas: "
                    + "numerador (" + cotaNumerador[0] + "-" + cotaNumerador[1] + ") denominador ("
                    + cotaDenominador[0] + "-" + cotaDenominador[1] + ") Además la fracción debe cumplir con la relación configurada: "
                    + "[numerador "+relacion+" denominador].");
        }
        return fraccion;
    }
    
    public static String fraccionADecimal(Fraccion fraccion, int numeroDecimales) {
        return aStringDecimal((double) fraccion.getNumerador() / fraccion.getDenominador(), numeroDecimales);
    }
    
    public static String aStringDecimal(double numero, int numeroDecimales){
        StringBuilder decimalesFormato = new StringBuilder();
        for(int i = 0; i<numeroDecimales;i++){
            decimalesFormato.append("#");
        }
        if(numeroDecimales>0){
            decimalesFormato.insert(0, ".");
        }
        DecimalFormat formatter = new DecimalFormat("##"+decimalesFormato.toString());
        formatter.setRoundingMode(RoundingMode.DOWN);
        return formatter.format(numero);
    }
   
    /**
     * Encuentra el entero menor más cercano al entero criterio dentro del intervalo 
     * definido por intervalo[0] - intervalo[1]. Se asume que el arreglo 
     * intervalo esta ordenado ascendentemente. Si no se encuentra un entero menor 
     * más cercano se devuelve null.
     * @param intervalo
     * @param criterio
     * @return 
     */
    public static Integer obtenerEnteroMenorMasCercanoEnIntervalo(int[] intervalo, int criterio) {
        validarCota(intervalo);
        for (int i = intervalo[1]; i >= intervalo[0]; i--) {
            if (i < criterio) {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Encuentra el entero menor oigual más cercano al entero criterio dentro del intervalo 
     * definido por intervalo[0] - intervalo[1]. Se asume que el arreglo 
     * intervalo esta ordenado ascendentemente. Si no se encuentra un entero menor
     * o igual más cercano se decuelve null.
     * @param intervalo
     * @param criterio
     * @return 
     */
    public static Integer obtenerEnteroMenorOIgualMasCercanoEnIntervalo(int [] intervalo, int criterio){
        validarCota(intervalo);
        for (int i = intervalo[1]; i >= intervalo[0]; i--) {
            if (i <= criterio) {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Encuentra el entero mayor o igual más cercano al entero criterio dentro del intervalo 
     * definido por intervalo[0] - intervalo[1]. Se asume que el arreglo 
     * intervalo esta ordenado ascendentemente. Si no se encuentra un entero mayor
     * o igual más cercano se devuelve null.
     * @param intervalo
     * @param criterio
     * @return 
     */
    public static Integer obtenerEnteroMayorMasCercanoEnIntervalo(int [] intervalo, int criterio){
        for (int i = intervalo[0]; i <= intervalo[1]; i++) {
            if (i > criterio) {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Encuentra el entero mayor o igual más cercano al entero criterio dentro del intervalo 
     * definido por intervalo[0] - intervalo[1]. Se asume que el arreglo 
     * intervalo esta ordenado ascendentemente. Si no se encuentra un entero mayor
     * o igual más cercano se devuelve null.
     * @param intervalo
     * @param criterio
     * @return 
     */
    public static Integer obtenerEnteroMayorOIgualMasCercanoEnIntervalo(int [] intervalo, int criterio){
        validarCota(intervalo);
        for (int i = intervalo[0]; i <= intervalo[1]; i++) {
            if (i >= criterio) {
                return i;
            }
        }
        return null;
    }
    
    public static boolean eventoAleatorioDentroDeProbabilidad(int probabilidad){
        validarPorcentaje(probabilidad);
        int aleatorio = obtenerEnteroAleatorio(1, 100);
        return probabilidad > 0 ? aleatorio <= probabilidad : false;
    }
    
    public static Fraccion obtenerFraccionEquivalente(Fraccion fraccion, int cotaMultiplicadores[]){
        validarCota(cotaMultiplicadores);
        int multiplicador = obtenerEnteroAleatorio(cotaMultiplicadores[0], cotaMultiplicadores[1]);
        Fraccion equivalente = new Fraccion();
        equivalente.setNumerador(fraccion.getNumerador()*multiplicador);
        equivalente.setDenominador(fraccion.getDenominador()*multiplicador);
        return equivalente;
    }
    
    public static void validarCota(int [] cota){
        if(cota == null || cota.length<2){
            throw new RuntimeException("Las cotas configuradas deben tener límite "
                    + "inferior y superior. Encontrada: "+Arrays.toString(cota));
        }
    }
    
    public static void validarPorcentaje(int porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new RuntimeException("Los porcentajes de probabilidad configurados deben estar entre 0 y 100. "
                    + "Encontrado: "+porcentaje);
        }
    }
    
    public static String aString(SingularPlural obj, boolean plural){
        return plural ? obj.getPlural() : obj.getSingular();
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
    public static   double redondea(double valor, int decimales) {
        if (decimales < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
