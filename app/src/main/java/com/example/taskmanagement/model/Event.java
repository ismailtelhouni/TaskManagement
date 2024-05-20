package com.example.taskmanagement.model;

import java.util.Objects;

public class Event {

    private String id , title , description , lieu , category , status , image , startDate , endDate , email ;
    private boolean favourite;

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getStartDate() {
        return startDate;
    }

    public Event(String id, String title, String description, String lieu, String category, String status, String image, String startDate, String endDate , String email, boolean favourite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lieu = lieu;
        this.category = category;
        this.status = status;
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
        this.email = email;
        this.favourite = favourite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", titre='" + title + '\'' +
                ", description='" + description + '\'' +
                ", lieu='" + lieu + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(title, event.title) && Objects.equals(description, event.description) && Objects.equals(lieu, event.lieu) && Objects.equals(category, event.category) && Objects.equals(status, event.status) && Objects.equals(image, event.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, lieu, category, status, image);
    }

    public Event() {
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
