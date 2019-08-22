package personal.project.android.firebasenotifications;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;


public class UsersFragment extends Fragment {

    private List<Users> list;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    public UsersFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_users, container, false);
        // Inflate the layout for this fragment
        recyclerView=v.findViewById(R.id.rv);
        list=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        usersRecyclerAdapter=new UsersRecyclerAdapter(list, Objects.requireNonNull(getActivity()).getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(usersRecyclerAdapter);

        if(auth.getCurrentUser()!=null) {
            list.clear();
            firebaseFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(queryDocumentSnapshots!=null){
                        for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType()== DocumentChange.Type.ADDED){
                                String user_id=doc.getDocument().getId();
                                Users users=doc.getDocument().toObject(Users.class).withId(user_id);
                                list.add(users);
                                usersRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }
            });
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        usersRecyclerAdapter.notifyDataSetChanged();
    }
}
