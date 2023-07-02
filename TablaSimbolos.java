package mx.ipn.interprete;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {
    private final Map<String, Object> values = new HashMap<>();

    boolean existeIdentificador(String identificador){
        return values.containsKey(identificador);
    }

    Object obtener(Token t) {
        if (!values.containsKey(t.lexema)) {
            //throw new RuntimeException(
            Interprete.error(t.linea, "Error en la posición " + t.posicion + 
            ". Variable no definida '" + t.lexema + "'.");
            //);
            System.exit(64);
        }
        return values.get(t.lexema);
    }

    void asignar(String identificador, Object valor){
        values.put(identificador, valor);
    }
}