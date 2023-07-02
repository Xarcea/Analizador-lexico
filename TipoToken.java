package mx.ipn.interprete;

/**
 *
 * @author xavier arce
 */

public enum TipoToken {
    // Crear un tipoToken por palabra reservada
    // Crear un tipoToken: identificador, una cadena y numero
    // Crear un tipoToken por cada "Signo del lenguaje" (ver clase Scanner)

    // Identificador, cadena y número
    ID, CADENA, NUM,
    // Palabras clave
    AND, CLASS, ELSE, FALSE, FOR, FUN, IF, NULL, OR, PRINT, RETURN,
    SUPER, THIS, TRUE, VAR, WHILE,
    // Signos
    PAR_AP, PAR_CIE, LLAVE_AP, LLAVE_CIE, COMA, PUNTO, PUNT_COMA, MENOS, MAS,
    MULT, NO, DIFERENCIA, ASIG, IGUAL, LT, LE, GT, GE, DIV, 
    // Final de cadena
    EOF
}