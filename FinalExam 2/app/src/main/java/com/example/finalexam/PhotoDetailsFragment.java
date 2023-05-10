package com.example.finalexam;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalexam.databinding.FragmentPhotoDetailsBinding;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1_PHOTO = "ARG_PARAM1_PHOTO";


    // TODO: Rename and change types of parameters
    private Photo mPhoto;


    public PhotoDetailsFragment() {
        // Required empty public constructor
    }


    public static PhotoDetailsFragment newInstance(Photo photo) {
        PhotoDetailsFragment fragment = new PhotoDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1_PHOTO, photo);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhoto = (Photo) getArguments().getSerializable(ARG_PARAM1_PHOTO);

        }
    }

    FragmentPhotoDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Photo Details");

        binding.textViewDescription.setText(mPhoto.getDescription());
        binding.textViewCreatedAt.setText(mPhoto.getCreated_at());
        Picasso.get().load(mPhoto.getThumb()).into(binding.imageViewThumbnail);
        Picasso.get().load(mPhoto.getProfile_image()).into(binding.imageViewUserThumbnail);
        binding.textViewUserFullName.setText(mPhoto.getUsername());

        binding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewsUrl(mPhoto.url);
            }
        });
    }

    public void openNewsUrl(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
    }
}