package a2.mobile.mobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Data dataDistributor;

    RecyclerView recyclerView;
    Adapter adapter;

    ProgressBar progressBar;
    TextView wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        wait = findViewById(R.id.wait);

        dataDistributor = new Data(this);
//        File file = dataDistributor.getDataFile("points_of_interest.xls");

//        TODO: Data.java doesn't work with online files yet.
        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/blob/master/MobileApp/app/src/main/assets/points_of_interest.xls?raw=true";
        File fetchedFile = dataDistributor.fetchDataFile(fileUrl);

        printData();

        wait.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void printData() {
        File fetchedFile = dataDistributor.getFetchedFile();
        if (fetchedFile == null || fetchedFile.length() == 0) {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, dataDistributor.getStartPoint(), dataDistributor.getTargetDestination(), dataDistributor.getPointsOfInterest());

        recyclerView.setAdapter(adapter);
    }
}
