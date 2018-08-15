package com.crolopez.smartfridge;

public class ListNode extends Product{
    private boolean mark;
    private String place;
    private static String default_place = "Anywhere";

    ListNode(String name, int elements, String place, String code, String image_front, boolean mark) {
        super(code, name, "0g", elements, null,
                null, null, null, image_front,
                null, null);
        this.mark = mark;
        this.place = (place == null || place.equals("")) ? default_place : place;
    }

    public void change_mark() { mark = !mark; }
    public boolean is_marked() { return mark; }
    public String get_place() { return place; }
    public static String get_default_place() { return default_place; }
    public void set_place(String s) { place = s;}
}
