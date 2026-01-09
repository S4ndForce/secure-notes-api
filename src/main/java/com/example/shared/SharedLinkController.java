package com.example.shared;

import com.example.note.NoteResponse;
import com.example.note.Note;
import com.example.exceptions.NotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shared")
public class SharedLinkController {

    private final SharedLinkRepository sharedLinkRepository;

    public SharedLinkController(SharedLinkRepository sharedLinkRepository) {
        this.sharedLinkRepository = sharedLinkRepository;
    }

    @GetMapping("/{token}")
    public NoteResponse getShared(@PathVariable String token) {
        SharedLink link = sharedLinkRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid link"));

        Note note = link.getNote();
        return NoteResponse.fromEntity(note);
    }
}
