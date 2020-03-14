package a2.mobile.mobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static Adapter adapter;

    public ProgressBar progressBar;
    public TextView wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        wait = findViewById(R.id.wait);

        Data.context = this;
//        File file = Data.getDataFile("points_of_interest.xls");
//
//        downloadComplete();

        // TODO: Downloading file works but printing it out on the UI doesn't.
        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/points_of_interest.xls";
        String fileUrlWithoutFileName = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/";

        Data.fetchDataFile(fileUrl, fileUrlWithoutFileName);
    }

    /**
     * Handle the file after getting it offline/online.
     */
    public void downloadComplete() {
        DataPrintHandler dataPrintHandler = new DataPrintHandler(this);
        dataPrintHandler.run();

        wait.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
}
