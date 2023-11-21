package edu.northeastern.g15finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmergencyContact {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phoneNumber")
    public String phoneNumber;

    public EmergencyContact (int uid, String name, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
