package com.example.folder;

import com.example.auth.CurrentUser;
import com.example.auth.OwnerAction;
import com.example.auth.OwnerAuthorization;
import com.example.exceptions.NotFoundException;
import com.example.note.Note;
import com.example.note.NoteRepository;
import com.example.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final CurrentUser currentUser;
    private final NoteRepository noteRepository;
    private final OwnerAuthorization ownedAuth;

    private Specification<Folder> ownedActiveFolder(Long id, User user) {
        return Specification
                .allOf(FolderSpecs.withId(id))
                .and(FolderSpecs.belongsTo(user))
                .and(FolderSpecs.notDeleted());
    }

    private Specification<Folder> ownedDeletedFolder(Long id, User user) {
        return Specification.
                allOf(FolderSpecs.withId(id))
                .and(FolderSpecs.belongsTo(user));
    }

    public FolderService(FolderRepository folderRepository, CurrentUser currentUser, NoteRepository noteRepository, OwnerAuthorization ownedAuth) {
        this.folderRepository = folderRepository;
        this.currentUser = currentUser;
        this.noteRepository = noteRepository;
        this.ownedAuth = ownedAuth;
    }

    public FolderResponse create(String name, Authentication auth) {
        User user = currentUser.get(auth);
        ownedAuth.authorize(OwnerAction.CREATE);
        Folder folder = new Folder(name, user);
        Instant now = Instant.now();
        folder.setCreatedAt(now);
        folder.setUpdatedAt(now);
        folderRepository.save(folder);
        return FolderResponse.fromEntity(folder);
    }

    // Temporary comment: method returns unspecified folders.
    /*
    public List<FolderResponse> getMyFolders(Authentication auth) {
        User user = currentUser.get(auth);
        ownedAuth.authorize(OwnerAction.READ);
        return folderRepository.findByOwner(user)
                .stream()
                .map(FolderResponse::fromEntity)
                .toList();
    }

    */

    public FolderResponse getById(Long id, Authentication auth) {
        User user = currentUser.get(auth);

        Specification<Folder> spec = ownedActiveFolder(id, user);

        Folder folder = folderRepository.findOne(spec)
                .orElseThrow(() -> new NotFoundException("Folder not found"));
        ownedAuth.authorize(OwnerAction.READ);
        return FolderResponse.fromEntity(folder);
    }

    public FolderResponse update(Long id, String name, Authentication auth) {
        User user = currentUser.get(auth);

        Specification<Folder> spec = ownedActiveFolder(id, user);

        Folder folder = folderRepository.findOne(spec)
                .orElseThrow(() -> new NotFoundException("Folder not found"));
        ownedAuth.authorize(OwnerAction.UPDATE);
        folder.setName(name);
        folderRepository.save(folder);
        return FolderResponse.fromEntity(folder);
    }


    public void delete(Long id, Authentication auth) {
        User user = currentUser.get(auth);
        Specification<Folder> spec = ownedActiveFolder(id, user);
        ownedAuth.authorize(OwnerAction.DELETE);
        Folder folder = folderRepository.findOne(spec)
                .orElseThrow(() -> new NotFoundException("Folder not found"));
        folder.setDeletedAt(Instant.now());
        folderRepository.save(folder);


    }

    public void restore(Long id, Authentication auth) {
        User user = currentUser.get(auth);

        Specification<Folder> spec = ownedDeletedFolder(id, user);

        Folder folder = folderRepository.findOne(spec)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        ownedAuth.authorize(OwnerAction.UPDATE);
        folder.setDeletedAt(null);
        folder.setUpdatedAt(Instant.now());
        folderRepository.save(folder);
    }

}
