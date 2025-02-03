package sia;

import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import static sia.AlgoritmoGenetico.*;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        List<Individuo> poblacion = generarPoblacionInicial();
        List<Double> historialAptitud = new ArrayList<>(); // Guardamos las mejores aptitudes

        for (int gen = 0; gen < GENERACIONES; gen++) {
            List<Individuo> nuevaPoblacion = new ArrayList<>();
            while (nuevaPoblacion.size() < TAM_POBLACION) {
                int tamTorneo = 3;
                Individuo padre = seleccionTorneo(poblacion, tamTorneo);
                Individuo madre = seleccionRuleta(poblacion);

                if (Math.random() < PROB_CRUCE) {
                    Individuo hijo;
                    if (Math.random() < 0.5) {
                        hijo = crucePunto(padre, madre);
                    } else {
                        hijo = cruceUniforme(padre, madre);
                    }
                    nuevaPoblacion.add(hijo);
                } else {
                    nuevaPoblacion.add(madre);
                }

                if (Math.random() < PROB_MUTACION) {
                    if (Math.random() < 0.5) {
                        mutacionBit(nuevaPoblacion.get(nuevaPoblacion.size() - 1));
                    } else {
                        mutacionSwap(nuevaPoblacion.get(nuevaPoblacion.size() - 1));
                    }
                }
            }

            poblacion = nuevaPoblacion;
            double mejorAptitud = poblacion.stream()
                    .mapToDouble(Individuo::calcularAptitud)
                    .min()
                    .orElse(Double.MAX_VALUE);

            if (Double.isFinite(mejorAptitud)) {
                historialAptitud.add(mejorAptitud);
                System.out.println("Generación " + gen + " - Mejor aptitud: " + mejorAptitud);
            } else {
                System.out.println("Aptitud no válida en la generación " + gen);
            }

        }
        mostrarGrafico(historialAptitud);


    }

    static List<Individuo> generarPoblacionInicial() {
        List<Individuo> poblacion = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < TAM_POBLACION; i++) {
            List<Producto> seleccion = new ArrayList<>();
            for (Producto p : productos) {
                if (rand.nextBoolean()) seleccion.add(p);
            }
            poblacion.add(new Individuo(seleccion));
        }
        return poblacion;
    }

    /*
     * realizar varios torneos escogidos al azar de la poblacion
     * el ganador del torneo (mayor actitud) es seleccionado para el cruzamiento
     * */
    static Individuo seleccionTorneo(List<Individuo> poblacion, int tamTorneo) {
        Random random = new Random();
        Individuo ganador = null;

        for (int i = 0; i < tamTorneo; i++) {
            Individuo candidato = poblacion.get(random.nextInt(poblacion.size()));
            if (ganador == null || candidato.calcularAptitud() < ganador.calcularAptitud()) {
                ganador = candidato;
            }
        }
        return ganador;

    }

    /*
    * los individuos con mayor aptitud (fitness) tienen una mayor probabilidad de ser seleccionados,
    * similar a cómo en una ruleta las casillas más grandes tienen más probabilidad de ser seleccionadas.
    * */
    static Individuo seleccionRuleta(List<Individuo> poblacion) {
        double sumaAptitud = poblacion.stream().mapToDouble(Individuo::calcularAptitud).sum();
        double valorSeleccion = Math.random() * sumaAptitud;
        double count = 0;
        for(Individuo i : poblacion) {
            count += i.calcularAptitud();
            if (count >= valorSeleccion) {
                return i;
            }
        }
        return poblacion.get(poblacion.size() - 1);
    }

    /*
    * combinar la información genética de dos individuos (padres) y generar uno o más descendientes
    * se elige un punto de corte para dividir los cromosomas
     * */
    static Individuo crucePunto (Individuo padre, Individuo madre) {
        Random random = new Random();
        int maxPunto = Math.min(padre.productos.size(), madre.productos.size());

        if (maxPunto == 0) {
            return new Individuo(new ArrayList<>()); // Si no hay productos, devolver un individuo vacío
        }

        int punto = random.nextInt(maxPunto);
        List<Producto> hijos = new ArrayList<>(padre.productos.subList(0, punto));
        hijos.addAll(madre.productos.subList(punto, madre.productos.size()));
        return new Individuo(hijos);
    }

    /*
    * cada gen del descendiente se elige aleatoriamente de uno de los dos padres.
    * */
    static Individuo cruceUniforme (Individuo padre, Individuo madre) {
        Random random = new Random();
        List<Producto> hijos = new ArrayList<>();

        int maxSize = Math.min(padre.productos.size(), madre.productos.size());

        for (int i = 0; i < maxSize; i++) {
            if (random.nextBoolean()) {
                hijos.add(padre.productos.get(i)); // Tomar del padre
            } else {
                hijos.add(madre.productos.get(i)); // Tomar de la madre
            }
        }

        return new Individuo(hijos);
    }


    /*
    * introduce cambios aleatorios en los genes de un individuo para mantener la diversidad genética
    * en la población y evitar la convergencia prematura hacia soluciones subóptimas
    * */

    static void mutacionBit(Individuo individuo) {
        Random rand = new Random();
        if (!individuo.productos.isEmpty()) {
            int idx = rand.nextInt(individuo.productos.size());
            individuo.productos.remove(idx);
        }
    }
    /*
    * intercambia dos elementos en la secuencia de un individuo, introduciendo diversidad genética en la población.
    * */
    static void mutacionSwap (Individuo individuo){
        Random rand = new Random();
        if(individuo.productos.size() > 1){
            int idx1 = rand.nextInt(individuo.productos.size());
            int idx2 = rand.nextInt(individuo.productos.size());
            Collections.swap(individuo.productos, idx1, idx2);
        }
    }

    static void mostrarGrafico(List<Double> aptitudes) {
        if (aptitudes.isEmpty()) {
            System.out.println("No hay datos para graficar.");
            return;
        }

        XYSeries serie = new XYSeries("Mejor Aptitud por Generación");
        for (int i = 0; i < aptitudes.size(); i++) {
            serie.add(i, aptitudes.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Evolución de la Aptitud",
                "Generación",
                "Mejor Aptitud",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        JFrame frame = new JFrame("Gráfico de Aptitud");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }



}