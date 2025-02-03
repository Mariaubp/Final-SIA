package sia;
import java.util.ArrayList;
import java.util.List;

// representa un subconjunto de productos
public class Individuo {
    List<Producto> productos;
    double pesoTotal;

    public Individuo(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
        this.pesoTotal = productos.stream().mapToDouble(p -> p.peso).sum();
    }

    //funcion aptitud
    public double calcularAptitud() {
        if (pesoTotal > 17) {
            return Double.MAX_VALUE;
        }
        return 17 - pesoTotal;
    }
}
