package com.crolopez.smartfridge;

public class ListNode {
    private String name;
    private int quantity;
    private String market = null;

    ListNode(String n, int q, String m) {
        name = n;
        quantity = q;
        market = m;
    }

    public String get_name() { return name; }
    public int get_quantity() { return quantity; }
    public String get_market() { return market; }
}
