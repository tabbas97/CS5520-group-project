package edu.northeastern.g15finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmergencyContactDao {

    @Query("SELECT * FROM emergencycontact")
    List<EmergencyContact> getAll();

    @Query("SELECT * FROM emergencycontact WHERE uid IN (:userIds)")
    List<EmergencyContact> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM emergencycontact WHERE name LIKE :name LIMIT 1")
    EmergencyContact findByName(String name);

    @Insert
    void insertAll(EmergencyContact... emergencyContacts);

    @Delete
    void delete(EmergencyContact emergencyContact);
}
