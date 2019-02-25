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
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddContactsAdapter extends RecyclerView.Adapter<AddContactsAdapter.ViewHolder> implements ValueEventListener {

    public List<UserData> users = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        public View check;
        public boolean added = false;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            check = itemView.findViewById(R.id.imgCheck);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> usersMap = (Map<String, Object>) dataSnapshot.getValue();
        for (Map.Entry<String, Object> entry : usersMap.entrySet()) {

            Map singleUser = (Map) entry.getValue();

            String id = (String) singleUser.get("id");
            String name = (String) singleUser.get("name");
            String email = (String) singleUser.get("email");
            String mobile = (String) singleUser.get("mobile");
            long createdAt = (Long) singleUser.get("createdAt");

            if (!currentUserId.equals(id)) {
                UserData user = new UserData(id, name, email, mobile, createdAt);
                users.add(user);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public AddContactsAdapter() {
        super();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.item_add_contact, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData user = users.get(position);
        holder.userName.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public UserData getUser(int position) {
        return users.get(position);
    }
}
