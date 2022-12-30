package org.example.Entities;

public class Profile {
    private int profile_id;
    private String profile_name;
    private int age;
    private String image;
    private String description;

    public Profile(int profile_id, String profile_name, int age, String image, String description) {
        this.profile_id = profile_id;
        this.profile_name = profile_name;
        this.age = age;
        this.image = image;
        this.description = description;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public int getAge() {
        return age;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
