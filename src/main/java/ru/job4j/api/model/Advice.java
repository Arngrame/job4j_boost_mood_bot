package ru.job4j.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mb_advice")
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isGood;

    private String text;

    private String description;

    public Advice() {
        super();
    }

    public Advice(boolean good, String text, String description) {
        this.isGood = good;
        this.text = text;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}