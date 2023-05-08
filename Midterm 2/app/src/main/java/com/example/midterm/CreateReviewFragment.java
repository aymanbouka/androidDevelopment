package com.example.midterm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.midterm.databinding.FragmentCreateReviewBinding;
import com.example.midterm.models.Product;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateReviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1_PRODUCT = "ARG_PARAM1_PRODUCT";


    // TODO: Rename and change types of parameters
    private Product mProduct;


    public CreateReviewFragment() {
        // Required empty public constructor
    }


    public static CreateReviewFragment newInstance(Product product) {
        CreateReviewFragment fragment = new CreateReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1_PRODUCT, product);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable(ARG_PARAM1_PRODUCT);

        }
    }
    private final OkHttpClient client = new OkHttpClient();

    FragmentCreateReviewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Create a Review");

        binding.textViewProductPrice.setText(mProduct.getPrice());
        Picasso.get().load(mProduct.getImg_url()).into(binding.imageViewProductIcon);
        binding.textViewProductName.setText(mProduct.getName());

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String review = binding.editTextReview.getText().toString();

                if (review.isEmpty())
                {
                    Toast.makeText(getActivity(), "Enter a review", Toast.LENGTH_SHORT).show();
                }

                int selectedItem = binding.radioGroup.getCheckedRadioButtonId();

                int rating = 0;
                if (selectedItem == R.id.radioButton_level_1)
                {
                    rating = 1;
                }
                else if (selectedItem == R.id.radioButton_level_2)
                {
                    rating = 2;
                }
                else if (selectedItem == R.id.radioButton_level_3)
                {
                    rating = 3;
                }
                else if (selectedItem == R.id.radioButton_level_4)
                {
                    rating = 4;
                }
                else if (selectedItem == R.id.radioButton_level_5)
                {
                    rating = 5;
                }

                    RequestBody formBody = new FormBody.Builder()
                            .add("review", review)
                            .add("rating", String.valueOf(rating))
                            .add("pid",mProduct.getPid())
                            .build();
                    Request request = new Request.Builder()
                            .url("https://www.theappsdr.com/api/product/review")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            if (response.isSuccessful()){
                               getActivity().runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       mListner.goBackToReviewFragment();
                                   }
                               });
                            }
                            else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });


            }
        });

   }

    CreateReviewListner mListner;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListner = (CreateReviewListner) context;
    }

    interface CreateReviewListner {
        void goBackToReviewFragment();
   }
}