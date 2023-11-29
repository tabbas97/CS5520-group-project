package edu.northeastern.g15finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class ReportMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

//    public boolean onOptionsItemSelected
    public void createReport(View view) {
        startActivity(new Intent(this, ReportActivity.class));
    }
    public void showReportNearby(View view) {
        startActivity(new Intent(this, ReportNearbyActivity.class));
    }
}
