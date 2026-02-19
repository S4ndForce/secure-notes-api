package com.example;

import com.example.folder.Folder;
import com.example.folder.FolderRepository;
import com.example.note.Note;
import com.example.note.NoteRepository;
import com.example.shared.SharedAction;
import com.example.shared.SharedLink;
import com.example.shared.SharedLinkRepository;
import com.example.user.Role;
import com.example.user.User;
import com.example.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SharedLinkIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    SharedLinkRepository sharedLinkRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    User owner;
    Folder folder;
    Note note;
    String token;

    @BeforeEach
    void setup() {
        owner = userRepository.save(
                new User("owner@test.com", passwordEncoder.encode("password"), Role.USER)
        );

        folder = folderRepository.save(new Folder("Default", owner));

        note = noteRepository.save(new Note("Secret", owner, folder));

        token = UUID.randomUUID().toString();

        // direct saving opposed to sharing a note
        sharedLinkRepository.save(new SharedLink(
                token,
                note, // <--- connected through HERE
                owner,
                Set.of(SharedAction.READ),
                Instant.now().plusSeconds(3600)
        ));
    }

    @Test
    void unauthenticatedUserCanAccessValidSharedNote() throws Exception{
        mockMvc.perform(get("/shared/{token}", token))
                .andExpect(status().isOk());
    }
    @Test
    void expiredTokenIsRejected() throws Exception {
        String expiredToken = UUID.randomUUID().toString();
        sharedLinkRepository.save(new SharedLink(
                expiredToken,
                note,
                owner,
                Set.of(SharedAction.READ),
                Instant.now().minusSeconds(3600) // already expired
        ));

        mockMvc.perform(get("/shared/{token}", expiredToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void revokedTokenIsRejected() throws Exception {
        String revokedToken = UUID.randomUUID().toString();
        SharedLink link = sharedLinkRepository.save(new SharedLink(
                revokedToken,
                note,
                owner,
                Set.of(SharedAction.READ),
                Instant.now().plusSeconds(3600)
        ));
        link.revoke(Instant.now());

        mockMvc.perform(get("/shared/{token}", revokedToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonExistentTokenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/shared/bad-token-xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void readOnlyTokenCannotUpdate() throws Exception {
        mockMvc.perform(patch("/shared/{token}", token)
                        .contentType("application/json")
                        .content("""
                        { "content": "hacked" }
                    """))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTokenCanUpdate() throws Exception {
        String updateToken = UUID.randomUUID().toString();
        sharedLinkRepository.save(new SharedLink(
                updateToken,
                note,
                owner,
                Set.of(SharedAction.READ, SharedAction.UPDATE),
                Instant.now().plusSeconds(3600)
        ));

        mockMvc.perform(patch("/shared/{token}", updateToken)
                        .contentType("application/json")
                        .content("""
                        { "content": "updated content" }
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/shared/{token}", updateToken))
                .andExpect(jsonPath("$.content").value("updated content"));

    }

    @Test
    void tokenForSoftDeletedNoteIsRejected() throws Exception {
        note.setDeletedAt(Instant.now());
        noteRepository.save(note);

        mockMvc.perform(get("/shared/{token}", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenForNoteInDeletedFolderIsRejected() throws Exception {
        folder.setDeletedAt(Instant.now());
        folderRepository.save(folder);

        mockMvc.perform(get("/shared/{token}", token))
                .andExpect(status().isForbidden());
    }


}