package com.crolopez.smartfridge;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Inventory extends Fragment {
    private String TAG = "INVENTORY";
    private View myFragmentView = null;
    private Context context;
    private TableLayout table_layout = null;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Init values
        myFragmentView = inflater.inflate(com.crolopez.smartfridge.R.layout.activity_inventory, container, false);
        context = MainActivity.get_application_context();
        this.inflater = inflater;

        // Print table
        set_table();

        return myFragmentView;
    }

    private void set_table() {
        table_layout= (TableLayout) myFragmentView.findViewById(R.id.id_database_layout);
        table_layout.setStretchAllColumns(true);

        // Set header
        set_header();

        // Set products table
        set_product_table();
    }

    private void set_header() {
        TableRow row_header;
        TextView text;

        row_header = new TableRow(context);
        row_header.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
        );

        // Add buttons
        // This text is filler until the buttons are added
        text = new TextView(context);
        text.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        text.setGravity(Gravity.CENTER);
        text.setTextSize(18);
        text.setPadding(5, 5, 5, 5);
        text.setText("BUTTONS");
        text.setTextColor(Color.parseColor(context.getResources().getString(0 + R.color.colorFragmentBackground)));
        row_header.addView(text);

        table_layout.addView(row_header);
    }

    private void set_product_table() {
        TableRow row;
        DatabaseDisplayer displayer;

        displayer = new DatabaseDisplayer(inflater);
        if(displayer.is_valid()) {
            for (int position = 0; position < displayer.get_count(); position++) {
                row = displayer.get_row(position);
                if (position % 2 == 1) {
                    row.setBackgroundColor(Color.parseColor(context.getResources().getString(0 + R.color.colorAccent)));
                }
                table_layout.addView(row);
            }
        }
    }
}