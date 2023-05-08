package com.example.midterm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.midterm.databinding.FragmentProductsBinding;
import com.example.midterm.databinding.RowItemProductBinding;
import com.example.midterm.models.Product;
import com.example.midterm.models.Review;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductsFragment() {
        // Required empty public constructor
    }




    public static ProductsFragment newInstance(String param1, String param2) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    OkHttpClient client = new OkHttpClient();
    FragmentProductsBinding binding;

    productsAdapter adapter;

    ArrayList<Product> mProducts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Products");
        adapter = new productsAdapter(getActivity(), R.layout.row_item_product,mProducts);

        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = mProducts.get(i);

                mListner.goToReviews(product);
            }
        });

        getProductsData();
    }

    void getProductsData(){
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/products")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();

                    try {
                        JSONObject productsJson = new JSONObject(body);

                        JSONArray productJsonArray = productsJson.getJSONArray("products");

                        mProducts.clear();

                        for (int i = 0; i < productJsonArray.length(); i++) {
                            JSONObject productJsonObject = productJsonArray.getJSONObject(i);
                            Product product = new Product(productJsonObject);
                            mProducts.add(product);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
//                        for (int i = 0; i < contactsJsonArray.length(); i++) {
//                            JSONObject contactJsonObject = contactsJsonArray.getJSONObject(i);
//                            Contact contact = new Contact(contactJsonObject);
//                            contacts.add(contact);
//                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else {
                    Toast.makeText(getActivity(), "Products not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    class productsAdapter extends ArrayAdapter<Product>{

        public productsAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RowItemProductBinding rowItemProductBinding;
            if (convertView == null){
                rowItemProductBinding = RowItemProductBinding.inflate(getLayoutInflater(),parent,false);
                convertView = rowItemProductBinding.getRoot();

                convertView.setTag(rowItemProductBinding);

            }
            else {
                rowItemProductBinding = (RowItemProductBinding) convertView.getTag();

            }

            Product product = getItem(position);

            rowItemProductBinding.textViewProductDesc.setText(product.getDescription());
            rowItemProductBinding.textViewProductName.setText(product.getName());
            rowItemProductBinding.textViewProductPrice.setText(product.getPrice());
            rowItemProductBinding.textViewProductReviews.setText(product.getReview_count());

            Picasso.get().load(product.getImg_url()).into(rowItemProductBinding.imageViewProductIcon);






            return convertView;
        }
    }

    ProductFragmentListner mListner;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListner = (ProductFragmentListner) context;

    }

    interface ProductFragmentListner{
//        void sendProduct(Product product);

        void goToReviews(Product product);
    }


}//end class