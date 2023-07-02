package mx.ipn.interprete;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Xavier Arce
 */

public class Parser {
    private final List<Token> tokens;
    private boolean hayErrores = false;
    private Token preanalisis;
    private int i = 0;

    Set<TipoToken> primeroDeclaration = new HashSet<>(Arrays.asList(
        TipoToken.CLASS, TipoToken.FUN, TipoToken.VAR
    ));
    Set<TipoToken> primeroStatement = new HashSet<>(Arrays.asList(
        TipoToken.FOR, TipoToken.IF, TipoToken.PRINT, TipoToken.RETURN, TipoToken.WHILE, 
        TipoToken.LLAVE_AP
    ));
    Set<TipoToken> primeroExpression = new HashSet<>(Arrays.asList(
        TipoToken.NO, TipoToken.MENOS
    ));
    Set<TipoToken> primeroPrimary = new HashSet<>(Arrays.asList(
        TipoToken.TRUE, TipoToken.FALSE, TipoToken.NULL, TipoToken.THIS,
        TipoToken.NUM, TipoToken.CADENA, TipoToken.ID, TipoToken.PAR_AP, 
        TipoToken.SUPER
    ));
    
    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public boolean analizar(){
        primeroExpression.addAll(primeroPrimary);
        primeroStatement.addAll(primeroExpression);
        primeroDeclaration.addAll(primeroStatement);
        preanalisis = tokens.get(i);
        PROGRAM();
        if(!hayErrores && !preanalisis.tipo.equals(TipoToken.EOF)){
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
        else if(!hayErrores && preanalisis.tipo.equals(TipoToken.EOF)){
            //System.out.println("Cadena válida");
            return true;
        }
        return false;
    }

    void PROGRAM(){
        if(primeroDeclaration.contains(preanalisis.tipo)){
            DECLARATION();
        }
    }

    void DECLARATION(){
        if(hayErrores) return;
        switch(preanalisis.tipo){
            case CLASS:
                CLASS_DECL();
                DECLARATION();
            break;
            case FUN:
                FUN_DECL();
                DECLARATION();
            break;
            case VAR:
                VAR_DECL();
                DECLARATION();
            break;
            default:
                if(primeroStatement.contains(preanalisis.tipo)){
                    STATEMENT();
                    DECLARATION();
                }
            break;
        }
    }

    void CLASS_DECL(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.CLASS)){
            coincidir(TipoToken.CLASS);
            coincidir(TipoToken.ID);
            CLASS_INHER();
            coincidir(TipoToken.LLAVE_AP);
            FUNCTIONS();
            coincidir(TipoToken.LLAVE_CIE);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void FUN_DECL(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.FUN)){
            coincidir(TipoToken.FUN);
            FUNCTION();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void VAR_DECL(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.VAR)){
            coincidir(TipoToken.VAR);
            coincidir(TipoToken.ID);
            VAR_INIT();
            coincidir(TipoToken.PUNT_COMA);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void STATEMENT(){
        if(hayErrores) return;
        switch(preanalisis.tipo){
            case FOR:
                FOR_STMT();
            break;
            case IF:
                IF_STMT();
            break;
            case PRINT:
                PRINT_STMT();
            break;
            case RETURN:
                RETURN_STMT();
            break;
            case WHILE:
                WHILE_STMT();
            break;
            case LLAVE_AP:
                BLOCK();
            break;
            default:
                if(primeroExpression.contains(preanalisis.tipo)){
                    EXPR_STMT();
                } else{
                    hayErrores = true;
                    Interprete.error(
                        preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                        ". No se esperaba el token " + preanalisis.tipo
                    );
                }
            break;
        }
    }

    void CLASS_INHER(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.LT)){
            coincidir(TipoToken.LT);
            coincidir(TipoToken.ID);
        }
    }

    void FUNCTIONS(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ID)){
            FUNCTION();
            FUNCTIONS();
        }
    }

    void FUNCTION(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ID)){
            coincidir(TipoToken.ID);
            coincidir(TipoToken.PAR_AP);
            PARAMETERS_OPC();
            coincidir(TipoToken.PAR_CIE);
            BLOCK();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void VAR_INIT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ASIG)){
            coincidir(TipoToken.ASIG);
            EXPRESSION();
        }
    }

    void EXPRESSION(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            ASSIGNMENT();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void FOR_STMT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.FOR)){
            coincidir(TipoToken.FOR);
            coincidir(TipoToken.PAR_AP);
            FOR_STMT_1();
            FOR_STMT_2();
            FOR_STMT_3();
            coincidir(TipoToken.PAR_CIE);
            STATEMENT();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void IF_STMT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.IF)){
            coincidir(TipoToken.IF);
            coincidir(TipoToken.PAR_AP);
            EXPRESSION();
            coincidir(TipoToken.PAR_CIE);
            STATEMENT();
            ELSE_STATEMENT();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void PRINT_STMT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.PRINT)){
            coincidir(TipoToken.PRINT);
            EXPRESSION();
            coincidir(TipoToken.PUNT_COMA);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void RETURN_STMT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.RETURN)){
            coincidir(TipoToken.RETURN);
            RETURN_EXP_OPC();
            coincidir(TipoToken.PUNT_COMA);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void WHILE_STMT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.WHILE)){
            coincidir(TipoToken.WHILE);
            coincidir(TipoToken.PAR_AP);
            EXPRESSION();
            coincidir(TipoToken.PAR_CIE);
            STATEMENT();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void BLOCK(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.LLAVE_AP)){
            coincidir(TipoToken.LLAVE_AP);
            BLOCK_DECL();
            coincidir(TipoToken.LLAVE_CIE);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void EXPR_STMT(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            EXPRESSION();
            coincidir(TipoToken.PUNT_COMA);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void FOR_STMT_1(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.VAR)){
            VAR_DECL();
        } else if(primeroExpression.contains(preanalisis.tipo)){
            EXPR_STMT();
        } else if(preanalisis.tipo.equals(TipoToken.PUNT_COMA)){
            coincidir(TipoToken.PUNT_COMA);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void FOR_STMT_2(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            EXPRESSION();
            coincidir(TipoToken.PUNT_COMA);
        } else if(preanalisis.tipo.equals(TipoToken.PUNT_COMA)){
            coincidir(TipoToken.PUNT_COMA);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void FOR_STMT_3(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            EXPRESSION();
        }
    }

    void ELSE_STATEMENT(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ELSE)){
            coincidir(TipoToken.ELSE);
            STATEMENT();
        }
    }

    void RETURN_EXP_OPC(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            EXPRESSION();
        }
    }

    void BLOCK_DECL(){
        if(hayErrores) return;
        if(primeroDeclaration.contains(preanalisis.tipo)){
            DECLARATION();
            BLOCK_DECL();
        }
    }

    void ASSIGNMENT(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            LOGIC_OR();
            ASSIGNMENT_OPC();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void LOGIC_OR(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            LOGIC_AND();
            LOGIC_OR_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void ASSIGNMENT_OPC(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ASIG)){
            coincidir(TipoToken.ASIG);
            EXPRESSION();
        }
    }

    void LOGIC_AND(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            EQUALITY();
            LOGIC_AND_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void LOGIC_OR_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.OR)){
            coincidir(TipoToken.OR);
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }

    void EQUALITY(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            COMPARISON();
            EQUALITY_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void LOGIC_AND_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.AND)){
            coincidir(TipoToken.AND);
            EQUALITY();
            LOGIC_AND_2();
        }
    }

    void COMPARISON(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            TERM();
            COMPARISON_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void EQUALITY_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.DIFERENCIA)){
            coincidir(TipoToken.DIFERENCIA);
            COMPARISON();
            EQUALITY_2();
        } else if(preanalisis.tipo.equals(TipoToken.IGUAL)){
            coincidir(TipoToken.IGUAL);
            COMPARISON();
            EQUALITY_2();
        }
    }

    void TERM(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            FACTOR();
            TERM_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void COMPARISON_2(){
        if(hayErrores) return;
        switch(preanalisis.tipo){
            case GT:
                coincidir(TipoToken.GT);
                TERM();
                COMPARISON_2();
            break;
            case GE:
                coincidir(TipoToken.GE);
                TERM();
                COMPARISON_2();
            break;
            case LT:
                coincidir(TipoToken.LT);
                TERM();
                COMPARISON_2();
            break;
            case LE:
                coincidir(TipoToken.LE);
                TERM();
                COMPARISON_2();
            break;
            default:
            break;
        }
    }

    void FACTOR(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            UNARY();
            FACTOR_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void TERM_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.MENOS)){
            coincidir(TipoToken.MENOS);
            FACTOR();
            TERM_2();
        } else if(preanalisis.tipo.equals(TipoToken.MAS)){
            coincidir(TipoToken.MAS);
            FACTOR();
            TERM_2();
        }
    }

    void UNARY(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.NO)){
            coincidir(TipoToken.NO);
            UNARY();
        } else if(preanalisis.tipo.equals(TipoToken.MENOS)){
            coincidir(TipoToken.MENOS);
            UNARY();
        } else if(primeroPrimary.contains(preanalisis.tipo)){
            CALL();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void FACTOR_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.DIV)){
            coincidir(TipoToken.DIV);
            UNARY();
            FACTOR_2();
        } else if(preanalisis.tipo.equals(TipoToken.MULT)){
            coincidir(TipoToken.MULT);
            UNARY();
            FACTOR_2();
        }
    }

    void CALL(){
        if(hayErrores) return;
        if(primeroPrimary.contains(preanalisis.tipo)){
            PRIMARY();
            CALL_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void PRIMARY(){
        if(hayErrores) return;
        switch(preanalisis.tipo){
            case TRUE:
                coincidir(TipoToken.TRUE);
            break;
            case FALSE:
                coincidir(TipoToken.FALSE);
            break;
            case NULL:
                coincidir(TipoToken.NULL);
            break;
            case THIS:
                coincidir(TipoToken.THIS);
            break;
            case NUM:
                coincidir(TipoToken.NUM);
            break;
            case CADENA:
                coincidir(TipoToken.CADENA);
            break;
            case ID:
                coincidir(TipoToken.ID);
            break;
            case PAR_AP:
                coincidir(TipoToken.PAR_AP);
                EXPRESSION();
                coincidir(TipoToken.PAR_CIE);
            break;
            case SUPER:
                coincidir(TipoToken.SUPER);
                coincidir(TipoToken.PUNTO);
                coincidir(TipoToken.ID);
            break;
            default:
                hayErrores = true;
                Interprete.error(
                    preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                    ". No se esperaba el token " + preanalisis.tipo
                );
            break;
        }
    }

    void CALL_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.PAR_AP)){
            coincidir(TipoToken.PAR_AP);
            ARGUMENTS_OPC();
            coincidir(TipoToken.PAR_CIE);
            CALL_2();
        } else if(preanalisis.tipo.equals(TipoToken.PUNTO)){
            coincidir(TipoToken.PUNTO);
            coincidir(TipoToken.ID);
            CALL_2();
        }
    }

    void ARGUMENTS_OPC(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            ARGUMENTS();
        }
    }

    void PARAMETERS_OPC(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ID)){
            PARAMETERS();
        }
    }

    void PARAMETERS(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.ID)){
            coincidir(TipoToken.ID);
            PARAMETERS_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void PARAMETERS_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.COMA)){
            coincidir(TipoToken.COMA);
            coincidir(TipoToken.ID);
            PARAMETERS_2();
        }
    }

    void ARGUMENTS(){
        if(hayErrores) return;
        if(primeroExpression.contains(preanalisis.tipo)){
            EXPRESSION();
            ARGUMENTS_2();
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". No se esperaba el token " + preanalisis.tipo
            );
        }
    }

    void ARGUMENTS_2(){
        if(hayErrores) return;
        if(preanalisis.tipo.equals(TipoToken.COMA)){
            coincidir(TipoToken.COMA);
            EXPRESSION();
            ARGUMENTS_2();
        }
    }

    void coincidir(TipoToken tt){
        if(hayErrores) return;

        if(preanalisis.tipo == tt){
            preanalisis = tokens.get(++i);
        } else{
            hayErrores = true;
            Interprete.error(
                preanalisis.linea, "Error en la posición " + preanalisis.posicion + 
                ". Se esperaba el token " + tt
            );
        }
    }
}