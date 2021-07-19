package com.uam.auxiliar;

import com.uam.constantes.Constantes;
import com.uam.utilidades.Utilidades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class Fraccion implements Comparable<Fraccion> {

    private static final int MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO = 500;
    private int numerador;
    private int denominador;
    private int[] cotaNumerador;
    private int[] cotaDenominador;

    public int getNumerador() {
        return numerador;
    }

    public void setNumerador(int numerador) {
        this.numerador = numerador;
    }

    public int getDenominador() {
        return denominador;
    }

    public void setDenominador(int denominador) {
        this.denominador = denominador;
    }

    public Fraccion() {
        this.numerador = 0;
        this.denominador = 1;
    }

    public Fraccion(int numerador, int denominador) {
        this.numerador = numerador;
        this.denominador = denominador;
    }

    /**
     * Genera una nueva fracción utilizando denominador y numerador aleatorios
     * acotados por las cotas cotaDenominador y cotaNumerador respectivamente.
     * Se asegura que el numerador y denomiador cumplan con la relacion
     * relacionNumDenom que se especifica.
     *
     * @param cotaNumerador
     * @param cotaDenominador
     * @param relacionNumDenom La relación que hay entre numerador y denominador
     * al generar la fracción aleatoria.
     */
    public Fraccion(int[] cotaNumerador, int[] cotaDenominador, Relacion relacionNumDenom) {
        this.cotaDenominador = cotaDenominador;
        this.cotaNumerador = cotaNumerador;
        Utilidades.validarCota(this.cotaDenominador);
        Utilidades.validarCota(this.cotaNumerador);
        Arrays.sort(this.cotaDenominador);
        Arrays.sort(this.cotaNumerador);
        this.generarNumeradorDenominador(relacionNumDenom);
    }

    public static Fraccion sumar(Fraccion a, Fraccion b) {
        Fraccion c = new Fraccion();
        c.numerador = a.numerador * b.denominador + b.numerador * a.denominador;
        c.denominador = a.denominador * b.denominador;
        return c;
    }

    /**
     * Genera un nuevo par aleatorio de numerador-denominador utilizando las
     * cotas establecidas al momento de crear esta fracción. Se asegura que el
     * numerador y denominador cumplan con la relación relacionNumDenom que se
     * especifica. Esto es: se cumplirá que numerador RELACION denominador.
     *
     * @param relacionEntreNumeradorYDenominador La relación que se desea que
     * haya entre el numerador y denominador generados.
     */
    public final void generarNumeradorDenominador(Relacion relacionEntreNumeradorYDenominador) {

        switch (relacionEntreNumeradorYDenominador) {
            case MAYOR_QUE:
                this.generarNumeradorMayorQueDenominador();
                break;
            case MAYOR_O_IGUAL_QUE:
                this.generarNumeradorMayorOIgualQueDenominador();
                break;
            case MENOR_QUE:
                this.generarNumeradorMenorQueDenominador();
                break;
            case MENOR_O_IGUAL_QUE:
                this.generarNumeradorMenorOIgualQueDenominador();
                break;
            case SIN_RELACION:
                this.numerador = Utilidades.obtenerEnteroAleatorio(this.cotaNumerador[0], this.cotaNumerador[1]);
                this.denominador = Utilidades.obtenerEnteroAleatorio(this.cotaDenominador[0], this.cotaDenominador[1]);
                break;
        }
    }
    
    private void generarNumeradorMayorQueDenominador() {
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        Integer enteroMenorMasCercano = null;
        do {
            this.numerador = Utilidades.obtenerEnteroAleatorio(this.cotaNumerador[0], this.cotaNumerador[1]);
            enteroMenorMasCercano = Utilidades.obtenerEnteroMenorMasCercanoEnIntervalo(this.cotaDenominador, this.numerador);
            intentos--;
        } while (intentos >= 0 && enteroMenorMasCercano == null);

        if (enteroMenorMasCercano == null) {
            this.excepcionDenominadorNumeradorNoEncontrado(Relacion.MAYOR_QUE);
        }
        this.denominador = Utilidades.obtenerEnteroAleatorio(this.cotaDenominador[0], enteroMenorMasCercano);
    }
    
    private void generarNumeradorMenorQueDenominador() {
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        Integer enteroMayorMasCercano = null;
        do {
            this.numerador = Utilidades.obtenerEnteroAleatorio(this.cotaNumerador[0], this.cotaNumerador[1]);
            enteroMayorMasCercano = Utilidades.obtenerEnteroMayorMasCercanoEnIntervalo(this.cotaDenominador, this.numerador);
            intentos--;
        } while (intentos >= 0 && enteroMayorMasCercano == null);

        if (enteroMayorMasCercano == null) {
            this.excepcionDenominadorNumeradorNoEncontrado(Relacion.MENOR_QUE);
        }
        this.denominador = Utilidades.obtenerEnteroAleatorio(enteroMayorMasCercano, this.cotaDenominador[1]);
    }
    
    private void generarNumeradorMenorOIgualQueDenominador() {
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        Integer enteroMayorOIgualMasCercano = null;
        do {
            this.numerador = Utilidades.obtenerEnteroAleatorio(this.cotaNumerador[0], this.cotaNumerador[1]);
            enteroMayorOIgualMasCercano = Utilidades.obtenerEnteroMayorOIgualMasCercanoEnIntervalo(this.cotaDenominador, this.numerador);
            intentos--;
        } while (intentos >= 0 && enteroMayorOIgualMasCercano == null);

        if (enteroMayorOIgualMasCercano == null) {
            this.excepcionDenominadorNumeradorNoEncontrado(Relacion.MENOR_O_IGUAL_QUE);
        }
        this.denominador = Utilidades.obtenerEnteroAleatorio(enteroMayorOIgualMasCercano, this.cotaDenominador[1]);
    }
    
    private void generarNumeradorMayorOIgualQueDenominador() {
        int intentos = MAXIMOS_INTENTOS_GENERACION_CON_CRITERIO;
        Integer enteroMenorOIgualMasCercano = null;
        do {
            this.numerador = Utilidades.obtenerEnteroAleatorio(this.cotaNumerador[0], this.cotaNumerador[1]);
            enteroMenorOIgualMasCercano = Utilidades.obtenerEnteroMenorMasCercanoEnIntervalo(this.cotaDenominador, this.numerador);
            intentos--;
        } while (intentos >= 0 && enteroMenorOIgualMasCercano == null);

        if (enteroMenorOIgualMasCercano == null) {
            this.excepcionDenominadorNumeradorNoEncontrado(Relacion.MAYOR_O_IGUAL_QUE);
        }
        this.denominador = Utilidades.obtenerEnteroAleatorio(this.cotaDenominador[0], enteroMenorOIgualMasCercano);
    }
    
    private void excepcionDenominadorNumeradorNoEncontrado(Relacion relacion){
        throw new RuntimeException("No se ha podido encontrar un denominador y numerador que cumplan con la relación: "
                + "numerador "+relacion.getString()+" denominador. "
                + "La cota establecida para numerador es ["
                + this.cotaNumerador[0] + "-" + this.cotaNumerador[1] + "]. "
                + "La cota establecida para denominador es ["+
                + this.cotaDenominador[0] + "-" + this.cotaDenominador[1] + "]");
    }
    
    
    /**
     * Resta a - b y simplifica el resultado si la bandera simplificar es true.
     *
     * @param a
     * @param b
     * @param simplificar
     * @return
     */
    public static Fraccion restar(Fraccion a, Fraccion b, boolean simplificar) {
        Fraccion c = new Fraccion();
        c.numerador = a.numerador * b.denominador - b.numerador * a.denominador;
        c.denominador = a.denominador * b.denominador;
        if (simplificar) {
            c.simplificar();
        }
        return c;
    }

    public static Fraccion multiplicar(Fraccion a, Fraccion b) {
        return new Fraccion(a.numerador * b.numerador, a.denominador * b.denominador);
    }

    public static Fraccion inversa(Fraccion a) {
        return new Fraccion(a.denominador, a.numerador);
    }

    public static Fraccion dividir(Fraccion a, Fraccion b) {
        return multiplicar(a, inversa(b));
    }


    private int mcd() {
        int u = Math.abs(this.numerador);
        int v = Math.abs(this.denominador);
        if (v == 0) {
            return u;
        }
        int r;
        while (v != 0) {
            r = u % v;
            u = v;
            v = r;
        }
        return u;
    }

    public Fraccion simplificar() {
        int dividir = mcd();
        this.numerador /= dividir;
        this.denominador /= dividir;
        return this;
    }

    public String enFormatoCloze() {
        if(this.denominador==1){
            return String.valueOf(this.numerador);
        }
        if(this.numerador == this.denominador){
            return "1";
        }
        StringBuilder fraccion = new StringBuilder(Constantes.TAG_CLOZE_INICIO_FRACCION);
        fraccion.append(this.obtenerTextoFraccionCloze(this.numerador, this.denominador));
        fraccion.append(Constantes.TAG_CLOZE_FIN_SECCION);
        return fraccion.toString();
    }

    private String obtenerTextoFraccionCloze(int numerador, int denominador) {
        return new StringBuilder("{").append(numerador).append("}{").append(denominador).append("}").toString();
    }

    @Override
    public int compareTo(Fraccion f) {
        Double a = (double) this.numerador / this.denominador;
        Double b = (double) f.numerador / f.denominador;
        return a.compareTo(b);
    }

    @Override
    public String toString() {
        return this.numerador + "/" + this.denominador;
    }

}


