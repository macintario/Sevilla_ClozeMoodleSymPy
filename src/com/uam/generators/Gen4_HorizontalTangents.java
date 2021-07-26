package com.uam.generators;
/*
 *
 *
 *
 * */

import com.uam.auxiliar.GeneradorReactivoCloze;
import com.uam.auxiliar.SolucionaSimbolico;
import com.uam.data.DatosReactivos;
import com.uam.executor.EjecutadorGeneradorXML;
import com.uam.utilidades.Utilidades;

import static com.uam.constantes.Constantes.XML_PREFIJO;
import static com.uam.constantes.Constantes.XML_SUFIJO;
import static com.uam.utilidades.Utilidades.maximoComunDivisor;

public class Gen4_HorizontalTangents implements GeneradorReactivoCloze {
    /**
     * El número de dígitos para el número de reactivo que se pondrá como
     * comentario del reactivo. e.g. si el número de posiciones es 3 entonces el
     * comentario que tendrá el primer reactivo será
     * "<!--Reactivo tangentesHorizontalesGrafica_000-->"
     */
    private static final int POSICIONES_CONTADOR_REACTIVO = 3;
    /**
     * El nombre o ruta absoluta del archivo de salida.
     */
    private static final String NOMBRE_ARCHIVO_SALIDA = "Sevilla_step_by_step_horizontal_tangents_graph.xml";

    /**
     * El número de reactivos que se generarán y vaciarán al archivo de texto.
     */
    private static final int NUMERO_DE_REACTIVOS = 2;

    /**
     * El texto del reactivo, las variables se encuentran en mayúsculas y
     * encerradas entre signos $. La tildes deben ser colocadas con código utf8.
     */
    private static final String PLANTILLA_REACTIVO
            = "<span style=\"color: #ff0000,2E8B57; font-size: large;\"><strong>\n" +
            "galoisenlinea&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; http://galois.azc.uam.mx </strong></span>\n" +
            "<span style=\"color: #E38E03; font-size: large;\"><strong>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; En este ejercicio encontrarás la recta tangente a una curva en un punto</strong></span> <br><br>"
            + "<span style=\"color: #ff0000; font-size: xx-large;\"><strong>\n"
            + "PROBLEMA:\n"
            + "</strong></span>"
            + "<center><span style=\"color: #0000ff; font-size: x-large;\"><strong>"
            + "Determine los puntos donde la curva<br/><br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "$$\\displaystyle f(x)=$EXPRESION$ $$<br/><br/>tiene una o mas tangentes horizontales.<br/>"
            + "</strong><br/><br/></span><span style=\"color: #ff0000; font-size: x-large;\"><strong>"
            + "<script type=\"math/tex\">\\bullet</script> &nbsp;&nbsp;&nbsp;"
            +" Los puntos en donde las tangentes son horizontales son: </strong></span><br/><br/>"
            + "$RESPUESTA$"
            + "</strong></span><br/>"
            + "<span style=\"color: #000000; font-size: medium;\"><strong>"
            + "Usted deberá calcular la derivada $$f(x)$$, igualarla a cero y encontrar las raices, para luego escribir las coordenadas de cada punto<br/>"
            + "<span style=\"color: #FF4000; font-size: medium;\"><strong>\n" +
            "¿ Revisión de su ejercicio ? Escribirás en papel el procedimiento detallado que muestre cómo obtuviste tus respuestas. \n" +
            "</strong></span>";

    private static final int[] COTA_CONSTANTE_D = {-5, 5};
    private static final int[] COTA_CONSTANTE_G = {3, 5};
    private static final int[] COTA_CONSTANTE_H = {2, 5};
    private static final int[] COTA_R = {-4, 4};

