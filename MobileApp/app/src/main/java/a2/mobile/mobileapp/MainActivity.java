package a2.mobile.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static DataAdapter adapter;

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
        Data.getDataFile("points_of_interest.xls");

        downloadComplete();

//        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/points_of_interest.xls";
//        String fileUrlWithoutFileName = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/";
//
//        Data.fetchDataFile(fileUrl, fileUrlWithoutFileName);
    }

    /**
     * Handle the file after getting it offline/online.
     */
    public void downloadComplete() {
        DataPrintHandler dataPrintHandler = new DataPrintHandler(this);
        dataPrintHandler.run();

        wait.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(this, MapsActivityCurrentPlace.class);
        startActivity(intent);
    }
}
