package com.example.folder;

import com.example.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "folders") // Prevents naming conflicts in db
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User owner;

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
}

