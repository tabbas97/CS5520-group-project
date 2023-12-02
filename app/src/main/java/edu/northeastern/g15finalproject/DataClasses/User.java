package edu.northeastern.g15finalproject.DataClasses;

import java.util.List;

public class User {
    public final String userName;
    public final String fullName;

    // We should probably use a hash of the password instead of the password itself
    public final String password;

    public final List<String> friendIds;

    public final List<String> emergencyContacts;

    public final String dateOfBirth;

    public User(String userName, String fullName, String password, List<String> friendIds, List<String> emergencyContacts, String dateOfBirth) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.friendIds = friendIds;
        this.emergencyContacts = emergencyContacts;
        this.dateOfBirth = dateOfBirth;
    }
}