    private static final String EXPRESION = "$CONSTANTEA$x^3+$CONSTANTEB$x^2+$CONSTANTEC$x+$CONSTANTED$";
    private String RESPUESTA= "$$\\displaystyle P_1($${1:SHORTANSWER:=$RESPUESTA_A$}$$,$${1:SHORTANSWER:=$RESPUESTA_B$}$$)$$ <br/>"+
            "$$\\displaystyle P_2($${1:SHORTANSWER:=$RESPUESTA_C$}$$,$${1:SHORTANSWER:=$RESPUESTA_D$}$$)$$ <br/>";
    private String CAJAS_RESPUESTA = "$$A=$${1:SHORTANSWER:=$RESPUESTA_A$} <br/> $$B=$${1:SHORTANSWER:=$RESPUESTA_B$}) <br/> "
            + "<span style=\"color: #ff0000; font-size: x-large;\"><strong>"
            + "<script type=\"math/tex\">\\bullet</script> &nbsp;&nbsp;&nbsp; Los números $$A,B$$ en este orden "
            + "y que dan solución correcta al ejercicio son: </strong></span>"
            + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            +" {20:SHORTANSWER:=$RESPUESTA_A$,$RESPUESTA_B$"
            + "}</center> <br>"
            + "</center>";

    /**
     * El comentario que se pondrá a cada reactivo para etiquetarlo, el sufijo
     * sera el número de reactivo. Éste se insertará como un comentario html
     * para que no sea visible para el usuario. El lugar de inserción de este
     * comentario dentro del texto del reactivo esta dado por la variable
     * $COMENTARIO$ en la plantilla del reactivo.
     *
     */
    private static final String COMENTARIO_REACTIVO_PREFIJO = "Sevilla_step_by_step_horizontal_tangents_graph_";
    private static final String SEPARADOR_REACTIVOS = "\r\n";

