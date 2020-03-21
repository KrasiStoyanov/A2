package a2.mobile.mobileapp;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

// TODO: This should properly handle UI response messages and print data properly and accordingly.
public class DataPrintHandler implements Runnable {
    private Context context;

    public DataPrintHandler(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        MainActivity.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        MainActivity.adapter = new DataAdapter(context, Data.startPoint, Data.targetDestination, Data.pointsOfInterest);

        MainActivity.recyclerView.setAdapter(MainActivity.adapter);
    }
}
