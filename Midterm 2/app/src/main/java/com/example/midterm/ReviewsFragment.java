package com.example.midterm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.midterm.databinding.FragmentReviewsBinding;
import com.example.midterm.databinding.RowItemProductBinding;
import com.example.midterm.databinding.RowItemReviewBinding;
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
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1_Product = "ARG_PARAM1_Product";


    // TODO: Rename and change types of parameters
    private Product mProduct;


    public ReviewsFragment() {
        // Required empty public constructor
    }


    public static ReviewsFragment newInstance(Product product) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1_Product, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable(ARG_PARAM1_Product);

        }
    }

    FragmentReviewsBinding binding;
    ArrayList<Review> mReviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewsBinding.inflate(inflater, container, false);


        return binding.getRoot();

    }
//https://www.theappsdr.com/api/product/reviews/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Review products");

    adapter = new reviewsAdapter(getActivity(),R.layout.row_item_review,mReviews);

    binding.listView.setAdapter(adapter);
    getReviews();

        binding.textViewProductName.setText(mProduct.getName());
        binding.textViewProductPrice.setText(mProduct.getPrice());
        Picasso.get().load(mProduct.getImg_url()).into(binding.imageViewProductIcon);

        binding.buttonCreateReview.setText("Review");

        binding.buttonCreateReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mListner.createReview(mProduct);
            }
        });

    }
    OkHttpClient client = new OkHttpClient();

    void getReviews(){
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/product/reviews/" + mProduct.getPid())
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

                        JSONArray productJsonArray = productsJson.getJSONArray("reviews");

                        mReviews.clear();

                        for (int i = 0; i < productJsonArray.length(); i++) {
                            JSONObject reviewJsonObject = productJsonArray.getJSONObject(i);
                            Review review = new Review(reviewJsonObject);
                            mReviews.add(review);
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
    reviewsAdapter adapter;

    class reviewsAdapter extends ArrayAdapter<Review> {

        public reviewsAdapter(@NonNull Context context, int resource, @NonNull List<Review> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RowItemReviewBinding reviewBinding;
            if (convertView == null) {
                reviewBinding = RowItemReviewBinding.inflate(getLayoutInflater(), parent, false);
                convertView = reviewBinding.getRoot();

                convertView.setTag(reviewBinding);

            } else {
                reviewBinding = (RowItemReviewBinding) convertView.getTag();

            }

            Review review = getItem(position);


            reviewBinding.textViewReviewDate.setText(review.getCreated_at());
            reviewBinding.textViewReview.setText(review.getReview());

            Picasso.get().load(review.getReview()).into(reviewBinding.imageViewReviewRating);

            if (review.getRating().equals("1")){
                Picasso.get().load(R.drawable.stars_1).into(reviewBinding.imageViewReviewRating);
            }
            else if (review.getRating().equals("2"))
            {
                Picasso.get().load(R.drawable.stars_2).into(reviewBinding.imageViewReviewRating);

            } else if (review.getRating().equals("3")) {
                Picasso.get().load(R.drawable.stars_3).into(reviewBinding.imageViewReviewRating);
            }
            else if (review.getRating().equals("4"))
            {
                Picasso.get().load(R.drawable.stars_4).into(reviewBinding.imageViewReviewRating);

            } else if (review.getRating().equals("5")) {
                Picasso.get().load(R.drawable.stars_5).into(reviewBinding.imageViewReviewRating);
            }


            return convertView;
        }
    }
    ReviewsFragementListner mListner;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListner = (ReviewsFragementListner) context;
    }

    interface ReviewsFragementListner{
        void createReview(Product product);
    }
}