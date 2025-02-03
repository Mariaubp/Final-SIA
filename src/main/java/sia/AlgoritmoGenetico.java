package sia;

import java.util.Arrays;
import java.util.List;

public class AlgoritmoGenetico {
    static final int TAM_POBLACION = 20; //nro de individuos en cada generación.
    static final int GENERACIONES = 50; //nro de iteraciones del algoritmo.
    static final double PROB_CRUCE = 0.8; //probabilidad de aplicar el operador de cruce
    static final double PROB_MUTACION = 0.1; //probabilidad de aplicar mutación en un individuo

    static final List<Producto> productos = Arrays.asList(
            new Producto("Notebook", 2.1),
            new Producto("Tablet", 0.6),
            new Producto("Parlante Bluetooth", 3.6),
            new Producto("Smart TV", 5),
            new Producto("Smartphone", 0.25),
            new Producto("Impresora laser", 10),
            new Producto("Ventilador 15\"", 6),
            new Producto("Cámara GoPro", 0.16),
            new Producto("Router wifi", 0.55),
            new Producto("Aro luz 18\"", 2)
    );
}
