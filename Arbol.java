package mx.ipn.interprete;

public class Arbol {
    private final Nodo raiz;
    private TablaSimbolos tabla;

    public Arbol(Nodo raiz){
        this.raiz = raiz;
    }

    public void recorrer(TablaSimbolos tabla){
        this.tabla = tabla;
        for(Nodo n : raiz.getHijos()){
            recorrer(n);
        }
    }

    private void recorrer(Nodo n){
        Token t = n.getValue();
        switch (t.tipo){
            // Operadores aritméticos
            case MAS:
            case MENOS:
            case MULT:
            case DIV:
                realizarOperacion(n);
            break;
            case ASIG:
                Nodo temp = n.getHijos().get(0);
                tabla.obtener(temp.getValue());
                Object valor = realizarOperacion(n.getHijos().get(1));
                tabla.asignar(temp.getValue().lexema, valor);
            break;
            case VAR:
                crearVar(n);
            break;
            case PRINT:
                Object cadena = obtenerCadena(n.getHijos().get(0));
                System.out.println(cadena);
            break;
            case IF:
                Object condIF = realizarOperacion(n.getHijos().get(0));
                int size = n.getHijos().size();
                Nodo posibleELSE = n.getHijos().get(size-1);
                if(condIF instanceof Boolean){
                    if((boolean) condIF){
                        if(n.getHijos() != null)
                            for(int i=1; i<n.getHijos().size(); i++){
                                Nodo m = n.getHijos().get(i);
                                if(m.getValue().tipo == TipoToken.ELSE)
                                    break;
                                recorrer(m);
                            }
                    } else if(posibleELSE.getValue().tipo == TipoToken.ELSE){
                        if(posibleELSE.getHijos() != null)
                            for(Nodo m : posibleELSE.getHijos()){
                                    recorrer(m);
                            }
                    }
                } else{
                    Interprete.error(n.getValue().linea, "Error en la posición " + n.getValue().posicion + 
                    ". La condición no es un valor booleano.");
                    System.exit(64);
                }
            break;
            case FOR:
                recorrer(n.getHijos().get(0));
                Object condFOR = realizarOperacion(n.getHijos().get(1));
                if(condFOR instanceof Boolean){
                    while((boolean) condFOR){
                        if(n.getHijos() != null)
                            for(int i=3; i<n.getHijos().size(); i++){
                                Nodo m = n.getHijos().get(i);
                                recorrer(m);
                            }
                        recorrer(n.getHijos().get(2));
                        condFOR = realizarOperacion(n.getHijos().get(1));
                    }
                } else{
                    Interprete.error(n.getValue().linea, "Error en la posición " + n.getValue().posicion + 
                    ". La condición no es un valor booleano.");
                    System.exit(64);
                }
            break;
            case WHILE:
                Object condWHILE = realizarOperacion(n.getHijos().get(0));
                if(condWHILE instanceof Boolean){
                    while((boolean) condWHILE){
                        if(n.getHijos() != null)
                            for(int i=1; i<n.getHijos().size(); i++){
                                Nodo m = n.getHijos().get(i);
                                recorrer(m);
                            }
                        condWHILE = realizarOperacion(n.getHijos().get(0));
                    }
                } else{
                    Interprete.error(n.getValue().linea, "Error en la posición " + n.getValue().posicion + 
                    ". La condición no es un valor booleano.");
                    System.exit(64);
                }
            break;
            default:
            break;
        }
    }

    private Object obtenerCadena(Nodo n) {
        if(n.getValue().tipo==TipoToken.CADENA){
            return n.getValue().literal;
        } else if(n.getValue().tipo==TipoToken.ID){
            return tabla.obtener(n.getValue());
        } else {
            Solver solver = new Solver(n);
            return solver.resolver(tabla);
        }
    }

    private void crearVar(Nodo n){
        Nodo izq = n.getHijos().get(0);
        if(tabla.existeIdentificador(izq.getValue().lexema)){
            Interprete.error(izq.getValue().linea, "Error en la posición " + izq.getValue().posicion + 
            ". Variable duplicada.");
            System.exit(64);
        }
        Object valor;
        if(n.getHijos().size() == 1){
            valor = null;
        } else {
            Nodo der = n.getHijos().get(1);
            if(der.getValue().esOperador()){
                valor = realizarOperacion(der);
            } else{
                valor = der.getValue().literal;
            }
        }
        tabla.asignar(izq.getValue().lexema, valor);
    }

    private Object realizarOperacion(Nodo n) {
        Solver solver = new Solver(n);
        return solver.resolver(tabla);
    }
}