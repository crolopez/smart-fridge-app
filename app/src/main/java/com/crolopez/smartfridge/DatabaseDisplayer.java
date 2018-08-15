package com.crolopez.smartfridge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;

public class DatabaseDisplayer {
    private String TAG = "INV_NODES";
    private boolean is_valid;
    private Cursor cursor = null;
    private Context context;
    private int text_size_dp = 15;
    private ArrayList<Product> products;
    private ArrayList<Pair<String,String>> additives;
    private ArrayList<Pair<String,String>> allergens;
    private ArrayList<Pair<String,String>> ingredients;
    private int count = 0;
    private LayoutInflater inflater;

    DatabaseDisplayer(Cursor cursor, LayoutInflater inflater) {
        if (cursor.getCount() > 0) {
            is_valid = true;
        } else {
            is_valid = false;
            return;
        }
        this.cursor = cursor;
        context = MainActivity.get_application_context();
        this.inflater = inflater;
        set_products_array();
    }

    private void set_products_array() {
        int i;
        products = new ArrayList<>();

        for(i = 0; cursor.moveToNext(); i++) {
            products.add(new Product(get_sql_code(),
                                    get_sql_name(),
                                    get_sql_quantity(),
                                    get_sql_elements(),
                                    get_sql_timestamp(),
                                    get_sql_brands(),
                                    get_sql_labels(),
                                    get_sql_expiration_date(),
                                    get_sql_front_image(),
                                    get_sql_nutrition_image(),
                                    get_sql_ingredients_image())
                        );
        }
        count = i;
    }

    public int get_count() { return count; }
    public boolean is_valid() { return is_valid; }
    public String get_sql_code() { return cursor.getString(cursor.getColumnIndex("CODE")); }
    public String get_sql_name() { return cursor.getString(cursor.getColumnIndex("NAME")); }
    public int get_sql_elements() { return cursor.getInt(cursor.getColumnIndex("ELEMENTS")); }
    public String get_sql_quantity() { return cursor.getString(cursor.getColumnIndex("QUANTITY")); }
    public String get_sql_timestamp() { return cursor.getString(cursor.getColumnIndex("TIMESTAMP")); }
    public String get_sql_brands() { return cursor.getString(cursor.getColumnIndex("BRANDS")); }
    public String get_sql_labels() { return cursor.getString(cursor.getColumnIndex("LABELS")); }
    public String get_sql_expiration_date() { return cursor.getString(cursor.getColumnIndex("EXPIRATION_DATE")); }
    public String get_sql_front_image() { return cursor.getString(cursor.getColumnIndex("FRONT")); }
    public String get_sql_nutrition_image() { return cursor.getString(cursor.getColumnIndex("NUTRITION")); }
    public String get_sql_ingredients_image() { return cursor.getString(cursor.getColumnIndex("INGREDIENTS")); }

    public TableRow get_row(int position) {
        TableRow row;
        TableLayout left_margin_element;
        TableLayout right_margin_element;
        TableLayout first_element;
        ImageView image_element;
        TableRow.LayoutParams layout_params;
        Product pr = products.get(position);

        // Create the row
        row = new TableRow(context);
        row.setWeightSum(100);
        set_listeners(row);
        row.setId(position);

        //Init the margins
        left_margin_element = new TableLayout(context);
        right_margin_element = new TableLayout(context);
        left_margin_element.setLayoutParams(new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,5));
        right_margin_element.setLayoutParams(new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,5));
        left_margin_element.setBackgroundColor(0xF0FFFF00);
        right_margin_element.setBackgroundColor(0xF0FFFF00);

        // Init the first element of the row
        first_element = new TableLayout(context);
        layout_params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,70);
        layout_params.gravity = Gravity.CENTER;
        first_element.setLayoutParams(layout_params);
        first_element.addView(create_layout_text(pr.get_name(), null, true, false));
        first_element.addView(create_layout_text(String.valueOf(pr.get_elements()), "Elements: ", false, true));

        image_element = pr.get_imageview();
        if (image_element != null) {
            layout_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout_params.gravity = Gravity.RIGHT;
            layout_params.weight = 20;
            image_element.setLayoutParams(layout_params);
            image_element.setVisibility(View.VISIBLE);
        } else {
            // Create empty imageview ~~
        }

        // Add the elements to the row
        row.addView(left_margin_element);
        row.addView(first_element);
        if (image_element != null) {
            row.addView(image_element);
        }
        row.addView(right_margin_element);

        return row;
    }

    private int dps_to_pixel(int dps) {
        final float conversion_scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * conversion_scale + 0.5f);
    }

    private TextView create_layout_text(String text, String d_tag, boolean header, boolean last) {
        TextView tv;
        TableRow.LayoutParams lp;

        lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        tv = new TextView(context);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, text_size_dp);
        if (d_tag == null) {
            if (header) {
                tv.setTextColor(Color.parseColor(context.getResources().getString(0 + R.color.colorTextDark)));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, text_size_dp + 1);
            }
            tv.setText(text);
        } else {
            Spanned hml_text = Html.fromHtml("<b>" + d_tag + "</b>" + text);
            tv.setText(hml_text);
        }

        tv.setPaddingRelative(5, (header) ? 50 : 5,5, (last) ? 50 : 5);
        return tv;
    }

    private void set_listeners(TableRow row) {
        row.setLongClickable(true);
        row.setClickable(true);

        // Press click
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Product pr;

                pr = products.get(v.getId());
                Log.d(TAG, "Press on '" + pr.get_name() + "'.");
                ListAdapter.save_pending_state(pr, context);

                ToastMsg.show_toast_msg(context,pr.get_name() + " added to the shopping list");

                return true;
            }
        });

        // normal click
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product pr;

                pr = products.get(v.getId());
                Log.d(TAG, "Click on '" + pr.get_name() + "'.");
                show_details(pr);
            }
        });
    }

    private void show_details(Product product) {
        View prompts_view;
        AlertDialog.Builder dialog_builder;
        AlertDialog alert_dialog;
        TextView text;

        prompts_view = inflater.inflate(R.layout.activity_inventory_advanced, null);
        dialog_builder = new AlertDialog.Builder(MainActivity.getActivity());
        dialog_builder.setView(prompts_view);

        ((TextView) prompts_view.findViewById(R.id.id_ad_name_value)).setText(product.get_name());
        ((TextView) prompts_view.findViewById(R.id.id_ad_elements_value)).setText(String.valueOf(product.get_elements()));
        ((TextView) prompts_view.findViewById(R.id.id_ad_code_value)).setText(product.get_code());
        ((TextView) prompts_view.findViewById(R.id.id_ad_quantity_value)).setText(product.get_quantity());
        ((TextView) prompts_view.findViewById(R.id.id_ad_brands_value)).setText(product.get_brands());
        ((TextView) prompts_view.findViewById(R.id.id_ad_expiration_value)).setText(product.get_expiration_date());
        ((TextView) prompts_view.findViewById(R.id.id_ad_labels_value)).setText(product.get_labels());
        ((TextView) prompts_view.findViewById(R.id.id_ad_added_value)).setText(product.get_timestamp());

        dialog_builder
                .setCancelable(false)
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        alert_dialog = dialog_builder.create();
        alert_dialog.show();
    }
}
