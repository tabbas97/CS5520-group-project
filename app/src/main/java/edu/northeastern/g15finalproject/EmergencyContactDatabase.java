package edu.northeastern.g15finalproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.List;

@Database(entities = {EmergencyContact.class}, version = 1)
public abstract class EmergencyContactDatabase extends RoomDatabase {

    public abstract EmergencyContactDao EmergencyContactDao();
}
