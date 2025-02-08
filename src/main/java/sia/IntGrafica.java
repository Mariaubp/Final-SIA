package sia;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class IntGrafica extends JFrame {
    private JTextField[] camposProductos;
    private JButton calcularButton;
    private JButton agregarProductoButton;
    private JTextArea resultadoArea;
    private JPanel panel;

    public IntGrafica() {
        setTitle("Optimización de Viajes de Drones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        add(panel, BorderLayout.CENTER);

        calcularButton = new JButton("Calcular");
        agregarProductoButton = new JButton("Agregar Producto");
        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.SOUTH);

        actualizarCamposProductos();

        panel.add(calcularButton);
        panel.add(agregarProductoButton);

        calcularButton.addActionListener(e -> ejecutarAlgoritmo());
        agregarProductoButton.addActionListener(e -> agregarProducto());

        setVisible(true);
    }

    private void actualizarCamposProductos() {
        panel.removeAll();

        if (AlgoritmoGenetico.productos.isEmpty()) {
            panel.revalidate();
            panel.repaint();
            return;
        }

        camposProductos = new JTextField[AlgoritmoGenetico.productos.size()];
        for (int i = 0; i < AlgoritmoGenetico.productos.size(); i++) {
            panel.add(new JLabel(AlgoritmoGenetico.productos.get(i).nombre));
            camposProductos[i] = new JTextField();
            panel.add(camposProductos[i]);
        }

        panel.add(calcularButton);
        panel.add(agregarProductoButton);

        panel.revalidate();
        panel.repaint();
    }

    private void ejecutarAlgoritmo() {
        List<Individuo> poblacion = Main.generarPoblacionInicial();
        List<Double> historialAptitud = new ArrayList<>();

        for (int gen = 0; gen < AlgoritmoGenetico.GENERACIONES; gen++) {
            poblacion = Main.evolucionarPoblacion(poblacion);
            double mejorAptitud = poblacion.stream().mapToDouble(Individuo::calcularAptitud).min().orElse(Double.MAX_VALUE);
            historialAptitud.add(mejorAptitud);
        }

        resultadoArea.setText("Mejor aptitud: " + historialAptitud.get(historialAptitud.size() - 1));
        mostrarGrafico(historialAptitud);
    }

    private void agregarProducto() {
        String nombre = JOptionPane.showInputDialog("Nombre del producto:");
        if (nombre != null && !nombre.isEmpty()) {
            String pesoStr = JOptionPane.showInputDialog("Peso del producto (en kg):");
            try {
                double peso = Double.parseDouble(pesoStr);
                AlgoritmoGenetico.productos.add(new Producto(nombre, peso));
                actualizarCamposProductos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Peso inválido. Ingrese un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarGrafico(List<Double> aptitudes) {
        XYSeries serie = new XYSeries("Mejor Aptitud por Generación");
        for (int i = 0; i < aptitudes.size(); i++) {
            serie.add(i, aptitudes.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Evolución de la Aptitud", "Generación", "Mejor Aptitud", dataset);

        JFrame frame = new JFrame("Gráfico de Aptitud");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}

