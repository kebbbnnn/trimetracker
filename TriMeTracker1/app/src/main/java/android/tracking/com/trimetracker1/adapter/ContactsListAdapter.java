package android.tracking.com.trimetracker1.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.R;
import android.tracking.com.trimetracker1.data.UserData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> implements ValueEventListener {
    private final FirebaseUser CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();
    private final String CURRENT_USER_ID = CURRENT_USER.getUid();
    private RecyclerView recyclerView;
    private View emptyView;
    public List<UserData> users = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot child : snapshot.getChildren()) {
            UserData userData = child.getValue(UserData.class);
            if (userData != null && CURRENT_USER_ID.equals(userData.id)) {
                if (userData.contacts.isEmpty()) {
                    showEmpty();
                } else {
                    hideEmpty();
                    users.addAll(userData.contacts);
                }
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        showEmpty();
    }

    public ContactsListAdapter(RecyclerView recyclerView, View emptyView) {
        super();
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.item_users, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserData user = users.get(position);
        holder.userName.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmpty() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
}
