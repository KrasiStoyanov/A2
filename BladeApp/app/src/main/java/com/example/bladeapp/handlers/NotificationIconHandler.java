package com.example.bladeapp.handlers;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bladeapp.adapter.NotificationIconAdapter;

import java.util.List;

public class NotificationIconHandler {
    private static NotificationIconAdapter notificationIconAdapter;
    private static LinearLayoutManager layoutManager;

    public static void handleNotification(
            final Context context,
            final View rootView,
            final List<String[]> pointsOfInterest) {

        RecyclerView notificationIconHolder = (RecyclerView) rootView;
        notificationIconAdapter = new NotificationIconAdapter(
                context,
                pointsOfInterest
        );

        notificationIconHolder.setAdapter(notificationIconAdapter);

        layoutManager = new LinearLayoutManager(context);
        notificationIconHolder.setLayoutManager(layoutManager);
    }

    public static void removeNotification(String title, String interest, String priority) {
        notificationIconAdapter.removeNotification(title, interest, priority);
    }
}
