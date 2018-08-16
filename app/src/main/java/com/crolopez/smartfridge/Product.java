package com.crolopez.smartfridge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Product {
    private String TAG = "PRODUCT";
    private String code;
    private String name;
    private String quantity;
    private int elements;
    private String timestamp;
    private String brands;
    private String labels;
    private String expiration_date;
    private String image_front;
    private String image_nutrition;
    private String image_ingredients;
    private ArrayList <String> additives;
    private ArrayList <Pair<String, String>> ingredients;
    private ArrayList <Pair<String, String>> allergens;

    Product(String in_code, String in_name, String in_quantity, int in_elements, String in_timestamp,
            String in_brands, String in_labels, String in_expiration_date, String in_image_front,
            String in_image_nutrition, String in_image_ingredients) {
        code = in_code;
        name = in_name;
        quantity = in_quantity;
        elements = in_elements;
        timestamp = in_timestamp;
        brands = in_brands;
        labels = in_labels;
        expiration_date = in_expiration_date;
        image_front = in_image_front;
        image_ingredients = in_image_ingredients;
        image_nutrition = in_image_nutrition;

        TAG = TAG + "-" + code;
    }

    public String get_code() { return code; }
    public String get_name() { return name; }
    public String get_quantity() { return quantity; }
    public int get_elements() { return elements; }
    public String get_timestamp() { return timestamp; }
    public String get_brands() { return brands; }
    public String get_labels() { return labels; }
    public String get_expiration_date() { return expiration_date; }
    public String get_image_front() { return image_front; }
    public String get_image_ingredients() { return image_ingredients; }
    public String get_image_nutrition() { return image_nutrition; }
    public String get_ingredient(int pos) { return (ingredients.size() > pos) ? ingredients.get(pos).first : null;}
    public String get_additives(int pos) { return (additives.size() > pos) ? additives.get(pos) : null;}
    public String get_allergen(int pos) { return (allergens.size() > pos) ? allergens.get(pos).first : null;}
    public void set_elements(int i) { elements = i;}
    public void set_name(String s) { name = s;}
    public void set_tags(ArrayList <String> additives,
                         ArrayList <Pair<String, String>> ingredients,
                         ArrayList <Pair<String, String>> allergens) {
        this.additives = additives;
        this.ingredients = ingredients;
        this.allergens = allergens;
    }

    public ImageView get_imageview() {
        Bitmap image;
        ImageView return_image = null;

        // Check front image
        image = check_image("front", return_image == null);
        return_image = (image == null) ? return_image : new ImageView(MainActivity.get_application_context());
        if (image != null) return_image.setImageBitmap(image);

        // Check ingredients image
        image = check_image("ingredients", return_image == null);
        return_image = (image == null) ? return_image : new ImageView(MainActivity.get_application_context());
        if (image != null) return_image.setImageBitmap(image);

        // Check nutrition image
        image = check_image("nutrition", return_image == null);
        return_image = (image == null) ? return_image : new ImageView(MainActivity.get_application_context());
        if (image != null) return_image.setImageBitmap(image);

        return return_image;
    }

    public Bitmap check_image(String image_tag, boolean return_image) {
        String code = get_code();
        String image_url;
        String file_base_path = MainActivity.get_application_cache_dir() + code;
        String full_path;
        String extension = ".png";
        Bitmap bitmap = null;

        if (image_tag == "front") {
            image_url = get_image_front();
        } else if (image_tag == "nutrition") {
            image_url = get_image_nutrition();
        } else {
            image_url = get_image_ingredients();
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
                bitmap = BitmapFactory.decodeFile(full_path);
            }
        }

        return bitmap;
    }


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
}
