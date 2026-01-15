package com.example.note;

import com.example.folder.Folder;
import com.example.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private String content;

    @ManyToOne(optional = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Folder folder;

    protected Note() {}

    public Note(String content, User owner, Folder folder) {
        this.content = content;
        this.owner = owner;
        this.folder = folder;
    }

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PRIVATE;


    public Note(String content, User owner) {
        this.content = content;
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public boolean isOwnedBy(User user) {
        return this.owner.equals(user);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
