package com.example.nutricom.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListaLigada<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    public ListaLigada() {
        this.cabeza = null;
        this.cola = null;
        this.tamanio = 0;
    }

    // Agrega un dato al final
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
        }
        cola = nuevo;
        tamanio++;
    }

    // Busca el primer dato que cumpla la condición (o null si no hay)
    public T buscar(Predicate<T> condicion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    // Elimina el primer dato que cumpla la condición
    public boolean eliminar(Predicate<T> condicion) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;

        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    // Convierte la lista en una List de Java para que Spring la pueda mandar como JSON
    public List<T> toList() {
        List<T> resultado = new ArrayList<>();
        Nodo<T> actual = cabeza;
        while (actual != null) {
            resultado.add(actual.getDato());
            actual = actual.getSiguiente();
        }
        return resultado;
    }
}