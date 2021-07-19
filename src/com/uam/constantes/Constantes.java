package com.uam.constantes;

/**
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class Constantes {

    public static final String HTML_INICIO_COMENTARIO = "<!--";
    public static final String HTML_FIN_COMENTARIO = "-->";
    public static final String TAG_CLOZE_INICIO_FRACCION = "$$\\displaystyle\\frac";
    public static final String TAG_CLOZE_FIN_SECCION = "$$";
    //agregar funcionalidad XML
    public static String XML_PREFIJO = "<!-- question: $COMENTARIO$  -->\n"
            + "  <question type=\"cloze\">\n"
            + "    <name>\n"
            + "      <text>$COMENTARIO$</text>\n"
            + "    </name>\n"
            + "    <questiontext format=\"html\">\n"
            + "      <text><![CDATA[";
    public static String XML_SUFIJO = "]]></text>\n"
            + "    </questiontext>\n"
            + "    <generalfeedback format=\"html\">\n"
            + "      <text><![CDATA["
            + "<span style=\"color: #000000; font-size: large;\"><strong>Instrucciones para hacer autoevaluaciones y poder presentar examen de unidad.</strong></span><br/><br/>"
            + "a).-"
            + " Nos interesa que usted domine el\n"
            + "ejercicio aquí mostrado; para ello tendrá que seguir un procedimiento\n"
            + "detallado, similar al que ofrece Symbolab. Cualquier duda, consulte a cualquier\n"
            + "profesor de galoisenlinea, en los siguientes lugares y horarios:&nbsp; "
            + "<a href=\"http://galois.azc.uam.mx/mate/propaganda/horarios.pdf\" target=\"_blank\">"
            + "<img src=\"http://galois.azc.uam.mx/mate/imagenes/entrar.gif\" alt=\"\" width=\"80\" "
            + "height=\"64\" role=\"presentation\" class=\"img-responsive atto_image_button_middle\"></a><br/>"
            + "<p>b).-"
            + "Realiza tantas autoevaluaciones como tú lo necesites. Entre más mejor !!!</p>"
            + "<br/><br/><span style=\"color: #000000; font-size: x-large\"><strong>SOLUCIÓN:</strong></span><br/>"
//            + "<center><br/><span style=\"color: #000000; font-size: large\">$SOLUCION$</span></center>"
            + "<br/><span style=\"color: #000000; font-size: large\">$SOLUCION$</span>"
            + "<span style=\"color: #ff0000; font-size: large\"><strong>PROCEDIMIENTO DE SOLUCION PASO A PASO</strong></span>"
            + "<br/><br/>En este ejercicio y con objeto de que uses uno de los mejores Sistemas Algebraicos Computacionales (CAS) llamado "
            + "<span style=\"color: #ff0000\"><strong>Symbolab</strong></span>, obtendrás el procedimiento de solución de este ejercicio paso a paso.<br/>"
            + "Es importante que analice el procedimiento dado por Symbolab; en examen tendrás que hacer el procedimiento detallado de algún ejercicio similar.<br/><br/>"
            + "Si requieres revisión en algún punto de tu desarrollo en papel, muestra tu procedimiento detallado a algún profesor de galoisenlinea y "
            + "con mucho gusto aclararemos tus dudas. <br/>"
            + "<ul style=\"list-style-type:square\"><li>Click en cada fórmula en azul y copia el texto:<br><br>"
            + "<center><span style=\"color:#ff0000; fontsize:medium;\"><strong>La derivada de la función $$f(x)$$ es:</strong></span><br/><br/>"
            + "<span style=\"color: #0000ff; font-size:x-large;\"><strong> "
            + "\\frac{d}{dx}\\left($EXPRESION$ \\right)"
            + "</strong></span></center></li><br/>"
            + "<li>Click en icono de Symbolab:<a title=\"Symbolab\" href=\"https://www.symbolab.com/solver\" target=\"_blank\">"
            + "<img title=\"Symbolab\" style=\"width: 100px; height: 70px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/propaganda/symbolab.jpg\" border=\"0\" height=\"40\" hspace=\"0\" width=\"40\"></a></li>"
            + "<li>Pega en la ventana de entrada de datos de Symbolab tu texto copiado. Allí obtendrás la solución del ejercicio paso a paso.</li></ul>"
            + "<p style=\"text-align: left;\"><span style=\"color: rgb(255, 0, 0); font-size: x-large;\"><strong>Biblioteca en galoisenlinea:</strong></span>&nbsp; &nbsp;&nbsp;<strong style=\"text-align: center; color: rgb(0, 0, 0); font-size: x-large;\">Consulta material relacionado a este ejercicio aquí:&nbsp;&nbsp;<a href=\"http://galois.azc.uam.mx/mate/DIFERENCIAL/autoevaluacion1/index.html\" target=\"_blank\"><img src=\"http://galois.azc.uam.mx/mate/imagenes/folder3.webp\" alt=\"\" width=\"100\" height=\"75\" role=\"presentation\" class=\"img-responsive atto_image_button_text-bottom\"></a>&nbsp; &nbsp;&nbsp;</strong></p>"
            + "<center>\n"
            + "</center>"
            + "<strong>\n"
            + "\n"
            + "\t\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "\n"
            + "<span style=\"color: #ff0000; font-size: large;\"><strong>\n"
            + "LIBRO DE TEXTO Y SOLUCIONARIO AL LIBRO DE TEXTO:</strong></span><br>\n"
            + "\t<br>\n"
            + "TEXTO: Para teoría y ejercicios consulta el libro: George B. Thomas, Jr. Decimosegunda edición \n" +
            "Massachusetts Institute of Technology.\n" +
            "<a title=\"matematicas en galoisenlinea \" href=\"http://galois.azc.uam.mx/mate/diferencial/thomas.jpg\" target=\"_blank\"><img title=\"c\" style=\"width: 90px; height: 100px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/diferencial/thomas.jpg\" border=\"0\" height=\"50\" hspace=\"0\" width=\"50\"></a>\n" +
            "<br><br>&nbsp;\n" +
            "<br>\n" +
            "SOLUCIONARIO AL TEXTO: Consulta el libro: THOMAS' CALCULUS, Solution Manual,  Twelfth Edition.\n" +
            "<a title=\"matematicas en galoisenlinea \" href=\"http://galois.azc.uam.mx/mate/diferencial/solucionariothomas.jpg\" target=\"_blank\"><img title=\"c\" style=\"width: 90px; height: 100px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/diferencial/solucionariothomas.jpg\" border=\"0\" height=\"50\" hspace=\"0\" width=\"50\"></a>\n" +
            "<br><br>&nbsp;\n" +
            "<br>\n" +
            "<br>\n" +
            "<br>\n" +
            "<span style=\"color: #ff0000; font-size: large;\"><strong>\n" +
            "CAS (Sistemas algebraicos computacionales):</strong></span><br>\n" +
            "\n" +
            "En las autoevaluaciones, una vez que realices cada ejercicio, te sugerimos usar cualquiera de los \n" +
            "siguientes Sistemas Algebraicos Computacionales (CAS) para comprobar tus cálculos. \n" +
            "<br>\n" +
            "En exámenes no podrás usar ninguno de estos CAS.   Click en iconos para abrir CAS.<br>&nbsp;\n" +
            "<br>\n" +
            "<a title=\"Symbolab\" href=\"https://www.symbolab.com/solver\" target=\"_blank\"><img title=\"Symbolab\" style=\"width: 120px; height: 45px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/propaganda/symbolab.jpg\" border=\"0\" height=\"40\" hspace=\"0\" width=\"40\"></a>\n" +
            "&nbsp;&nbsp;\n" +
            "<a title=\"Mathway\" href=\"https://www.mathway.com/\" target=\"_blank\"><img title=\"Mathway\" style=\"width: 150px; height: 45px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/imagenes/mathway.png\" border=\"0\" height=\"40\" hspace=\"0\" width=\"40\"></a>\n" +
            "&nbsp;\n" +
            "<a title=\"Wiris\" href=\"http://www.wiris.net/demo/wiris/manual/es/html/tour/index.html\" target=\"_blank\"><img title=\"Wiris\" style=\"width: 100px; height: 50px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/imagenes/wiris.jpg\" border=\"0\" height=\"40\" hspace=\"0\" width=\"40\"></a>\n" +
            "&nbsp;\n" +
            "<a title=\"Wolfram Alpha\" href=\"https://www.wolframalpha.com/\" target=\"_blank\"><img title=\"Wolfram Alpha\" style=\"width: 150px; height: 50px;\" alt=\"c\" src=\"http://galois.azc.uam.mx/mate/imagenes/wolframalpha.png\" border=\"0\" height=\"40\" hspace=\"0\" width=\"40\"></a><br><br>&nbsp;</strong>"
            + "    ]]></text>\n"
            + "    </generalfeedback>\n"
            + "    <penalty>0.3333333</penalty>\n"
            + "    <hidden>0</hidden>\n"
            + "  </question>";
    public static String GLOBAL_XML_PREFIJO = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<quiz>\n"
            + "<!-- question: 0  -->\n"
            + "  <question type=\"category\">\n"
            + "    <category>\n"
            + "        <text>$module$/Por defecto en Derivadas Test Iván</text>\n"
            + "    </category>\n"
            + "  </question>\n"
            + "";
    public static String GLOBAL_XML_SUFIJO = "</quiz>";

}
