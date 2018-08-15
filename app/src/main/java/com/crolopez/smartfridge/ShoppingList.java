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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList extends Fragment {
    private String TAG = "SHOPPING_LIST";
    private ListView listview;
    private View myFragmentView = null;
    private static ListAdapter list_adapter;
    private Context context;
    private ImageButton add_buttom;
    private ImageButton clear_buttom;
    private LayoutInflater inflater;
    private int add_mode = 0;
    private int modify_mode = 1;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup container,
                             Bundle savedInstanceState) {
        context = MainActivity.get_application_context();
        inflater = inf;
        myFragmentView = inflater.inflate(com.crolopez.smartfridge.R.layout.activity_list, container, false);
        listview = (ListView) myFragmentView.findViewById(R.id.id_listview);
        add_buttom = (ImageButton) myFragmentView.findViewById(R.id.id_shoppinglist_add);
        clear_buttom = (ImageButton) myFragmentView.findViewById(R.id.id_shoppinglist_remove);
        add_buttom.setImageResource(R.mipmap.list_icons_add);
        clear_buttom.setImageResource(R.mipmap.list_icons_remove);
        add_buttom.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                show_modify_dialog(add_mode, null);
            }
        });
        clear_buttom.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                show_clear_dialog();
            }
        });

        // Init the list
        ArrayList<ListNode> list = new ArrayList<ListNode>();
        list_adapter = new ListAdapter(context, list, myFragmentView, inflater);
        list_adapter.load_states();
        listview.setAdapter(list_adapter);

        // On pressed click
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListNode node = list_adapter.getItem(position);
                Log.d(TAG, "Simple long click on: " + node.get_name());
                show_modify_dialog(modify_mode, node);
                return true;
            }
        });

        // On click
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListNode node = list_adapter.getItem(position);
                node.change_mark();
                list_adapter.notifyDataSetChanged();
            }
        });

        return myFragmentView;
    }

    public static List<ListNode> get_products() {
        return list_adapter.get_all();
    }

    private void show_modify_dialog(final int mode, final ListNode node) {
        View prompts_view;
        AlertDialog.Builder dialog_builder;
        AlertDialog alert_dialog;
        final EditText products_text_input;
        final EditText quantity_text_input;
        final EditText place_text_input;
        String positive_button = null;

        prompts_view = inflater.inflate(R.layout.activity_list_modify_add_prompt, null);
        dialog_builder = new AlertDialog.Builder(getActivity());
        dialog_builder.setView(prompts_view);

        products_text_input = (EditText) prompts_view.findViewById(R.id.id_list_add_name_value);
        quantity_text_input = (EditText) prompts_view.findViewById(R.id.id_list_add_quantity_value);
        place_text_input = (EditText) prompts_view.findViewById(R.id.id_list_add_place_value);

        // Check the dialog mode
        if (mode == add_mode) {
            Log.d(TAG, "Add products button click.");
            positive_button = "Add";
        } else if (mode == modify_mode) {
            Log.d(TAG, "Showing modifier mode.");
            positive_button = "Set";
            products_text_input.setText(node.get_name());
            quantity_text_input.setText(Integer.toString(node.get_elements()));
            place_text_input.setText(node.get_place());
        }

        dialog_builder
                .setCancelable(false)
                .setPositiveButton(positive_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String name = products_text_input.getText().toString();
                                String place = place_text_input.getText().toString();
                                String quantity = quantity_text_input.getText().toString();

                                Log.d(TAG, "Input dialog - Product: '" + name +
                                        "' - Quantity: '" + quantity +
                                        "' - Place: '" + place + "'.");

                                if (name != "") {
                                    if (mode == add_mode) {
                                        list_adapter.add(
                                                new ListNode(name,
                                                            (!quantity.equals("")) ? Integer.parseInt(quantity) : 1,
                                                            place,
                                                            null,
                                                            null,
                                                            false));
                                    } else if (mode == modify_mode) {
                                        list_adapter.modify_element(
                                                            node,
                                                            name,
                                                            (!quantity.equals("")) ? Integer.parseInt(quantity) : 1,
                                                            place);
                                    }
                                }
                            }
                        })
                .setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        if (mode == add_mode) {
            dialog_builder.setTitle("Add product");
        } else if (mode == modify_mode) {
            dialog_builder.setTitle("Modify the product")
                            .setNegativeButton("Remove",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    list_adapter.remove(node);
                                }
                            });
        }

        alert_dialog = dialog_builder.create();
        alert_dialog.show();
    }

    private void show_clear_dialog() {
        View prompts_view;
        AlertDialog.Builder dialog_builder;
        AlertDialog alert_dialog;

        Log.d(TAG, "Clear products buttom click.");

        prompts_view = inflater.inflate(R.layout.activity_list_clear_prompt, null);
        dialog_builder = new AlertDialog.Builder(getActivity());
        dialog_builder.setView(prompts_view);

        dialog_builder
                .setCancelable(false)
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                list_adapter.clear();
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