package com.example.amm;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CircularLinkedList<String> list = new CircularLinkedList<>();
        list.insertWithOrder("d");
        list.insertWithOrder("b");
        list.insertWithOrder("a");
        list.insertWithOrder("c");
        // list.display(); // 输出：1 2 4 5
        List<String> s = new ArrayList<>();
        for (int i = 0; i < list.getSize(); i++) {
            if (i == list.getSize() - 1) {
                s.add(list.getNode(i) + list.getNode(0));
                System.out.println(list.getNode(i) + list.getNode(0));
            } else {
                s.add(list.getNode(i) + list.getNode(i + 1));
                System.out.println(list.getNode(i) + list.getNode(i + 1));
            }
        }
        // b
        for (String s1 : s) {
            if (s1.startsWith("b")) {
                System.out.println(s1);
            }
        }

    }

}
