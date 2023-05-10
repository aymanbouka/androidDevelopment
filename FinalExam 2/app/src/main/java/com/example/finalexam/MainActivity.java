package com.example.finalexam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListner, RegisterFragment.RegisterFragmentListner, SearchFragment.SearchFragmentListner, MyFavoritesFragment.MyFavoritesFragmentListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerView,new LoginFragment())
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerView,new SearchFragment())
                    .commit();
        }



    }

    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new RegisterFragment()).commit();
    }



    @Override
    public void cancel() {
        getSupportFragmentManager().popBackStack();
    }
    @Override
    public void authSuccessful() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new SearchFragment())
                .commit();
    }

        @Override
        public void logout() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerView, new LoginFragment())
                    .commit();
        }

        @Override
        public void myFavorites() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerView, new MyFavoritesFragment())
                    .addToBackStack(null)
                    .commit();
        }

    @Override
    public void selectedPhoto(Photo photo) {
        getSupportFragmentManager().beginTransaction()
                // .replace(R.id.fragment_container, photoFragment)
                .replace(R.id.containerView, PhotoDetailsFragment.newInstance(photo))
                .addToBackStack(null)
                .commit();
    }
}
//     }