package com.example.taskmanagement.model;

import com.google.firebase.Timestamp;
import java.util.Objects;

public class Note {

    private String id , title , description , password  , stringDate , stringTime;
    private boolean follow ;
    private Timestamp date;

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public String getStringTime() {
        return stringTime;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", password='" + password + '\'' +
                ", date=" + date +
                '}';
    }

    public Note(String id, String title, String description, String password, String stringDate, String stringTime, boolean follow, Timestamp date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.password = password;
        this.follow = follow;
        this.date = date;
        this.stringDate = stringDate;
        this.stringTime = stringTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) && Objects.equals(title, note.title) && Objects.equals(description, note.description) && Objects.equals(password, note.password) && Objects.equals(date, note.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, password, date);
    }

    public Note() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
