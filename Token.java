package mx.ipn.interprete;

/**
 *
 * @author xavier arce
 */

public class Token {
    final TipoToken tipo;
    final String lexema;
    final Object literal;
    final int posicion;
    final int linea;

    public Token(TipoToken tipo, String lexema, Object literal, 
                 int posicion, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
        this.posicion = posicion;
        this.linea = linea;
    }

    public Token(TipoToken tipo, String lexema, Object literal) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
        this.posicion = 0;
        this.linea = 0;
    }

    @Override
    public String toString(){
        return "<" + tipo + " " + lexema + " " + literal + ">";
    }

    // Métodos auxiliares
    public boolean esOperando(){
        switch (this.tipo){
            case ID:
            case NUM:
            case CADENA:
                return true;
            default:
                return false;
        }
    }

    public boolean esOperador(){
        switch (this.tipo){
            case MAS:
            case MENOS:
            case MULT:
            case DIV:
            case IGUAL:
            case GT:
            case GE:
            case LT:
            case LE:
            case DIFERENCIA:
            case AND:
            case OR:
            case ASIG:
                return true;
            default:
                return false;
        }
    }

    public boolean esPalabraReservada(){
        switch (this.tipo){
            case CLASS:
            case FALSE:
            case FOR:
            case FUN:
            case NULL:
            case VAR:
            case IF:
            case PRINT:
            case ELSE:
            case RETURN:
            case SUPER:
            case THIS:
            case TRUE:
            case WHILE:
                return true;
            default:
                return false;
        }
    }

    public boolean esEstructuraDeControl(){
        switch (this.tipo){
            case IF:
            case ELSE:
            case WHILE:
            case FOR:
                return true;
            default:
                return false;
        }
    }

    public boolean precedenciaMayorIgual(Token t){
        return this.obtenerPrecedencia() >= t.obtenerPrecedencia();
    }

    private int obtenerPrecedencia(){
        switch (this.tipo){
            case MULT:
            case DIV:
                return 7;
            case MAS:
            case MENOS:
                return 6;
            case GT:
            case GE:
            case LT:
            case LE:
                return 5;
            case IGUAL:
            case DIFERENCIA:
                return 4;
            case AND:
                return 3;
            case OR:
                return 2;
            case ASIG:
                return 1;
            default:
        }
        return 0;
    }

    public int aridad(){
        switch (this.tipo) {
            case MULT:
            case DIV:
            case MAS:
            case MENOS:
            case GT:
            case GE:
            case LT:
            case LE:
            case IGUAL:
            case DIFERENCIA:
            case AND:
            case OR:
            case ASIG:
                return 2;
            default:
        }
        return 0;
    }
}