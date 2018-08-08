package com.crolopez.smartfridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ListNode> {
    private Context context;
    private View myFragmentView = null;
    private LayoutInflater inflater;
    public ListAdapter(Context c, List<ListNode> objects, View f_view, LayoutInflater inf) {
        super(c, 0, objects);
        context = c;
        myFragmentView = f_view;
        inflater = inf;
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        TextView name;
        TextView quantity;
        TextView market;
        ImageView edit;
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
        edit = (ImageView) convert_view.findViewById(R.id.id_node_edit);

        // Get current node
        current_node = getItem(position);

        // Setup.
        edit.setImageResource(R.mipmap.list_icons_edit);
        name.setText(current_node.get_name());
        quantity.setText(String.valueOf(current_node.get_quantity()));
        market.setText(current_node.get_market());

        return convert_view;
    }
}

