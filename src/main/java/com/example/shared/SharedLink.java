package com.example.shared;

import com.example.note.Note;
import jakarta.persistence.*;

@Entity
public class SharedLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(optional = false)
    private Note note;

    public SharedLink() {}

    public SharedLink(String token, Note note) {
        this.token = token;
        this.note = note;
    }

    public String getToken() {
        return token;
    }

    public Note getNote() {
        return note;
    }
}
