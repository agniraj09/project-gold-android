package com.business.project.gold.domain;

public class UserDetails {
    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String fullname;

    private String gender;

    private String role;

    public Long getId() {
        return id;
    }

    public UserDetails setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserDetails setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserDetails setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserDetails setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public UserDetails setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getRole() {
        return role;
    }

    public UserDetails setRole(String role) {
        this.role = role;
        return this;
    }

    public String getFullname() {
        return fullname;
    }

    public UserDetails setFullname(String fullname) {
        this.fullname = fullname;
        return this;
    }
}
