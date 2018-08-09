package com.crolopez.smartfridge;

public class ListNode {
    private String name;
    private int quantity;
    private String place;
    boolean mark;

    ListNode(String n, int q, String p, boolean m) {
        name = n;
        quantity = q;
        place = p;
        mark = m;
    }

    public String get_name() { return name; }
    public int get_quantity() { return quantity; }
    public String get_place() { return place; }
    public void set_quantity(int q) { quantity = q; }
    public void set_name(String s) { name = s; }
    public void set_place(String s) { place = s; }
    public void change_mark() { mark = !mark; }
    public boolean is_marked() { return mark; }
}
