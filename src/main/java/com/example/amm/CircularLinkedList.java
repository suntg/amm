package com.example.amm;

public class CircularLinkedList<T extends Comparable<T>> {

    // 头结点
    private Node<T> head;
    // 链表长度
    private int size;

    public CircularLinkedList() {
        this.head = null;
        this.size = 0;
    }

    // 插入新节点到末尾
    public void insert(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            head.next = head; // 头结点指向自己形成循环链表
        } else {
            Node<T> curNode = head;
            while (curNode.next != head) {
                curNode = curNode.next;
            }
            curNode.next = newNode;
            newNode.next = head;
        }
        size++;
    }

    // 删除指定元素
    public boolean delete(T data) {
        if (head == null) {
            return false;
        }

        // 找到要删除的节点和其前驱节点
        Node<T> prevNode = head;
        Node<T> curNode = head;
        do {
            if (curNode.data.equals(data)) {
                break;
            }
            prevNode = curNode;
            curNode = curNode.next;
        } while (curNode != head);

        if (curNode == head && curNode.data.equals(data)) { // 要删除的是头结点
            prevNode = prevNode.next;
            while (prevNode.next != head) {
                prevNode = prevNode.next;
            }
            prevNode.next = head.next;
            head = head.next;
        } else if (curNode.data.equals(data)) { // 要删除的不是头结点
            prevNode.next = curNode.next;
        } else { // 没找到要删除的元素
            return false;
        }

        size--;
        return true;
    }

    // 获取链表长度
    public int getSize() {
        return size;
    }

    // 获取指定位置的节点
    public T getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }

        Node<T> curNode = head;
        for (int i = 0; i < index; i++) {
            curNode = curNode.next;
        }

        return curNode.data;
    }

    // 输出所有元素
    public void display() {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }

        Node<T> curNode = head;
        do {
            System.out.print(curNode.data + " ");
            curNode = curNode.next;
        } while (curNode != head);
        System.out.println();
    }

    // 插入新节点并按照自定义排序规则排序
    public void insertWithOrder(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            head.next = head; // 头结点指向自己形成循环链表
        } else {
            Node<T> curNode = head;
            Node<T> prevNode = null;
            do {
                if (newNode.data.compareTo(curNode.data) < 0) { // 找到了插入位置
                    break;
                }
                prevNode = curNode;
                curNode = curNode.next;
            } while (curNode != head);

            if (prevNode == null) { // 插入头结点前面
                prevNode = head;
                while (prevNode.next != head) {
                    prevNode = prevNode.next;
                }
                prevNode.next = newNode;
                newNode.next = head;
                head = newNode;
            } else { // 插入中间或末尾
                prevNode.next = newNode;
                newNode.next = curNode;
            }
        }
        size++;
    }

    // 定义链表节点类
    private class Node<T> {
        T data;
        Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }

    }

}
