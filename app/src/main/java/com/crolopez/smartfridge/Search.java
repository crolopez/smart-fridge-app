package com.crolopez.smartfridge;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.sql.SQLInput;

public class Search extends Fragment {
    private String TAG = "SEARCH";
    private View myFragmentView = null;
    private String[] headerText={"Product","Quantity","Date added"};
    private Context context;
    private TableLayout table_layout = null;
    private String database_path = null;
    private String select_query = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Init values
        myFragmentView = inflater.inflate(com.crolopez.smartfridge.R.layout.activity_search, container, false);
        context = MainActivity.get_application_context();
        database_path = context.getCacheDir().getAbsolutePath() + "/products.db";
        if (select_query == null) {
            select_query = "SELECT NAME, QUANTITY, TIMESTAMP FROM PRODUCTS_DATA;";
        }

        // Print table
        set_table();

        return myFragmentView;
    }

    private void set_table() {
        table_layout= (TableLayout) myFragmentView.findViewById(R.id.id_database_layout);

        // Set header
        set_header();

        // Set content
        set_db_content();
    }

    private void set_header() {
        TableRow row_header;
        TextView text;

        row_header = new TableRow(context);
        row_header.setBackgroundColor(Color.parseColor(getResources().getString(0 + R.color.colorPrimary)));
        row_header.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
        );
        for(String c:headerText) {
            text = new TextView(context);
            text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            text.setGravity(Gravity.CENTER);
            text.setTextSize(18);
            text.setPadding(5, 5, 5, 5);
            text.setText(c);
            row_header.addView(text);
        }
        table_layout.addView(row_header);
    }

    private void set_db_content() {
        SQLiteDatabase db = null;
        ContentValues values;
        TableRow row;

        db = SQLiteDatabase.openDatabase(database_path, null, Context.MODE_PRIVATE);
        db.beginTransaction();

        try {
            Cursor cursor = db.rawQuery(select_query,null);

            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Get the columns values
                    String[] colText = {
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    };

                    row = new TableRow(context);
                    row.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT)
                    );

                    for(String text:colText) {
                        TextView tv = new TextView(context);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(16);
                        tv.setPadding(5, 5, 5, 5);
                        tv.setText(text);
                        row.addView(tv);
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