package mx.ipn.interprete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author xavier arce
 */
public class Interprete {

    //static boolean existenErrores = false;
    private static int linea = 1;

    public static void main(String[] args) throws IOException {
        if(args.length > 1) {
            System.out.println("Uso correcto: interprete [script]");
            // Convención defininida en el archivo "system.h" de UNIX
            System.exit(64);
        } else if(args.length == 1){
            ejecutarArchivo(args[0]);
        } else{
            ejecutarPrompt();
        }
    }

    private static void ejecutarArchivo(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));

        // Se indica que existe un error
        //if(existenErrores) System.exit(65);
    }

    private static void ejecutarPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true){
            System.out.print(">>> ");
            String inline = reader.readLine();
            if(inline == null) break; // Presionar Ctrl + C
            ejecutar(inline);
            linea++;
            //existenErrores = false;
        }
    }

    private static void ejecutar(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for(Token token : tokens){
            System.out.println(token);
        }
    }

    static void error(String mensaje){
        reportar(linea, mensaje);
    }

    static void error(int linea, String mensaje){
        reportar(linea, mensaje);
    }

    private static void reportar(int linea, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error: " + mensaje
        );
        //existenErrores = true;
    }
}