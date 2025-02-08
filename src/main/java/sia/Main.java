package sia;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(IntGrafica::new);
    }

    static List<Individuo> generarPoblacionInicial() {
        List<Individuo> poblacion = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < AlgoritmoGenetico.TAM_POBLACION; i++) {
            List<Producto> seleccion = new ArrayList<>();
            for (Producto p : AlgoritmoGenetico.productos) {
                if (rand.nextBoolean()) seleccion.add(p);
            }
            poblacion.add(new Individuo(seleccion));
        }
        return poblacion;
    }

    static List<Individuo> evolucionarPoblacion(List<Individuo> poblacion) {
        List<Individuo> nuevaPoblacion = new ArrayList<>();
        while (nuevaPoblacion.size() < AlgoritmoGenetico.TAM_POBLACION) {
            Individuo padre = seleccionTorneo(poblacion, 3);
            Individuo madre = seleccionRuleta(poblacion);

            Individuo hijo = (Math.random() < AlgoritmoGenetico.PROB_CRUCE) ?
                    crucePunto(padre, madre) : cruceUniforme(padre, madre);

            if (Math.random() < AlgoritmoGenetico.PROB_MUTACION) {
                if (Math.random() < 0.5) {
                    mutacionBit(hijo);
                } else {
                    mutacionSwap(hijo);
                }
            }
            nuevaPoblacion.add(hijo);
        }
        return nuevaPoblacion;
    }

    static Individuo seleccionTorneo(List<Individuo> poblacion, int tamTorneo) {
        Random random = new Random();
        Individuo ganador = poblacion.get(random.nextInt(poblacion.size()));
        for (int i = 1; i < tamTorneo; i++) {
            Individuo candidato = poblacion.get(random.nextInt(poblacion.size()));
            if (candidato.calcularAptitud() < ganador.calcularAptitud()) {
                ganador = candidato;
            }
        }
        return ganador;
    }

    static Individuo seleccionRuleta(List<Individuo> poblacion) {
        double sumaAptitud = poblacion.stream().mapToDouble(Individuo::calcularAptitud).sum();
        double valorSeleccion = Math.random() * sumaAptitud;
        double count = 0;
        for (Individuo i : poblacion) {
            count += i.calcularAptitud();
            if (count >= valorSeleccion) {
                return i;
            }
        }
        return poblacion.get(poblacion.size() - 1);
    }

    static Individuo crucePunto(Individuo padre, Individuo madre) {
        Random random = new Random();
        int punto = random.nextInt(Math.min(padre.productos.size(), madre.productos.size()));
        List<Producto> hijos = new ArrayList<>(padre.productos.subList(0, punto));
        hijos.addAll(madre.productos.subList(punto, madre.productos.size()));
        return new Individuo(hijos);
    }

    static Individuo cruceUniforme(Individuo padre, Individuo madre) {
        Random random = new Random();
        List<Producto> hijos = new ArrayList<>();
        int maxSize = Math.min(padre.productos.size(), madre.productos.size());

        for (int i = 0; i < maxSize; i++) {
            hijos.add(random.nextBoolean() ? padre.productos.get(i) : madre.productos.get(i));
        }
        return new Individuo(hijos);
    }

    static void mutacionBit(Individuo individuo) {
        Random rand = new Random();
        if (!individuo.productos.isEmpty()) {
            int idx = rand.nextInt(individuo.productos.size());
            individuo.productos.remove(idx);
        }
    }

    static void mutacionSwap(Individuo individuo) {
        Random rand = new Random();
        if (individuo.productos.size() > 1) {
            int idx1 = rand.nextInt(individuo.productos.size());
            int idx2 = rand.nextInt(individuo.productos.size());
            Collections.swap(individuo.productos, idx1, idx2);
        }
    }
}
