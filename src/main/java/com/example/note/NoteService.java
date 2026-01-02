package com.example.note;

import com.example.user.User;
import com.example.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public Note create(String content, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow();

        Note note = new Note(content, user);
        return noteRepository.save(note);
    }

    public Note getById(Long id, Authentication auth) {
        Note note = noteRepository.findById(id)
                .orElseThrow();

        if (!note.getOwner().getEmail().equals(auth.getName())) { //  <-- Ownership enforced HERE
            throw new RuntimeException("Not your note");
        }

        return note;
    }
}
