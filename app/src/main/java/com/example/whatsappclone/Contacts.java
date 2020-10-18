package com.example.whatsappclone;


public class Contacts {
     public String name,profilePhoto,description;

     public Contacts(){

     }

    public Contacts(String name, String profilePhoto, String description) {
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
