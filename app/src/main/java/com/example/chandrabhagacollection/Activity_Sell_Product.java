package com.example.chandrabhagacollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_Sell_Product extends AppCompatActivity {

    Context context;
    Button btAdd;
    ProgressBar progressBar;
    EditText edtCatalogName, edtBrandName, edtQuantity, edtPrice;
    Spinner spinnerType;

    private List<String> listoccupation;
    ArrayList<Products> ProductList;

    private DatabaseReference mDatabaseRefsales;
    private DatabaseReference mDatabase;
    String key, cat, type;
    Products Product;
    LeedRepository leedRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sell__product);

        mDatabaseRefsales = FirebaseDatabase.getInstance().getReference("Sales");
        ProductList = new ArrayList<>();

        Intent i = getIntent();
        Product = (Products) i.getSerializableExtra("Product");
        leedRepository = new LeedRepositoryImpl();

        edtCatalogName = (EditText) findViewById(R.id.catalog_name);
        edtCatalogName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edtBrandName = (EditText) findViewById(R.id.brand_name);
        edtBrandName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edtQuantity = (EditText) findViewById(R.id.quantity);
        edtPrice = (EditText) findViewById(R.id.rate_per_peice);
        spinnerType = (Spinner) findViewById(R.id.spinner_type);

        btAdd = (Button) findViewById(R.id.btn_add_purchase);

        listoccupation = new ArrayList<>();
        listoccupation.add("Saree");
        listoccupation.add("Dress Material");
        listoccupation.add("Kurti");
        listoccupation.add("Leggins");

        ArrayAdapter<String> occupation = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, listoccupation);

        occupation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(occupation);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddData();

            }
        });

        getData();

    }

    private void getData() {
        String type = Product.getType();
        edtCatalogName.setText(Product.getCatalogName());
        edtBrandName.setText(Product.getBrandName());
        if (type != null) {
            ArrayAdapter myAdap = (ArrayAdapter) spinnerType.getAdapter();
            spinnerType.setSelection(myAdap.getPosition(type));
        }
    }

    private void AddData() {
        key = mDatabaseRefsales.push().getKey();
        Products product = new Products();
        product.setType(spinnerType.getSelectedItem().toString());
        product.setBrandName(edtBrandName.getText().toString());
        product.setCatalogName(edtCatalogName.getText().toString());
        product.setQuantity(edtQuantity.getText().toString());
        product.setPrice(edtPrice.getText().toString());

        mDatabaseRefsales.child(key).setValue(product);

        type = spinnerType.getSelectedItem().toString();
        cat = edtCatalogName.getText().toString();
        String brand = edtBrandName.getText().toString();

//        Query query3 = FirebaseDatabase.getInstance().getReference("Stock")
//                .orderByChild("brandName")
//                .equalTo(brand);
//        query3.addListenerForSingleValueEvent(valueEventListener);
        setLeedStatus(Product);

        Toast.makeText(getApplicationContext(), "Product Addred", Toast.LENGTH_SHORT).show();
    }

//    ValueEventListener valueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            ProductList.clear();
//            if (dataSnapshot.exists()) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Products subproducts1 = snapshot.getValue(Products.class);
//                    if (subproducts1.getCatalogName().equalsIgnoreCase(cat) && subproducts1.getType().equalsIgnoreCase(type)) {
//                        ProductList.add(subproducts1);
//                    }
//
//                }
//                UpdateStock(ProductList);
//                // subCatalogAdapter.notifyDataSetChanged();
//            }
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    };

    private void setLeedStatus(Products products) {
        if (products != null) {
            String price = products.getQuantity();
            int p;
            p = Integer.valueOf(price) - Integer.valueOf(edtQuantity.getText().toString());
            key = products.getProductId();
            products.setQuantity(String.valueOf(p));
            updateLeed(products.getProductId(), products.getLeedStatusMap());
//            mDatabase = FirebaseDatabase.getInstance().getReference();
//            mDatabase.child("Stock").child(key).child("quantity").setValue(String.valueOf(p));
        }

    }

    private void updateLeed(String requestId, Map leedStatusMap) {
        leedRepository.updateLeed(requestId, leedStatusMap, new CallBack() {
            @Override
            public void onSuccess(Object object) {


            }


            @Override
            public void onError(Object object) {
                Utility.showLongMessage(getApplication(), getString(R.string.server_error));
            }
        });
    }

}
