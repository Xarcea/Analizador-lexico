package mx.ipn.interprete;

public class TreePrinter {
    
    public static void printTree(Nodo root) {
        printTree(root, "", true, 0);
    }
    
    private static void printTree(Nodo nodo, String prefix, boolean isTail, int index) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "[" + index + "] " + nodo.getValue());
        
        if (nodo.getHijos() != null) {
            for (int i = 0; i < nodo.getHijos().size() - 1; i++) {
                printTree(nodo.getHijos().get(i), prefix + (isTail ? "    " : "│   "), false, i);
            }
            
            if (nodo.getHijos().size() > 0) {
                printTree(nodo.getHijos().get(nodo.getHijos().size() - 1), prefix + (isTail ? "    " : "│   "), true, nodo.getHijos().size() - 1);
            }
        }
    }
}