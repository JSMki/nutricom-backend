package com.example.nutricom.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.Optional;

public class SinglyLinkedList<T> {
    private Node<T> head;
    private int size;

    public SinglyLinkedList() {
        head = null;
        size = 0;
    }

    public boolean isEmpty() { return head == null; }
    public int getSize() { return size; }

    // Método original: insertar al inicio
    public void insertFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.setNext(head);
        head = newNode;
        size++;
    }

    // Método original: insertar al final (Este usarás para crear usuarios nuevos)
    public void insertLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    // Método original: eliminar por objeto
    public boolean remove(T data) {
        if (isEmpty()) return false;

        if (head.getData().equals(data)) {
            head = head.getNext();
            size--;
            return true;
        }

        Node<T> previous = head;
        Node<T> current = head.getNext();

        while (current != null && !current.getData().equals(data)) {
            previous = current;
            current = current.getNext();
        }

        if (current == null) return false;

        previous.setNext(current.getNext());
        size--;
        return true;
    }

    // NUEVO MÉTODO PARA SPRING: Eliminar un registro comprobando una condición (ej. buscar por ID)
    public boolean removeIf(Predicate<T> condition) {
        if (isEmpty()) return false;

        if (condition.test(head.getData())) {
            head = head.getNext();
            size--;
            return true;
        }

        Node<T> previous = head;
        Node<T> current = head.getNext();

        while (current != null) {
            if (condition.test(current.getData())) {
                previous.setNext(current.getNext());
                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }

    // NUEVO MÉTODO PARA SPRING: Buscar un registro por condición
    public Optional<T> find(Predicate<T> condition) {
        Node<T> current = head;
        while (current != null) {
            if (condition.test(current.getData())) {
                return Optional.of(current.getData());
            }
            current = current.getNext();
        }
        return Optional.empty();
    }

    // OBLIGATORIO PARA RÚBRICA: Para que Jackson de Spring Boot convierta a JSON
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }

    public void clear() {
        head = null;
        size = 0;
    }
}