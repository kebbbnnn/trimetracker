package android.tracking.com.trimetracker1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.R;
import android.tracking.com.trimetracker1.data.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SharedLocationAdapter extends RecyclerView.Adapter<SharedLocationAdapter.ViewHolder> {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);

    private List<Message> eventList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            textLabel = itemView.findViewById(R.id.textLabel);
        }
    }

    public void setEventList(List<Message> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    public SharedLocationAdapter() {
        super();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.item_shared_loc, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message event = eventList.get(position);
        holder.textLabel.setText(String.format("%s shared location on %s", event.getSenderName(), getDate(event.getCreatedAt())));
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public String getDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return DATE_FORMAT.format(calendar.getTime());
    }

    public Message getItem(int position) {
        return eventList.get(position);
    }
}