    @Override
    public String generarReactivoCloze(int numeroReactivo) {
        String solucion = "";
        //Generación de variables aleatorias con parámetros de ejecución
        Integer r1 = Utilidades.obtenerEnteroAleatorioDistintoDe(COTA_R[0], COTA_R[1],0);
        Integer r2;
        do {
            r2 = Utilidades.obtenerEnteroAleatorioDistintoDe(COTA_R[0], COTA_R[1], 0);
        }while(r2.equals(r1) || r2.equals(-r1) );
        if(r2>r1){
            Integer tmp = r2;
            r2=r1;
            r1=tmp;
        }
        Integer constanteA = 2;
        Integer constanteB = (r1+r2)*3;
        Integer constanteC = 6*r1*r2;
        Integer constanteD =  Utilidades.obtenerEnteroAleatorioDistintoDe(COTA_CONSTANTE_D[0], COTA_CONSTANTE_D[1],0);
        Integer divisor = maximoComunDivisor(maximoComunDivisor(maximoComunDivisor(constanteA,constanteB),constanteC),constanteD);
        if(divisor<0) divisor = -divisor;
        constanteA/=divisor;
        constanteB/=divisor;
        constanteC/=divisor;
        constanteD/=divisor;
        Integer constanteE = constanteA;
        Integer constanteF = constanteC;
        Integer constanteG = Utilidades.obtenerEnteroAleatorio(COTA_CONSTANTE_G[0], COTA_CONSTANTE_G[1]);
        Integer constanteH = Utilidades.obtenerImparAleatorioDistintoDe(COTA_CONSTANTE_H[0], COTA_CONSTANTE_H[1], constanteF);
        String comentarioReactivo = Utilidades.generaComentario(COMENTARIO_REACTIVO_PREFIJO, numeroReactivo, POSICIONES_CONTADOR_REACTIVO);
        Integer respuestaA = -r1;
        Integer respuestaB = -constanteA*r1*r1*r1+constanteB*r1*r1-constanteC*r1+constanteD;
        Integer respuestaC = -r2;
        Integer respuestaD = -constanteA*r2*r2*r2+constanteB*r2*r2-constanteC*r2+constanteD;
        Integer respuestaE = constanteA;
        Integer respuestaF = constanteC;
        Integer respuestaG = constanteA;
        Integer respuestaH = 2;
        Integer respuestaJ = constanteC;
        Integer respuestaK = constanteA;
        Integer respuestaL = 3*constanteA-1;
        Integer respuestaM = constanteA;
        Integer respuestaN = constanteB;
        Integer respuestaP = constanteA;

        String parVariables = DatosReactivos.obtenerParesVariables();
        String variableIndependiente = parVariables.substring(0, 1);
        String variableDependiente = parVariables.substring(1, 2);

        //Sustitución de las variables por sus valores en el texto del reactivo
        String reactivo = XML_PREFIJO + PLANTILLA_REACTIVO + XML_SUFIJO;
        String expresion = EXPRESION;

        expresion = expresion.replace("$CONSTANTEA$", constanteA.toString());
        expresion = expresion.replace("$CONSTANTEB$", constanteB.toString());
        expresion = expresion.replace("$CONSTANTEC$", constanteC.toString());
        expresion = expresion.replace("$CONSTANTED$", constanteD.toString());
        expresion = expresion.replace("$CONSTANTEE$", constanteE.toString());
        expresion = expresion.replace("$CONSTANTEF$", constanteF.toString());
        expresion = expresion.replace("$CONSTANTEG$", constanteG.toString());
        expresion = expresion.replace("$CONSTANTEH$", constanteH.toString());
        expresion = expresion.replace("+-", "-");

        reactivo = reactivo.replace("$RESPUESTAS$", CAJAS_RESPUESTA);
        reactivo = reactivo.replace("$RESPUESTA$", RESPUESTA);
        //cambiar el problema para punto  tangente
        reactivo = reactivo.replace("\\frac{d}{dx}\\left($EXPRESION$ \\right)",
                "puntos extremos de f\\left(x\\right)=$EXPRESION$");
        reactivo = reactivo.replace("<strong>La derivada de la función $$f(x)$$ es:</strong>",
                "<strong>Los puntos donde la derivada se anula son:</strong>");
        reactivo = reactivo.replace("$EXPRESION$", expresion);
        reactivo = reactivo.replace("$COMENTARIO$", comentarioReactivo);
        reactivo = reactivo.replace("$VARIABLE_INDEPENDIENTE$", variableIndependiente);
        reactivo = reactivo.replace("$VARIABLE_DEPENDIENTE$", variableDependiente);
        reactivo = reactivo.replace("$RESPUESTA_A$", respuestaA.toString());
        reactivo = reactivo.replace("$RESPUESTA_B$", respuestaB.toString());
        reactivo = reactivo.replace("$RESPUESTA_C$", respuestaC.toString());
        reactivo = reactivo.replace("$RESPUESTA_D$", respuestaD.toString());
        reactivo = reactivo.replace("$RESPUESTA_E$", respuestaE.toString());
        reactivo = reactivo.replace("$RESPUESTA_F$", respuestaF.toString());
        reactivo = reactivo.replace("$RESPUESTA_G$", respuestaG.toString());
        reactivo = reactivo.replace("$RESPUESTA_H$", respuestaH.toString());
        reactivo = reactivo.replace("$RESPUESTA_J$", respuestaJ.toString());
        reactivo = reactivo.replace("$RESPUESTA_K$", respuestaK.toString());
        reactivo = reactivo.replace("$RESPUESTA_L$", respuestaL.toString());
        reactivo = reactivo.replace("$RESPUESTA_M$", respuestaM.toString());
        reactivo = reactivo.replace("$RESPUESTA_N$", respuestaN.toString());
        reactivo = reactivo.replace("$RESPUESTA_P$", respuestaP.toString());
        reactivo = reactivo.replace("1x", "x");

        solucion = SolucionaSimbolico.tangentesHorizontalesGraficaJSX(expresion, "x");

        reactivo = reactivo.replace("$SOLUCION$", solucion);

        //Concatenando el separador de reactivos
        reactivo = reactivo.concat(SEPARADOR_REACTIVOS);
        return reactivo;
    }

    public static void main(String[] args) {
        EjecutadorGeneradorXML.generarReactivos(NOMBRE_ARCHIVO_SALIDA, NUMERO_DE_REACTIVOS, new Gen4_HorizontalTangents());
    }


}
