package com.crolopez.smartfridge;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class InventoryNodes {
    private boolean is_valid;
    private Cursor cursor = null;
    private Context context;

    InventoryNodes(Cursor cursor) {
        if(cursor.getCount() > 0) {
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

    public String get_quantity() {
        return cursor.getString(cursor.getColumnIndex("QUANTITY"));
    }

    public String get_timestamp() {
        return cursor.getString(cursor.getColumnIndex("TIMESTAMP"));
    }

    public TableRow get_simple_row() {
        TableRow row;

        // Get the columns values
        String[] colText = {
                this.get_name(),
                this.get_quantity(),
                this.get_timestamp()
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

        return row;
    }
}
