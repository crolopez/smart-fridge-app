package com.crolopez.smartfridge;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ShoppingList extends Fragment {
    private String TAG = "SHOPPING_LIST";
    private ListView listview;
    private View myFragmentView = null;
    private ArrayAdapter<String> array_adapter;
    private ListAdapter list_adapter;
    private Context context;
    private ImageButton add_buttom;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup container,
                             Bundle savedInstanceState) {
        context = MainActivity.get_application_context();
        inflater = inf;
        myFragmentView = inflater.inflate(com.crolopez.smartfridge.R.layout.activity_list, container, false);
        listview = (ListView) myFragmentView.findViewById(R.id.id_listview);
        add_buttom = (ImageButton) myFragmentView.findViewById(R.id.id_shoppinglist_add);
        add_buttom.setImageResource(R.mipmap.list_icons_add);
        add_buttom.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                show_add_dialog();
            }
        });


        // ~~~~~~~~~~
        ArrayList<ListNode> list = new ArrayList<ListNode>();
        list.add(new ListNode("Product 1",5,"Dia"));
        list.add(new ListNode("Product 2",6,"Dia"));
        list.add(new ListNode("Product 3",7,"Alcampo"));
        list.add(new ListNode("Product 4",8,"Dia"));

        list_adapter = new ListAdapter(context, list, myFragmentView, inflater);
        listview.setAdapter(list_adapter);

        // ~~~~~~~~~


        return myFragmentView;
    }

    private void show_add_dialog() {
        View prompts_view;
        AlertDialog.Builder dialog_builder;
        AlertDialog alert_dialog;
        final EditText products_text_input;
        final EditText quantity_text_input;
        final EditText place_text_input;

        Log.d(TAG, "Add products buttom click.");

        prompts_view = inflater.inflate(R.layout.activity_list_add_prompt, null);
        dialog_builder = new AlertDialog.Builder(getActivity());
        dialog_builder.setView(prompts_view);

        products_text_input = (EditText) prompts_view.findViewById(R.id.id_list_add_name_value);
        quantity_text_input = (EditText) prompts_view.findViewById(R.id.id_list_add_quantity_value);
        place_text_input = (EditText) prompts_view.findViewById(R.id.id_list_add_place_value);

        dialog_builder
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String name = products_text_input.getText().toString();
                                String place = place_text_input.getText().toString();
                                String quantity = quantity_text_input.getText().toString();

                                Log.d(TAG, "Input dialog - Product: '" + name +
                                        "' - Quantity: '" + quantity + "'.");

                                if (name != "") {
                                    list_adapter.add(
                                            new ListNode(name,
                                                        (!quantity.equals("")) ? Integer.parseInt(quantity) : 1,
                                                        (!place.equals("")) ? place : "Anywhere"));
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        alert_dialog = dialog_builder.create();
        alert_dialog.show();
    }

}