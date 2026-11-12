package com.example.nutricom.structures;

package com.example.nutricom.structures;

import java.util.ArrayList;
import java.util.List;

public class LinkedList<T> {
    private Node<T> head;

    // Método para insertar al final
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    // TRUCO PARA SPRING BOOT: Convierte tu lista ligada a una lista normal de Java
    // solo al momento de responder, para que Jackson pueda convertirlo a JSON.
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}