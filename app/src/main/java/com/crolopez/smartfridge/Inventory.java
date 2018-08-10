package com.crolopez.smartfridge;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;

public class Inventory extends Fragment {
    private String TAG = "INVENTORY";
    private View myFragmentView = null;
    private Context context;
    private TableLayout table_layout = null;
    private String database_path = null;
    private String products_data_query = "SELECT * FROM PRODUCTS_DATA INNER JOIN IMAGES WHERE IMAGES.CODE = PRODUCTS_DATA.CODE;";
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Init values
        myFragmentView = inflater.inflate(com.crolopez.smartfridge.R.layout.activity_inventory, container, false);
        context = MainActivity.get_application_context();
        database_path = MainActivity.get_application_cache_dir() + "products.db";
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

        // Set content
        set_db_content();
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

    private void set_db_content() {
        SQLiteDatabase db = null;
        ContentValues values;
        TableRow row;
        DatabaseDisplayer inventory_n;

        if (!(new File(database_path).exists())) {
            return;
        }

        db = SQLiteDatabase.openDatabase(database_path, null, Context.MODE_PRIVATE);
        db.beginTransaction();

        try {
            inventory_n = new DatabaseDisplayer(db.rawQuery(products_data_query,null));
            if(inventory_n.is_valid()) {
                for (int position = 0; position < inventory_n.get_count(); position++) {
                    row = inventory_n.get_row(position);
                    if (position % 2 == 1) {
                        row.setBackgroundColor(Color.parseColor(context.getResources().getString(0 + R.color.colorAccent)));
                    }
                    table_layout.addView(row);
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }
}