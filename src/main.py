import tkinter as tk
from tkinter import messagebox
import random

# Lista de productos y sus pesos
productos = [
    {"nombre": "Notebook", "peso": 2.1},
    {"nombre": "Tablet", "peso": 0.6},
    {"nombre": "Parlante Bluetooth", "peso": 3.6},
    {"nombre": "Smart TV", "peso": 5},
    {"nombre": "Smartphone", "peso": 0.25},
    {"nombre": "Impresora laser", "peso": 10},
    {"nombre": "Ventilador 15’’", "peso": 6},
    {"nombre": "Cámara GoPro", "peso": 0.16},
    {"nombre": "Router wifi", "peso": 0.55},
    {"nombre": "Aro luz 18’’", "peso": 2}
]


# Función de aptitud
def fitness(cromosoma):
    peso_total = sum(productos[i]["peso"] * cromosoma[i] for i in range(len(cromosoma)))
    return peso_total if peso_total <= 17 else 0


# Selección por ruleta
def seleccion_ruleta(poblacion, aptitudes):
    total_aptitud = sum(aptitudes)
    probabilidades = [aptitud / total_aptitud for aptitud in aptitudes]
    seleccionado = random.choices(poblacion, weights=probabilidades, k=1)[0]
    return seleccionado


# Cruzamiento de un punto
def cruzamiento_un_punto(padre1, padre2):
    punto = random.randint(1, len(padre1) - 1)
    hijo1 = padre1[:punto] + padre2[punto:]
    hijo2 = padre2[:punto] + padre1[punto:]
    return hijo1, hijo2


# Mutación bit-flip
def mutacion_bit_flip(cromosoma, probabilidad_mutacion=0.1):
    for i in range(len(cromosoma)):
        if random.random() < probabilidad_mutacion:
            cromosoma[i] = 1 - cromosoma[i]
    return cromosoma


# Algoritmo Genético básico
def algoritmo_genetico(cantidades, tamano_poblacion=10, generaciones=100):
    poblacion = [[random.randint(0, cantidades[i]) for i in range(len(productos))] for _ in range(tamano_poblacion)]

    for generacion in range(generaciones):
        aptitudes = [fitness(cromosoma) for cromosoma in poblacion]
        nueva_poblacion = []

        while len(nueva_poblacion) < tamano_poblacion:
            padre1 = seleccion_ruleta(poblacion, aptitudes)
            padre2 = seleccion_ruleta(poblacion, aptitudes)
            hijo1, hijo2 = cruzamiento_un_punto(padre1, padre2)
            hijo1 = mutacion_bit_flip(hijo1)
            hijo2 = mutacion_bit_flip(hijo2)
            nueva_poblacion.extend([hijo1, hijo2])

        poblacion = nueva_poblacion[:tamano_poblacion]

    mejor_cromosoma = max(poblacion, key=fitness)
    return mejor_cromosoma


# Función para manejar el botón "Calcular"
def calcular_viajes():
    try:
        cantidades = [int(entry.get()) for entry in entries]
        mejor_solucion = algoritmo_genetico(cantidades)
        resultado = "Mejor solución:\n"
        for i in range(len(mejor_solucion)):
            if mejor_solucion[i] > 0:
                resultado += f"{productos[i]['nombre']}: {mejor_solucion[i]} unidades\n"
        resultado += f"Peso total: {fitness(mejor_solucion)} kg"
        messagebox.showinfo("Resultado", resultado)
    except ValueError:
        messagebox.showerror("Error", "Por favor, ingrese valores válidos.")


# Crear la ventana principal
root = tk.Tk()
root.title("Optimización de Viajes de Drones")

# Crear etiquetas y campos de entrada para cada producto
entries = []
for i, producto in enumerate(productos):
    label = tk.Label(root, text=f"{producto['nombre']} (Peso: {producto['peso']} kg):")
    label.grid(row=i, column=0, padx=10, pady=5)
    entry = tk.Entry(root)
    entry.grid(row=i, column=1, padx=10, pady=5)
    entries.append(entry)

# Botón para calcular los viajes
calcular_button = tk.Button(root, text="Calcular Viajes", command=calcular_viajes)
calcular_button.grid(row=len(productos), column=0, columnspan=2, pady=10)

# Iniciar la interfaz gráfica
root.mainloop()