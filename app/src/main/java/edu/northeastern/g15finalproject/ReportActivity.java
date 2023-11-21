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
        String street_address = ((TextView)findViewById(R.id.street_address_input)).getText().toString();
        String city = ((TextView)findViewById(R.id.city_input)).getText().toString();
        String state = ((TextView)findViewById(R.id.state_input)).getText().toString();
        String zipcode = ((TextView)findViewById(R.id.zipcode_input)).getText().toString();
        String detail = ((TextView)findViewById(R.id.report_detail_input)).getText().toString();

        if (isInputEmpty(street_address) || isInputEmpty(detail) || isInputEmpty(city)
                || isInputEmpty(state) || isInputEmpty(zipcode)) {
            showToast("Address and details cannot be empty");
        } else {
            Report newReport = new Report(street_address, city, state, zipcode, detail);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("report").push().setValue(newReport);
            ((TextView) findViewById(R.id.street_address_input)).setText("");
            ((TextView) findViewById(R.id.city_input)).setText("");
            ((TextView) findViewById(R.id.state_input)).setText("");
            ((TextView) findViewById(R.id.zipcode_input)).setText("");
            ((TextView)findViewById(R.id.report_detail_input)).setText("");
        }

    }

    private boolean isInputEmpty(String input) {
        return input.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
