
package com.mycompany.interprete;

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
            int linea){
        tokens.add(new Token(tipo,lexema,literal,linea));
    }
    
    private String ponerCaracterFin(){
        return source + ' ';
    }

    List<Token> scanTokens(){
        int est=0;
        int inicio=0;
        int avance=0;
        char c;
        String lexema,s;
        String fuente = ponerCaracterFin();
        TipoToken tipo;
        Object literal;
        
        for (int i=0; i<fuente.length();) {
            c=fuente.charAt(i);
            s = String.valueOf(c);
            switch (est){
                case 0:
                    if(s.matches("[a-zA-Z_]")){
                        est=10; avance=++i;
                    }
                    else if (s.matches("[0-9]")){
                        est=11; avance=++i;
                    }
                    else{
                        switch (c) {
                            case '<':
                                est=1; avance=++i;
                                break;
                            case '=':
                                est=2; avance=++i;
                                break;
                            case '>':
                                est=3; avance=++i;
                                break;
                            case '(': case ')': case '{': case '}': case ',':
                            case '.': case ';': case '-': case '+': case '*':
                                est=20; avance=++i;
                                break;
                            case '/':
                                est=4; avance=++i;
                                break;
                            case '"':
                                est=8; avance=++i;
                                break;
                            case '!':
                                est=9; avance=++i;
                                break;
                            case ' ': case '\t': case '\n': case '\r':
                                if(c=='\r')
                                    linea++;
                                est=17; i++;
                                break;
                            default:
                                est=24;
                                break;
                        }
                    }
                    break;
                case 1: case 2: case 3:
                    if(c=='='){
                        est=20; avance=++i;
                    }
                    else{
                        est=20;
                    }
                    break;
                case 4:
                    if(c=='/'){
                        est=5; avance=++i;
                    }
                    else if(c=='*'){
                        est=6; avance=++i;
                    }
                    else{
                        est=20;
                    }
                    break;
                case 5:
                    if(c=='\n'){
                        est=0; inicio=avance=++i;
                    }
                    else{
                        est=5; i++;
                    }
                    break;
                case 6:
                    if(i==source.length()){
                        est=24; break;
                    }
                    if(c=='*')
                        est=7;
                    else
                        est=6;
                    avance=++i;
                    break;
                case 7:
                    if(i==source.length()){
                        est=24; break;
                    }
                    if(c=='/'){
                        est=0; inicio=avance=++i;
                    }
                    else{
                        est=6; i++;
                    }
                    break;
                case 8:
                    if(i==source.length()){
                        est=24; break;
                    }
                    if(c=='"')
                        est=22;
                    else
                        est=8;
                    avance=++i;
                    break;
                case 9:
                    if(c=='='){
                        est=20; avance=++i;
                    }
                    else{
                        est=20;
                    }
                    break;
                case 10:
                    if(s.matches("[a-zA-Z_0-9]")){
                        est=10; avance=++i;
                    }
                    else{
                        est=21;
                    }
                    break;
                case 11:
                    if (s.matches("[0-9]")){
                        est=11; avance=++i;
                    }
                    else if(c=='.'){
                        est=12; avance=++i;
                    }
                    else if(c=='E'){
                        est=14; avance=++i;
                    }
                    else{
                        est=23;
                    }
                    break;
                case 12:
                    if(s.matches("[0-9]")){
                        est=13; avance=++i;
                    }
                    else
                        est=24;
                    break;
                case 13:
                    if(s.matches("[0-9]")){
                        est=13; avance=++i;
                    }
                    else if(c=='E'){
                        est=14; avance=++i;
                    }
                    else{
                        est=23;
                    }
                    break;
                case 14:
                    if(c=='+'||c=='-'){
                        est=15; avance=++i;
                    }
                    else if(s.matches("[0-9]")){
                        est=16; avance=++i;
                    }
                    else
                        est=24;
                    break;
                case 15:
                    if(s.matches("[0-9]")){
                        est=16; avance=++i;
                    }
                    else
                        est=24;
                    break;
                case 16:
                    if(s.matches("[0-9]")){
                        est=16; avance=++i;
                    }
                    else{
                        est=23;
                    }
                    break;
                case 17:
                    if(c==' '||c=='\t'||c=='\n'||c=='\r'){
                        if(c=='\r')
                            linea++;
                        est=17; i++;
                    }
                    else{
                        est=0; inicio=avance=i;
                    }
                    break;
                case 20:
                    lexema = fuente.substring(inicio, avance);
                    tipo = simbolos.get(lexema);
                    generarToken(tipo,lexema,null,linea);
                    est=0; inicio=avance=i;
                    break;
                case 21:
                    lexema = fuente.substring(inicio,avance);
                    tipo = palabrasReservadas.getOrDefault(lexema, 
                            TipoToken.ID);
                    generarToken(tipo,lexema,null,linea);
                    est=0; inicio=avance=i;
                    break;
                case 22:
                    lexema = fuente.substring(inicio,avance);
                    inicio++; avance--;
                    literal = fuente.substring(inicio, avance);
                    generarToken(TipoToken.CADENA,lexema,literal,linea);
                    est=0; inicio=avance=i;
                    break;
                case 23:
                    lexema = fuente.substring(inicio,avance);
                    double num = Double.parseDouble((String) lexema);
                    generarToken(TipoToken.NUM,lexema,num,linea);
                    est=0; inicio=avance=i;
                    break;
                case 24:
                    Interprete.error(linea, "Caracter: " + i + ": " + c);
                    break;
            }
        }
        generarToken(TipoToken.EOF,"",null,linea);
        return tokens;
    }
}