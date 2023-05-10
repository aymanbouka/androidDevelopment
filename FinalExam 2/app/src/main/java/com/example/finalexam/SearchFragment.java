package com.example.finalexam;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalexam.databinding.FragmentSearchBinding;
import com.example.finalexam.databinding.ListItemPhotoSearchBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentSearchBinding binding;

    PhotoAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    OkHttpClient client = new OkHttpClient();

    ArrayList<Photo> mPhotos = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new PhotoAdapter();

        binding.recyclerView.setAdapter(adapter);


        getActivity().setTitle("Search Fragment");

        String searchQuery = binding.editTextSearchKeyword.getText().toString();
        getSearchResults(searchQuery);

        binding.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = binding.editTextSearchKeyword.getText().toString();
                getSearchResults(searchQuery);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout_menu_item){
            //logout code goes here ...
            mListener.logout();
            return true;
        } else if(item.getItemId() == R.id.my_favorites_menu_item){
            //my favorites code goes here ...



            mListener.myFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getSearchResults(String query){
        //base url is https://api.unsplash.com/search/photos/?client_id=b1Q4Z3jQRDIIbTQIlVTXj17qv1kXW5CpeIt4PGkPc-o&query=user-entered-keywords&per_page=50&orientation=landscape&content_filter=high

        Request request = new Request.Builder()
                .url("https://api.unsplash.com/search/photos/?client_id=b1Q4Z3jQRDIIbTQIlVTXj17qv1kXW5CpeIt4PGkPc-o&query="+query+"&per_page=50&orientation=landscape&content_filter=high")
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
                        JSONObject photosJson = new JSONObject(body);
                        JSONArray photosJsonArray = photosJson.getJSONArray("results");

                        mPhotos.clear();

                        for (int i = 0; i < photosJsonArray.length(); i++) {
                            JSONObject photosJsonObject = photosJsonArray.getJSONObject(i);
                            Photo photo = new Photo(photosJsonObject);
                            mPhotos.add(photo);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>
    {
        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemPhotoSearchBinding vhBinding = ListItemPhotoSearchBinding.inflate(getLayoutInflater(),parent,false);

            return new PhotoViewHolder(vhBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            Photo listOfPhotos = mPhotos.get(position);

            holder.setUpUid(listOfPhotos);



        }

        @Override
        public int getItemCount() {
            return mPhotos.size();
        }

        class PhotoViewHolder extends RecyclerView.ViewHolder {
            ListItemPhotoSearchBinding mBinding;
            Photo mPhoto;
            public PhotoViewHolder(ListItemPhotoSearchBinding vhBinding) {
                super(vhBinding.getRoot());
                this.mBinding = vhBinding;
            }

            void setUpUid(Photo photo) {
                this.mPhoto = photo;

                mBinding.textViewDescription.setText(mPhoto.getDescription());
                mBinding.textViewCreatedAt.setText(mPhoto.getCreated_at());
                mBinding.textViewUserFullName.setText(mPhoto.getUsername());
                Picasso.get().load(mPhoto.getThumb()).into(mBinding.imageViewThumbnail);
                Picasso.get().load(mPhoto.getProfile_image()).into(mBinding.imageViewUserThumbnail);


                mBinding.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DocumentReference docRef = db.collection("favorite").document();

                        /*
                        created_at,description,username,small,thumb,url,profile_image,id;
                         */

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("id",mPhoto.getId());
                        data.put("userId",mAuth.getCurrentUser().getUid());
                        data.put("description",mPhoto.getDescription());
                        data.put("username",mPhoto.getUsername());
                        data.put("profile_image",mPhoto.getProfile_image());
                        data.put("created_at",mPhoto.getCreated_at());
                        data.put("thumb",mPhoto.getThumb());
                        data.put("docId",docRef.getId());
                        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mListener.myFavorites();
                                }
                                else{
                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



                    }
                });
            }
        }


    }



    SearchFragmentListner mListener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SearchFragmentListner) context;
    }

    interface SearchFragmentListner {
        void logout();
        void myFavorites();
    }
}