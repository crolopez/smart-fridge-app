package com.crolopez.smartfridge;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ListNode> {
    private String TAG = "LIST_ADAPTER";
    private Context context;
    private View myFragmentView;
    private LayoutInflater inflater;
    private String backup_file;
    private String separator = "-!-";

    public ListAdapter(Context c, List<ListNode> objects, View f_view, LayoutInflater inf) {
        super(c, 0, objects);
        context = c;
        myFragmentView = f_view;
        inflater = inf;
        backup_file = context.getCacheDir() + "/shopping_list.backup";
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        TextView name;
        TextView quantity;
        TextView market;
        ListNode current_node;

        // Check if the currently view exists
        if (convert_view == null) {
            convert_view = inflater.inflate(
                    R.layout.activity_list_node,
                    parent,
                    false);
        }

        // Get references
        name = (TextView) convert_view.findViewById(R.id.id_node_name);
        quantity = (TextView) convert_view.findViewById(R.id.id_node_quantity_value);
        market = (TextView) convert_view.findViewById(R.id.id_node_market_value);

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
        quantity.setText(String.valueOf(current_node.get_quantity()));
        market.setText(current_node.get_place());

        return convert_view;
    }

    @Override
    public void add(ListNode object) {
        int position;
        ListNode node;
        String name = object.get_name();
        String market = object.get_place();
        int quantity = object.get_quantity();

        // Remove excess space
        name = name.replaceAll("^\\s+|\\s+$", "");
        market = market.replaceAll("^\\s+|\\s+$", "");

        for (position = 0; position < getCount(); position++) {
            node = getItem(position);

            // Check if is the same node
            if (node.get_name().equalsIgnoreCase(name) && node.get_place().equalsIgnoreCase(market)) {
                node.set_quantity(quantity + node.get_quantity());
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
        try {
            save_state();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Exception: notifyDataSetChanged(): 1");
            e.printStackTrace();
        }
    }

    public void save_state() throws FileNotFoundException {
        PrintWriter file_output;
        int i;
        String buffer;
        ListNode node;
        String str_mark;

        file_output = new PrintWriter(backup_file);

        for (i = 0; i < getCount(); i++) {
            node = getItem(i);
            str_mark = (node.is_marked()) ? "T" : "N";
            buffer = node.get_name() + separator
                    + node.get_quantity() + separator
                    + node.get_place() + separator
                    + str_mark + separator;
            file_output.println(buffer);
        }

        file_output.close();
    }

    public void load_state() {
        ArrayList <ListNode> nodes;
        String name;
        int quantity;
        String place;
        String buffer;
        BufferedReader file_input;
        boolean marked;
        String[] splitted;

        if (new File(backup_file).exists()) {
            nodes = new ArrayList<ListNode>();
            try {
                file_input = new BufferedReader(new FileReader(backup_file));
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

                Log.d(TAG, "Read '" + buffer + "' from the backup file.");

                splitted = buffer.split(separator);
                name = splitted[0];
                quantity = Integer.parseInt(splitted[1]);
                place = splitted[2];
                marked = (splitted[3].equals("T")) ? true : false;

                nodes.add(new ListNode(name, quantity, place, marked));
            }
            addAll(nodes);
        }
    }

    public void modify_element(ListNode node, String name, int quantity, String place) {
        node.set_name(name);
        node.set_quantity(quantity);
        node.set_place(place);
        notifyDataSetChanged();
    }
}

