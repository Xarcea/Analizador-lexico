package mx.ipn.interprete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xavier arce
 */
public class Scanner {
    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    private int linea = 1;

    private static final Map <String, TipoToken> palabrasReservadas;
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and", TipoToken.Y);
        palabrasReservadas.put("class", TipoToken.CLASE);
        palabrasReservadas.put("also", TipoToken.ADEMAS);
        palabrasReservadas.put("false", TipoToken.FALSO);
        palabrasReservadas.put("for", TipoToken.PARA);
        palabrasReservadas.put("fun", TipoToken.FUN); //definir funciones
        palabrasReservadas.put("if", TipoToken.SI);
        palabrasReservadas.put("null", TipoToken.NULO);
        palabrasReservadas.put("or", TipoToken.O);
        palabrasReservadas.put("print", TipoToken.IMPRIMIR);
        palabrasReservadas.put("return", TipoToken.RETORNAR);
        palabrasReservadas.put("super", TipoToken.SUPER);
        palabrasReservadas.put("this", TipoToken.ESTE);
        palabrasReservadas.put("true", TipoToken.VERDADERO);
        palabrasReservadas.put("var", TipoToken.VAR); //definir variables
        palabrasReservadas.put("while", TipoToken.MIENTRAS);
    }
    
    private static final Map <String, TipoToken> simbolos;
    static {
        simbolos = new HashMap<>();
        simbolos.put("(", TipoToken.PAR_AP);
        simbolos.put(")", TipoToken.PAR_CIE);
        simbolos.put("{", TipoToken.LLAVE_AP);
        simbolos.put("}", TipoToken.LLAVE_CIE);
        simbolos.put(",", TipoToken.COMA);
        simbolos.put(".", TipoToken.PUNTO);
        simbolos.put(";", TipoToken.PUNT_COMA);
        simbolos.put("-", TipoToken.MENOS);
        simbolos.put("+", TipoToken.MAS);
        simbolos.put("*", TipoToken.MULT);
        simbolos.put("!", TipoToken.NO);
        simbolos.put("!=", TipoToken.DIFERENCIA);
        simbolos.put("=", TipoToken.ASIG);
        simbolos.put("==", TipoToken.IGUAL);
        simbolos.put("<", TipoToken.LT);
        simbolos.put("<=", TipoToken.LE);
        simbolos.put(">",TipoToken.GT);
        simbolos.put(">=",TipoToken.GE);
        simbolos.put("/",TipoToken.DIV);
    }

    public Scanner(String source){
        this.source = source;
    }
    
    private void generarToken(TipoToken tipo, String lexema, Object literal,
            int posicion){
        tokens.add(new Token(tipo,lexema,literal,posicion));
    }
    
    //private String ponerCaracterFin(){
    //    return source + ' ';
    //}

    List<Token> scanTokens(){
        int est=0,inicio=0,avance=0,j=1;
        char c;
        String lexema,literal;
        String fuente = source + ' ';//ponerCaracterFin();
        TipoToken tipo;
        
        for (int i=0; i<fuente.length();) {
            c = fuente.charAt(i);
            //s = String.valueOf(c);
            switch (est){
                case 0:
                    if(c=='_' || Character.isAlphabetic(c)){
                        est=10; avance=++i; j++;
                    }
                    else if (Character.isDigit(c)){
                        est=11; avance=++i; j++;
                    }
                    else{
                        switch (c) {
                            case '<':
                                est=1; avance=++i; j++;
                                break;
                            case '=':
                                est=2; avance=++i; j++;
                                break;
                            case '>':
                                est=3; avance=++i; j++;
                                break;
                            case '(': case ')': case '{': case '}': case ',':
                            case '.': case ';': case '-': case '+': case '*':
                                est=20; avance=++i; j++;
                                break;
                            case '/':
                                est=4; avance=++i; j++;
                                break;
                            case '"':
                                est=8; avance=++i; j++;
                                break;
                            case '!':
                                est=9; avance=++i; j++;
                                break;
                            case ' ': case '\t': case '\n': case '\r':
                                if(c=='\r'){
                                    linea++; j=1;
                                }
                                est=17; i++; j++;
                                break;
                            default:
                                Interprete.error(linea, "Error en la posición " + j 
                                + ". Símbolo no válido.");
                                inicio=avance=++i;
                                est=0; j++;
                                break;
                        }
                    }
                    break;
                case 1: case 2: case 3:
                    if(c=='='){
                        est=20; avance=++i; j++;
                    }
                    else{
                        est=20;
                    }
                    break;
                case 4:
                    if(c=='/'){
                        est=5; avance=++i; j++;
                    }
                    else if(c=='*'){
                        est=6; avance=++i; j++;
                    }
                    else{
                        est=20;
                    }
                    break;
                case 5:
                    if(c=='\n'){
                        est=0; inicio=avance=++i; j++;
                    }
                    else{
                        est=5; i++; j++;
                    }
                    break;
                case 6:
                    if(i==source.length()){
                        Interprete.error(linea, "Error en la posición " + j 
                                + ". Se esperaba fin de comentario.");
                        i++; break;
                    }
                    if(c=='*')
                        est=7;
                    else
                        est=6;
                    avance=++i; j++;
                    break;
                case 7:
                    if(i==source.length()){
                        Interprete.error(linea, "Error en la posición " + j 
                                + ". Se esperaba / .");
                        i++; break;
                    }
                    if(c=='/'){
                        est=0; inicio=avance=++i; j++;
                    }
                    else{
                        est=6; i++; j++;
                    }
                    break;
                case 8:
                    if(i==source.length()){
                        Interprete.error(linea, "Error en la posición " + j 
                                + ". Se esperaba \" ."); 
                        i++; break;
                    }
                    if(c=='"')
                        est=22;
                    else
                        est=8;
                    avance=++i; j++;
                    break;
                case 9:
                    if(c=='='){
                        est=20; avance=++i; j++;
                    }
                    else{
                        est=20;
                    }
                    break;
                case 10:
                    if(c=='_' || Character.isAlphabetic(c) || Character.isDigit(c)){
                        est=10; avance=++i; j++;
                    }
                    else{
                        est=21;
                    }
                    break;
                case 11:
                    if(Character.isDigit(c)){
                        est=11; avance=++i; j++;
                    }
                    else if(c=='.'){
                        est=12; avance=++i; j++;
                    }
                    else if(c=='E'){
                        est=14; avance=++i; j++;
                    }
                    else{
                        est=23;
                    }
                    break;
                case 12:
                    if(Character.isDigit(c)){
                        est=13; avance=++i; j++;
                    }
                    else{
                        Interprete.error(linea, "Error en la posición " + j 
                                + ". Se esperaba un dígito.");
                        lexema = fuente.substring(inicio,avance-1);
                        double num = Double.parseDouble((String) lexema);
                        generarToken(TipoToken.NUM,lexema,num,inicio);
                        est=0; inicio=avance=--i; j--;
                    }
                    break;
                case 13:
                    if(Character.isDigit(c)){
                        est=13; avance=++i; j++;
                    }
                    else if(c=='E'){
                        est=14; avance=++i; j++;
                    }
                    else{
                        est=23;
                    }
                    break;
                case 14:
                    if(c=='+'||c=='-'){
                        est=15; avance=++i; j++;
                    }
                    else if(Character.isDigit(c)){
                        est=16; avance=++i; j++;
                    }
                    else{
                        Interprete.error(linea, "Error en la posición " + j 
                                + ". Se esperaba un dígito, + o - .");
                        lexema = fuente.substring(inicio,avance-1);
                        double num = Double.parseDouble((String) lexema);
                        generarToken(TipoToken.NUM,lexema,num,inicio);
                        est=0; inicio=avance=--i; j--;
                    }
                    break;
                case 15:
                    if(Character.isDigit(c)){
                        est=16; avance=++i; j++;
                    }
                    else{
                        Interprete.error(linea, "Error en la posición " + j 
                                + ". Se esperaba un dígito.");
                        lexema = fuente.substring(inicio,avance-2);
                        double num = Double.parseDouble((String) lexema);
                        generarToken(TipoToken.NUM,lexema,num,inicio);
                        est=0; i-=2; inicio=avance=i; j-=2;
                    }
                    break;
                case 16:
                    if(Character.isDigit(c)){
                        est=16; avance=++i; j++;
                    }
                    else
                        est=23;
                    break;
                case 17:
                    if(c==' '||c=='\t'||c=='\n'||c=='\r'){
                        if(c=='\r'){
                            linea++; j=1;
                        }
                        est=17; i++; j++;
                    }
                    else{
                        est=0; inicio=avance=i;
                    }
                    break;
                case 20:
                    lexema = fuente.substring(inicio, avance);
                    tipo = simbolos.get(lexema);
                    generarToken(tipo,lexema,null,inicio);
                    est=0; inicio=avance=i;
                    break;
                case 21:
                    lexema = fuente.substring(inicio,avance);
                    tipo = palabrasReservadas.getOrDefault(lexema, 
                            TipoToken.ID);
                    generarToken(tipo,lexema,null,inicio);
                    est=0; inicio=avance=i;
                    break;
                case 22:
                    lexema = fuente.substring(inicio, avance);
                    //inicio++; avance--;
                    literal = fuente.substring(inicio+1, avance-1);
                    generarToken(TipoToken.CADENA,lexema,literal,inicio);
                    est=0; inicio=avance=i;
                    break;
                case 23:
                    lexema = fuente.substring(inicio,avance);
                    double num = Double.parseDouble((String) lexema);
                    generarToken(TipoToken.NUM,lexema,num,inicio);
                    est=0; inicio=avance=i;
                    break;
            }
        }
        generarToken(TipoToken.EOF,"",null,source.length());
        return tokens;
    }
}