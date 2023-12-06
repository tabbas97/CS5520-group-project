package edu.northeastern.g15finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FirebaseApp.initializeApp(TestActivity.this);
    }

    public void exAddClick(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        TestUser testUser = new TestUser();
        testUser.setTusername("testuser1");
        testUser.setTpass("testpass1");
        database.getReference().child("testUsers").push().setValue(testUser);

        TestUser testUser2 = new TestUser();
        testUser2.setTusername("testuser2");
        testUser2.setTpass("testpass2");
        database.getReference().child("testUsers").push().setValue(testUser2);
    }


    public class TestUser {

        String tusername = "";
        String tpass = "";

        public void TestUser() {

        }

        public String getTusername() {
            return tusername;
        }

        public void setTusername(String tusername) {
            this.tusername = tusername;
        }

        public String getTpass() {
            return tpass;
        }

        public void setTpass(String tpass) {
            this.tpass = tpass;
        }
    }
}