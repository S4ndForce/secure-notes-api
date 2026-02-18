package com.example.folder;

import com.example.base.BaseEntity;
import com.example.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "folders") // Prevents naming conflicts in db
public class Folder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User owner;

    private Instant deletedAt;

    protected Folder() {}

    public Folder(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public User getOwner() { return owner; }

    public void setName(String name) { this.name = name; }

    public boolean isOwnedBy(User user) {
        return owner != null && owner.getId().equals(user.getId());
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

