package com.example.mahadev.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahadev.inventoryapp.data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private EditText mProductNameEditText;
    private TextView mQuantityTextView;
    private EditText mPriceEditText;
    private Button mSelectImage;
    private ImageView mProductImage;
    private Button mIncrement;
    private Button mDecrement;
    private EditText mSupplierNameEditText;
    private EditText mSupplierNumberEditText;

    private int PICK_IMAGE_REQUEST = 1;

    private Uri mCurrentProductUri;

    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            mInventoryHasChanged = true;
            return false;
        }
    };

    int quantity = 0;

    private Bitmap bitmap;

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle("Add a Product");
        }
        else {
            setTitle("Edit Product");

            getSupportLoaderManager().initLoader(0, null, this);
        }

        mProductNameEditText = (EditText) findViewById(R.id.product_name_edit_text);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_edit_text);
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        mSelectImage = (Button) findViewById(R.id.select_image_button);
        mProductImage = (ImageView) findViewById(R.id.product_image_view);
        mIncrement = (Button) findViewById(R.id.increment_button);
        mDecrement = (Button) findViewById(R.id.decrement_button);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name_edit_text);
        mSupplierNumberEditText = (EditText) findViewById(R.id.supplier_no_edit_text);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSelectImage.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNumberEditText.setOnTouchListener(mTouchListener);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity == 100) {
                    return;
                }
                quantity = quantity + 1;
                mQuantityTextView.setText(String.valueOf(quantity));
            }
        });

        mDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity == 0) {
                    return;
                }
                quantity = quantity - 1;
                mQuantityTextView.setText(String.valueOf(quantity));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d(LOG_TAG, String.valueOf(bitmap));

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                ImageView imageView = (ImageView) findViewById(R.id.product_image_view);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;

            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_order_item:
                showOrderConfirmationDialog();
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mSupplierNumberEditText.getText().toString(), null)));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.editor_delete_product_falied, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_delete_product_successful, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void saveProduct() {

        String nameString = mProductNameEditText.getText().toString().trim();
        String quantityString = String.valueOf(quantity);
        String priceString = mPriceEditText.getText().toString().trim();
        Bitmap imageBm = bitmap;
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierNumber = mSupplierNumberEditText.getText().toString().trim();

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierNumber)) {

            Toast.makeText(this, "Enter a valid information", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] productImage = new byte[0];

        if (imageBm != null) {
            imageBm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            productImage = byteArrayOutputStream.toByteArray();
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, quantityString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, priceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_IMAGE, productImage);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NUMBER, supplierNumber);

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.editor_insert_product_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_insert_product_successful, Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.editor_update_product_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_update_product_successful, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_NAME,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NUMBER
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME);
            int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
            int productImageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NUMBER);

            String productName = cursor.getString(productNameColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierNumber = cursor.getInt(supplierNumberColumnIndex);

            byte[] image = cursor.getBlob(productImageColumnIndex);
            Bitmap bm = BitmapFactory.decodeByteArray(image, 0 ,image.length);

            mProductNameEditText.setText(productName);
            mQuantityTextView.setText(String.valueOf(productQuantity));
            mPriceEditText.setText(String.valueOf(productPrice));
            mProductImage.setImageBitmap(bm);
            mSupplierNameEditText.setText(supplierName);
            mSupplierNumberEditText.setText(String.valueOf(supplierNumber));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mProductNameEditText.setText("");
        mQuantityTextView.setText("");
        mPriceEditText.setText("");
        mProductImage.setImageBitmap(null);
        mSupplierNameEditText.setText("");
        mSupplierNumberEditText.setText("");
    }

    @Override
    public void onBackPressed() {

        if (mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showOrderConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm the Order");
        builder.setPositiveButton("Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mSupplierNumberEditText.getText().toString(), null)));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
