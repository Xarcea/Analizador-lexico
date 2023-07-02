package mx.ipn.interprete;

public class Solver {

    private final Nodo nodo;

    public Solver(Nodo nodo) {
        this.nodo = nodo;
    }

    public Object resolver(TablaSimbolos tabla){
        return resolver(nodo, tabla);
    }
    
    private Object resolver(Nodo n, TablaSimbolos tabla){
        // No tiene hijos, es un operando
        if(n.getHijos() == null){
            if(n.getValue().tipo == TipoToken.NUM || n.getValue().tipo == TipoToken.CADENA){
                return n.getValue().literal;
            }
            else if(n.getValue().tipo == TipoToken.ID){
                return tabla.obtener(n.getValue());
            }
        }
        // Por simplicidad se asume que la lista de hijos del nodo tiene dos elementos
        Nodo izq = n.getHijos().get(0);
        Nodo der = n.getHijos().get(1);
        Object resultadoIzquierdo = resolver(izq,tabla);
        Object resultadoDerecho = resolver(der,tabla);

        if(resultadoIzquierdo instanceof Double && resultadoDerecho instanceof Double){
            switch (n.getValue().tipo){
                case MAS:
                    return ((Double)resultadoIzquierdo + (Double) resultadoDerecho);
                case MENOS:
                    return ((Double)resultadoIzquierdo - (Double) resultadoDerecho);
                case MULT:
                    return ((Double)resultadoIzquierdo * (Double) resultadoDerecho);
                case DIV:
                    return ((Double)resultadoIzquierdo / (Double) resultadoDerecho);
                case LT:
                    return ((Double)resultadoIzquierdo < (Double)resultadoDerecho);
                case LE:
                    return ((Double)resultadoIzquierdo <= (Double)resultadoDerecho);
                case GT:
                    return ((Double)resultadoIzquierdo > (Double)resultadoDerecho);
                case GE:
                    return ((Double)resultadoIzquierdo >= (Double)resultadoDerecho);
                case IGUAL:
                    return ((Double)resultadoIzquierdo == (Double)resultadoDerecho);
                case DIFERENCIA:
                    return ((Double)resultadoIzquierdo != (Double)resultadoDerecho);
                default:
                    break;
            }
        }
        else if(resultadoIzquierdo instanceof String && resultadoDerecho instanceof String){
            if (n.getValue().tipo == TipoToken.MAS){
                return ((String) resultadoIzquierdo + (String) resultadoDerecho);
            } else if(n.getValue().tipo == TipoToken.IGUAL){
                return (((String) resultadoIzquierdo).equals((String) resultadoDerecho));
            } else{
                Interprete.error(n.getValue().linea, "Error en la posición " + n.getValue().posicion + 
                ". El operador " + n.getValue().lexema + " no está definido para el tipo de argumentos.");
                System.exit(64);
            }
        }
        else if(resultadoIzquierdo instanceof Boolean && resultadoDerecho instanceof Boolean){
            if(n.getValue().tipo == TipoToken.OR){
                return ((boolean) resultadoIzquierdo || (boolean) resultadoDerecho);
            } else if (n.getValue().tipo == TipoToken.AND){
                return ((boolean) resultadoIzquierdo && (boolean) resultadoDerecho);
            } else{
                Interprete.error(n.getValue().linea, "Error en la posición " + n.getValue().posicion + 
                ". El operador " + n.getValue().lexema + " no está definido para el tipo de argumentos.");
                System.exit(64);
            }
        } else{
            // Error por diferencia de tipos
            Interprete.error(n.getValue().linea, "Error en la posición " + n.getValue().posicion + 
                ". Los operandos son de difernete tipo.");
                System.exit(64);
        }
        return null;
    }
}