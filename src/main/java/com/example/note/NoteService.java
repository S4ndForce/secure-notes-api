package com.example.note;

import com.example.exceptions.ForbiddenException;
import com.example.exceptions.NotFoundException;
import com.example.user.User;
import com.example.auth.CurrentUser;
import com.example.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final CurrentUser currentUser;

    public NoteService(NoteRepository noteRepository, CurrentUser currentUser) {
        this.noteRepository = noteRepository;
        this.currentUser = currentUser;
    }

    public NoteResponse create(String content, Authentication auth) {
        User user = currentUser.get(auth);
        Note note = new Note(content, user);
        noteRepository.save(note);
        return NoteResponse.fromEntity(note);
    }

    public NoteResponse getById(Long id, Authentication auth) {
        User user = currentUser.get(auth);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found"));


        if (!note.isOwnedBy(user)) {
            throw new ForbiddenException("Not your note");
        }

        return NoteResponse.fromEntity(note);
    }
}
