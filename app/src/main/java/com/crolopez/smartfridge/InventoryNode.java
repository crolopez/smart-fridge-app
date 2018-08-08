package com.crolopez.smartfridge;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class InventoryNode {
    private String TAG = "INV_NODES";
    private boolean is_valid;
    private Cursor cursor = null;
    private Context context;
    private String cache_dir;
    private int text_size_dp = 15;

    InventoryNode(Cursor cursor) {
        if (cursor.getCount() > 0) {
            is_valid = true;
        } else {
            is_valid = false;
            return;
        }
        this.cursor = cursor;
        context = MainActivity.get_application_context();
        cache_dir = context.getCacheDir().getAbsolutePath() + "/";
    }

    public boolean is_valid() {
        return is_valid;
    }

    public boolean next_node() {
        return cursor.moveToNext();
    }

    public String get_code() {
        return cursor.getString(cursor.getColumnIndex("CODE"));
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

    public String get_front_image() {
        return cursor.getString(cursor.getColumnIndex("FRONT"));
    }

    public String get_nutrition_image() {
        return cursor.getString(cursor.getColumnIndex("NUTRITION"));
    }

    public String get_ingredients_image() {
        return cursor.getString(cursor.getColumnIndex("INGREDIENTS"));
    }

    public TableRow get_simple_row() {
        TableRow row;

        // Get the columns values
        String[] colText = {
                this.get_code(),
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
        TableLayout left_margin_element;
        TableLayout right_margin_element;
        TableLayout first_element;
        ImageView image_element;
        TableRow.LayoutParams layout_params;

        // Create the row
        row = new TableRow(context);
        //row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, dps_to_pixel(40)));
        row.setWeightSum(100);

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
        first_element.addView(create_layout_text(get_name(), null, true, false));
        first_element.addView(create_layout_text(get_elements(), "Elements: ", false, true));
        //first_element.setBackgroundColor(0xFF00FF00);

        image_element = get_imageview(get_code());
        if (image_element != null) {
            layout_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout_params.gravity = Gravity.RIGHT;
            layout_params.weight = 20;
            //layout_params.bottomMargin = layout_params.topMargin = layout_params.leftMargin = layout_params.rightMargin = 1;
            image_element.setLayoutParams(layout_params);
            image_element.setVisibility(View.VISIBLE);
            //image_element.setAdjustViewBounds(true);

        } else {
            // Create empty imageview ~~
        }
        //second_element.setBackgroundColor(0xFFF0FF00);

        // Add the elements to the row
        row.addView(left_margin_element);
        row.addView(first_element);
        row.addView(image_element);
        row.addView(right_margin_element);

        //row.addView(margin_element);

        //row.setBackgroundColor(0xFF0FFFF0);

        return row;
    }

    private ImageView get_imageview(String code) {
        ImageView image;
        ImageView return_image = null;

        // Check front image
        image = check_image(code, "front", return_image == null);
        return_image = (return_image != null) ? return_image : image;

        // Check ingredients image
        image = check_image(code, "ingredients", return_image == null);
        return_image = (return_image != null) ? return_image : image;

        // Check nutrition image
        image = check_image(code, "nutrition", return_image == null);
        return_image = (return_image != null) ? return_image : image;

        return return_image;
    }

   private int dps_to_pixel(int dps) {
        final float conversion_scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * conversion_scale + 0.5f);
   }

    private ImageView check_image(String code, String image_tag, boolean return_image) {
        ImageView image = null;
        String image_url;
        String file_base_path = cache_dir + code;
        String full_path;
        String extension = ".png";
        BitmapFactory.Options opt;
        Bitmap bitmap;

        if (image_tag == "front") {
            image_url = get_front_image();
        } else if (image_tag == "nutrition") {
            image_url = get_nutrition_image();
        } else {
            image_url = get_ingredients_image();
        }
        if (image_url != null) {
            full_path = file_base_path + "-" + image_tag + extension;
            if (new File(full_path).exists()) {
                Log.d(TAG, "The " + image_tag + " image for " + code + " already exists.");
            } else {
                Log.d(TAG, "The " + image_tag + " image for " + code + " doesn't exists. Downloading...");
                new DownloadImages().execute(image_url, full_path);
            }
            if (return_image) {
                int height, width;
                int bounding = dps_to_pixel(250);
                float xScale, yScale;
                float scale;
                //opt = new BitmapFactory.Options();
                //opt.inMutable = true;
                //bitmap = BitmapFactory.decodeFile(full_path, opt);
                bitmap = BitmapFactory.decodeFile(full_path);

                /*
                height = bitmap.getHeight();
                width = bitmap.getWidth();
                xScale = ((float) bounding) / width;
                yScale = ((float) bounding) / height;
                scale = (xScale <= yScale) ? xScale : yScale;*/
/*
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                bitmap.scale*/
                image = new ImageView(context);
                image.setImageBitmap(bitmap);
            }
        }

        return image;
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

    public void save_image(Bitmap b, String image_path) {
        FileOutputStream output;

        try {
            output = new FileOutputStream (new File(image_path), true);
            b.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not save the image " + image_path + ".");
            e.printStackTrace();
        }
    }

    /*
    public Bitmap load_bitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream    = context.openFileInput(imageName);
            bitmap      = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 3, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    } */

    private class DownloadImages extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private String file_path;

        private Bitmap download_bitmap(String url) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception.");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            file_path = params[1];
            return download_bitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            save_image(result, file_path);
            Log.d(TAG, "'" + file_path + "' successfully saved.");
        }
    }
}
