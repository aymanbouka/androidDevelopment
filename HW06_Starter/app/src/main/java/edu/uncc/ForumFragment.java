package edu.uncc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.hw06.Comment;
import edu.uncc.hw06.databinding.CommentRowItemBinding;
import edu.uncc.hw06.databinding.ForumRowItemBinding;
import edu.uncc.hw06.databinding.FragmentForumBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1_Forums = "ARG_PARAM1_Forums";

    // TODO: Rename and change types of parameters
    private Forums mForum;
    ArrayList<Comment> commentArrayList = new ArrayList<>();

    public ForumFragment() {
        // Required empty public constructor
    }

 static ForumFragment newInstance(Forums forums) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1_Forums, forums);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mForum = (Forums) getArguments().getSerializable(ARG_PARAM1_Forums);

        }
    }

    FragmentForumBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    CommentAdapter adapter;
    ListenerRegistration listenerRegistration;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forum");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CommentAdapter();

        binding.recyclerView.setAdapter(adapter);

        binding.textViewForumTitle.setText(mForum.getTitle());
        binding.textViewForumText.setText(mForum.getDescription());
        binding.textViewForumCreatedBy.setText(mForum.getOwnerName());

        listenerRegistration = db.collection("forums").document(mForum.docId).collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                 if (error != null)
                 {
                     error.getStackTrace();
                 }

                 commentArrayList.clear();

                for (QueryDocumentSnapshot doc: value) {
                    Comment comment = doc.toObject(Comment.class);
                    commentArrayList.add(comment);
                }
                adapter.notifyDataSetChanged();
            }
        });



        binding.buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = binding.editTextComment.getText().toString();

                if(comment.isEmpty())
                {
                    binding.editTextComment.setError("enter a comment");

                }else {
                    //add comment to firebase and refresh the list
                    DocumentReference docRef = db.collection("forums").document(mForum.getDocId()).collection("comments").document();


                    HashMap<String, Object> data = new HashMap<>();

                    data.put("comment", comment);
                    data.put("ownerName", mAuth.getCurrentUser().getDisplayName());
                    data.put("ownerId", mAuth.getCurrentUser().getUid());
                    data.put("docId", docRef.getId());
                    data.put("time", FieldValue.serverTimestamp());

                    docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.editTextComment.setText("");


                        }
                    });
                }



            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null)
            listenerRegistration.remove();
    }

    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentRowItemBinding vBinding = CommentRowItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(vBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
    Comment comment = commentArrayList.get(position);

    holder.setupUI(comment);

    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        CommentRowItemBinding mBinding;
        Comment mComment;
            public CommentViewHolder(CommentRowItemBinding vBinding) {
                super(vBinding.getRoot());
                this.mBinding = vBinding;

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("forums").document(mForum.getDocId()).collection("comments").document(mComment.getDocId()).delete();
                    }
                });
            }

            void setupUI(Comment comment)
            {
                this.mComment = comment;
                mBinding.textViewCommentText.setText(mComment.getComment());
                mBinding.textViewCommentCreatedBy.setText(mComment.getName());

                if (mComment.getTime() != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy:hh/mm a");
                    mBinding.textViewCommentCreatedAt.setText(simpleDateFormat.format(mComment.time.toDate()));

                }
                else {
                    mBinding.textViewCommentCreatedAt.setText("NA");
                }

            }
        }
}


}