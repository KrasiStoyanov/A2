package a2.mobile.mobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.chainsaw.Main;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private final Handler dataPrintHandler = new DataPrintHandler(this);

    public static RecyclerView recyclerView;
    public static Adapter adapter;

    public static ProgressBar progressBar;
    public static TextView wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        wait = findViewById(R.id.wait);

        Data.context = this;
        File file = Data.getDataFile("points_of_interest.xls");

        printData();

        // TODO: Downloading file works but printing it out on the UI doesn't.
//        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/points_of_interest.xls";
//        String fileUrlWithoutFileName = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/";
//
//        Data.fetchDataFile(fileUrl, fileUrlWithoutFileName);

//        dataPrintHandler.postDelayed(sRunnable, 1000);

        wait.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void printData() {
        File fetchedFile = Data.fetchedFile;
        if (fetchedFile == null || !fetchedFile.exists()) {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, Data.startPoint, Data.targetDestination, Data.pointsOfInterest);

        recyclerView.setAdapter(adapter);
    }

    /**
     * Instances of anonymous classes do not hold an implicit reference to their outer class when they are "static".
     */
    private static final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    private static class DataPrintHandler extends Handler {
        private final WeakReference<MainActivity> activity;

        public DataPrintHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            File fetchedFile = Data.fetchedFile;
            if (fetchedFile == null || !fetchedFile.exists()) {
                return;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(activity.get()));
            adapter = new Adapter(activity.get(), Data.startPoint, Data.targetDestination, Data.pointsOfInterest);

            recyclerView.setAdapter(adapter);
        }
    }
}
