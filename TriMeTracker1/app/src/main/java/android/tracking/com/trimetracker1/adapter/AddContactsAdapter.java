package android.tracking.com.trimetracker1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.R;
import android.tracking.com.trimetracker1.Session;
import android.tracking.com.trimetracker1.data.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class AddContactsAdapter extends RecyclerView.Adapter<AddContactsAdapter.ViewHolder> implements ValueEventListener {

    private final FirebaseUser CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();
    private final String CURRENT_USER_ID = CURRENT_USER.getUid();
    public List<UserData> users = new ArrayList<>();
    public List<UserData> contacts = new ArrayList<>();

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
        root_loop:
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            UserData user = child.getValue(UserData.class);
            if (user == null) break;
            if (!CURRENT_USER_ID.equals(user.id) && !found(user)) {
                users.add(user);
            }
        }
        notifyDataSetChanged();
    }

    private boolean found(UserData user) {
        if (!contacts.isEmpty()) {
            for (int i = 0, size = contacts.size(); i < size; i++) {
                UserData found = contacts.get(i);
                if (user.id.equals(found.id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public AddContactsAdapter(Context context) {
        super();
        String json = Session.getInstance().getPreferences(context).getJson(CURRENT_USER_ID + "-user");
        Log.e("test", "user json: " + json);
        if (!isEmpty(json)) {
            UserData user = Session.getInstance().gson().fromJson(json, UserData.class);
            if (user != null) {
                contacts = user.contacts;
            }
        }
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
