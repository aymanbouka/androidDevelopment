package edu.uncc;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.uncc.hw06.Forums;
import edu.uncc.hw06.R;
import edu.uncc.hw06.databinding.ForumRowItemBinding;
import edu.uncc.hw06.databinding.FragmentForumsBinding;

public class ForumsFragment extends Fragment {

    public ForumsFragment() {
        // Required empty public constructor
    }

    FragmentForumsBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<Forums> forums = new ArrayList<>();

    ListenerRegistration listenerRegistration;
    ForumsAdapter forumsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forums");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        forumsAdapter = new ForumsAdapter();
        binding.recyclerView.setAdapter(forumsAdapter);

        binding.buttonCreateForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createNewForum();
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });


        listenerRegistration = db.collection("forums").addSnapshotListener(new EventListener<QuerySnapshot>() {

        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null){
                error.getStackTrace();
            }
            forums.clear();

            for (QueryDocumentSnapshot doc: value) {
                Forums forums1 = doc.toObject(Forums.class);

                forums.add(forums1);
            }

            forumsAdapter.notifyDataSetChanged();
        }
    });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (listenerRegistration != null){
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    class ForumsAdapter extends RecyclerView.Adapter<ForumsAdapter.ForumsViewHolder>{
        @NonNull
        @Override
        public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForumRowItemBinding vhBinding = ForumRowItemBinding.inflate(getLayoutInflater(),parent, false);
            return new ForumsViewHolder(vhBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumsViewHolder holder, int position) {
            Forums listOfForums = forums.get(position);

            holder.setupUid(listOfForums);
        }

        @Override
        public int getItemCount() {
            return forums.size();
        }

        class ForumsViewHolder extends RecyclerView.ViewHolder
        {
            ForumRowItemBinding mBinding;
            Forums mForums;
            public ForumsViewHolder( ForumRowItemBinding vhBinding) {
                super(vhBinding.getRoot());
                this.mBinding = vhBinding;
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("forums").document(mForums.getDocId()).delete();
                    }
                });
            }

            void setupUid(Forums forums){
                this.mForums = forums;
                mBinding.textViewForumTitle.setText(mForums.getTitle());
                mBinding.textViewForumText.setText(mForums.description);
                mBinding.textViewForumCreatedBy.setText(mForums.ownerName);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy:hh/mm a");

                
                mBinding.textViewForumLikesDate.setText(simpleDateFormat.format(mForums.time.toDate()));

                String likes = "0 likes";
                    if (mForums.likes != null && mForums.likes.size() > 0) {
                        likes = mForums.likes.size() + " likes";

                        if (mForums.likes != null && mForums.likes.contains(mAuth.getCurrentUser().getUid())) {
                            mBinding.imageViewLike.setImageResource(R.drawable.like_favorite);
                        } else {
                            mBinding.imageViewLike.setImageResource(R.drawable.like_not_favorite);
                        }
                    }
                    else{
                        mBinding.imageViewLike.setImageResource(R.drawable.like_not_favorite);
                    }
                    mBinding.imageViewLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mForums.likes != null && mForums.likes.contains(mAuth.getCurrentUser().getUid())){
                                mForums.likes.remove(mAuth.getCurrentUser().getUid());
                            }else {
                                if (mForums.likes == null){
                                    mForums.likes = new ArrayList<>();
                                }
                                mForums.likes.add(mAuth.getCurrentUser().getUid());
                            }
                            db.collection("forums").document(mForums.getDocId()).set(mForums);
                        }


                    });

                   mBinding.textViewForumLikesDate.setText(likes + " " + simpleDateFormat.format(mForums.time.toDate()));


                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.goToForum(mForums);
                    }
                });
            }

        }

    }//end of Forums class
    ForumsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsListener) context;
    }

    interface ForumsListener {
        void createNewForum();
        void goToForum(Forums forums);
        void logout();
    }
}