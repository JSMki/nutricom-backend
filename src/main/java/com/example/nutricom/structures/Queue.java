package com.example.nutricom.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Queue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public Queue() {
        front = null;
        rear = null;
        size = 0;
    }

    public boolean isEmpty() { return front == null; }
    public int getSize() { return size; }

    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            front = newNode;
        } else {
            rear.setNext(newNode);
        }
        rear = newNode;
        size++;
    }

    // Atender: sale el primero de la fila
    public Optional<T> dequeue() {
        if (isEmpty()) {
            return Optional.empty();
        }
        T data = front.getData();
        front = front.getNext();
        if (front == null) {
            rear = null; // la fila quedó vacía
        }
        size--;
        return Optional.of(data);
    }

    public Optional<T> peek() {
        return isEmpty() ? Optional.empty() : Optional.of(front.getData());
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = front;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }

    public boolean removeIf(Predicate<T> condition) {
        if (isEmpty()) return false;

        if (condition.test(front.getData())) {
            front = front.getNext();
            if (front == null) {
                rear = null;
            }
            size--;
            return true;
        }

        Node<T> previous = front;
        Node<T> current = front.getNext();

        while (current != null) {
            if (condition.test(current.getData())) {
                previous.setNext(current.getNext());
                if (current == rear) {
                    rear = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }
}
