package com.crolopez.smartfridge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ListNode> {
    private static String TAG = "LIST_ADAPTER";
    private Context context;
    private View myFragmentView;
    private LayoutInflater inflater;
    private String backup_file;
    private static String pending_file = null;
    private static String separator = "-!-";
    private List<ListNode> elements;

    public ListAdapter(Context c, List<ListNode> objects, View f_view, LayoutInflater inf) {
        super(c, 0, objects);
        elements = objects;
        context = c;
        myFragmentView = f_view;
        inflater = inf;
        backup_file = MainActivity.get_application_cache_dir() + "shopping_list.backup";
        if (pending_file == null) {
            pending_file = MainActivity.get_application_cache_dir() + "shopping_list.pending";
        }
    }

    public List<ListNode> get_all() {
        return elements;
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        TextView name;
        TextView elements;
        TextView place;
        ListNode current_node;
        ImageView thumbail;
        Bitmap image_bitmap;

        // Check if the currently view exists
        if (convert_view == null) {
            convert_view = inflater.inflate(
                    R.layout.activity_list_node,
                    parent,
                    false);
        }

        // Get references
        name = (TextView) convert_view.findViewById(R.id.id_node_name);
        elements = (TextView) convert_view.findViewById(R.id.id_node_elements_value);
        place = (TextView) convert_view.findViewById(R.id.id_node_place_value);
        thumbail = (ImageView) convert_view.findViewById(R.id.id_node_image);

        // Get current node
        current_node = getItem(position);

        // Set background color
        if (!current_node.is_marked()) {
            convert_view.setBackgroundColor(Color.parseColor(context.getResources().getString(0 + R.color.colorFragmentBackground)));
        } else {
            convert_view.setBackgroundColor(Color.parseColor(context.getResources().getString(0 + R.color.colorGray)));
        }

        // Setup.
        name.setText(current_node.get_name());
        elements.setText(String.valueOf(current_node.get_elements()));
        place.setText(current_node.get_place());
        if ((image_bitmap = current_node.check_image("front", true)) != null) {
            thumbail.setVisibility(View.VISIBLE);
            thumbail.setImageBitmap(image_bitmap);
        } else {
            thumbail.setVisibility(View.INVISIBLE);
        }

        return convert_view;
    }

    @Override
    public void add(ListNode object) {
        int position;
        ListNode node;
        String name = object.get_name();
        String market = object.get_place();
        int quantity = object.get_elements();

        // Remove excess space
        name = name.replaceAll("^\\s+|\\s+$", "");
        market = market.replaceAll("^\\s+|\\s+$", "");

        for (position = 0; position < getCount(); position++) {
            node = getItem(position);

            // Check if is the same node
            if (node.get_name().equalsIgnoreCase(name) && node.get_place().equalsIgnoreCase(market)) {
                node.set_elements(quantity + node.get_elements());
                notifyDataSetChanged();
                return;
            }
        }

        object.set_place(market);
        object.set_name(name);

        super.add(object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        save_state();
    }

    public void save_state() {
        PrintWriter file_output;
        int i;
        String buffer;
        ListNode node;
        String str_mark;

        try {
            file_output = new PrintWriter(backup_file);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Exception: save_state(): 1");
            e.printStackTrace();
            return;
        }

        for (i = 0; i < getCount(); i++) {
            node = getItem(i);
            str_mark = (node.is_marked()) ? "T" : "N";

            buffer = node.get_name() + separator
                    + node.get_elements() + separator
                    + node.get_place() + separator
                    + str_mark + separator
                    + node.get_code() + separator
                    + node.get_image_front() + separator;
            file_output.println(buffer);
        }

        file_output.close();
    }

    public static void save_pending_state(Product product, Context context) {
        PrintWriter file_output;
        String buffer;

        if (pending_file == null) {
            pending_file = MainActivity.get_application_cache_dir() + "shopping_list.pending";
        }

        try {
            file_output = new PrintWriter(new FileOutputStream(new File(pending_file), true));
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Exception: save_pending_state(): 1");
            e.printStackTrace();
            return;
        }

        buffer = product.get_name() + separator
                + "1" + separator
                + ListNode.get_default_place() + separator
                + "N" + separator
                + product.get_code() + separator
                + product.get_image_front() + separator;

        file_output.println(buffer);
        file_output.close();
    }

    public void load_states() {
        load_state(false);
        load_state(true);
    }

    private void load_state(boolean pending) {
        ArrayList <ListNode> nodes;
        String name;
        int quantity;
        String place;
        String buffer;
        String image_front;
        BufferedReader file_input;
        ListNode node;
        boolean marked;
        String code;
        String[] splitted;
        String file_path = (pending) ? pending_file : backup_file;
        File fp;

        if ((fp = new File(file_path)).exists()) {
            nodes = new ArrayList<>();
            try {
                file_input = new BufferedReader(new FileReader(file_path));
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Exception: load_state(): 1");
                e.printStackTrace();
                return;
            }

            while (true) {
                try {
                    buffer = file_input.readLine();
                } catch (IOException e) {
                    Log.d(TAG, "Exception: load_state(): 2");
                    e.printStackTrace();
                    return;
                }

                if (buffer == null) {
                    break;
                }

                Log.d(TAG, "Read '" + buffer + "' from saved state.");

                splitted = buffer.split(separator);
                name = splitted[0];
                quantity = Integer.parseInt(splitted[1]);
                place = splitted[2];
                marked = splitted[3].equals("T");
                code = (splitted[4].equals("null")) ? null : splitted[4];
                image_front = (splitted[5].equals("null")) ? null : splitted[5];

                node = new ListNode(name, quantity, place, code, image_front, marked);
                if (pending) {
                    add(node);
                } else {
                    nodes.add(node);
                }
            }

            if (pending) {
                fp.delete();
            } else {
                addAll(nodes);
            }
        }
    }

    public void modify_element(ListNode node, String name, int elements, String place) {
        node.set_name(name);
        node.set_elements(elements);
        node.set_place(place);
        notifyDataSetChanged();
    }
}

