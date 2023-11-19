package edu.northeastern.g15finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    public void addReport(View view) {
        String address = ((TextView)findViewById(R.id.address_input)).getText().toString();
        String detail = ((TextView)findViewById(R.id.report_detail_input)).getText().toString();

        if (isInputEmpty(address) || isInputEmpty(detail)) {
            showToast("Address and details cannot be empty");
        } else {
            Report newReport = new Report(address, detail);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("report").push().setValue(newReport);

        }

    }

    private boolean isInputEmpty(String input) {
        return input.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
