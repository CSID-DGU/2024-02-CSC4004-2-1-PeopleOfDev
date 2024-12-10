package com.example.momentup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationItem> notifications = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification, int position);
    }

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void addNotification(NotificationItem notification) {
        notifications.add(0, notification);
        notifyItemInserted(0);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView titleView;
        private TextView messageView;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iv_notification_icon);
            titleView = itemView.findViewById(R.id.tv_notification_title);
            messageView = itemView.findViewById(R.id.tv_notification_message);
        }

        void bind(final NotificationItem item, final OnNotificationClickListener listener) {
            iconView.setImageResource(item.getIconResId());
            titleView.setText(item.getTitle());
            messageView.setText(item.getMessage());

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNotificationClick(item, position);
                }
            });
        }
    }
}