package com.example.mahadev.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahadev.inventoryapp.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.stock_no_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.price_no_text_view);
        ImageView productImageImageView = (ImageView) view.findViewById(R.id.product_image_view);

        int idIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME);
        int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int productImageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE);

        int id = cursor.getInt(idIndex);
        String productName = cursor.getString(productNameColumnIndex);
        final int productQuantity = cursor.getInt(productQuantityColumnIndex);
        int productPrice = cursor.getInt(productPriceColumnIndex);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_no_image);

        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        final Uri uri = Uri.parse(InventoryContract.InventoryEntry.CONTENT_URI + "/" + id);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Are you sure want sell?");
                builder.setPositiveButton(R.string.update_quantity_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        int updatedProductQuantity = productQuantity - 1;
                        ContentValues updateValues = new ContentValues();

                        if (updatedProductQuantity < 0) {
                            Toast.makeText(context, "Insufficient quantity to complete order", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }

                        updateValues.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, updatedProductQuantity);

                        int rowsAffected = context.getContentResolver().update(uri, updateValues, null, null);

                        if (rowsAffected > 0) {
                            Toast.makeText(context, "Sale has been recorded", Toast.LENGTH_SHORT).show();
                            context.getContentResolver().notifyChange(uri, null);
                        } else {
                            Toast.makeText(context, "Sale could not be recorded", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });

        productNameTextView.setText(productName);
        productQuantityTextView.setText(String.valueOf(productQuantity));
        productPriceTextView.setText(String.valueOf(productPrice));
        productImageImageView.setImageBitmap(bitmap);
    }
}
