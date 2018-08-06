package com.crolopez.smartfridge;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class InventoryNodes {
    private boolean is_valid;
    private Cursor cursor = null;
    private Context context;

    InventoryNodes(Cursor cursor) {
        if (cursor.getCount() > 0) {
            is_valid = true;
        } else {
            is_valid = false;
            return;
        }
        this.cursor = cursor;
        context = MainActivity.get_application_context();
    }

    public boolean is_valid() {
        return is_valid;
    }

    public boolean next_node() {
        return cursor.moveToNext();
    }

    public String get_name() {
        return cursor.getString(cursor.getColumnIndex("NAME"));
    }

    public String get_elements() {
        return cursor.getString(cursor.getColumnIndex("ELEMENTS"));
    }

    public String get_timestamp() {
        return cursor.getString(cursor.getColumnIndex("TIMESTAMP"));
    }

    public TableRow get_simple_row() {
        TableRow row;

        // Get the columns values
        String[] colText = {
                this.get_name(),
                this.get_elements(),
                this.get_timestamp()
        };

        row = new TableRow(context);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
        );

        for (String text : colText) {
            TextView tv = new TextView(context);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(text);
            row.addView(tv);
        }

        return row;
    }

    public TableRow get_row() {
        TableRow row;
        TableLayout margin_element;
        TableLayout first_element;
        TableLayout second_element;

        // Create the row
        row = new TableRow(context);
        row.setWeightSum(100);

        //Init the margin
        margin_element = new TableLayout(context);
        margin_element.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableLayout.LayoutParams.WRAP_CONTENT,
                5));
        //margin_element.setBackgroundColor(0xF00FFF00);

        // Init the first element of the row
        first_element = new TableLayout(context);
        first_element.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableLayout.LayoutParams.WRAP_CONTENT,
                65));
        first_element.addView(create_layout_text(get_name(), 1));
        first_element.addView(create_layout_text(get_elements(), 0));
        //first_element.setBackgroundColor(0xFF00FF00);

        // Init the second element of the row
        second_element = new TableLayout(context);
        second_element.setLayoutParams(new TableLayout.LayoutParams(
                0,
                TableLayout.LayoutParams.WRAP_CONTENT,
                30));
        second_element.addView(create_layout_text("Thumbail",0));
        //second_element.setBackgroundColor(0xFFF0FF00);

        // Add the elements to the row
        row.addView(margin_element);
        row.addView(first_element);
        row.addView(second_element);
        //row.setBackgroundColor(0xFF0FFFF0);

        return row;
    }

    private TextView create_layout_text(String text, int color) {
        TextView tv;
        TableRow.LayoutParams lp;

        lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        tv = new TextView(context);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(16);
        tv.setPaddingRelative(5,5,5,5);
        tv.setText(text);
        if (color == 1) {
            tv.setTextColor(Color.parseColor(context.getResources().getString(0 + R.color.colorTextDark)));
            tv.setTypeface(null, Typeface.BOLD);
        }

        return tv;
    }
}
