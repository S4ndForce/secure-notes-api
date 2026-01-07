package com.example.folder;

import com.example.auth.CurrentUser;
import com.example.exceptions.ForbiddenException;
import com.example.exceptions.NotFoundException;
import com.example.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final CurrentUser currentUser;

    public FolderService(FolderRepository folderRepository, CurrentUser currentUser) {
        this.folderRepository = folderRepository;
        this.currentUser = currentUser;
    }

    public FolderResponse create(String name, Authentication auth) {
        User user = currentUser.get(auth);
        Folder folder = new Folder(name, user);
        folderRepository.save(folder);
        return FolderResponse.fromEntity(folder);
    }

    public List<FolderResponse> getMyFolders(Authentication auth) {
        User user = currentUser.get(auth);
        return folderRepository.findByOwner(user)
                .stream()
                .map(FolderResponse::fromEntity)
                .toList();
    }

    public FolderResponse getById(Long id, Authentication auth) {
        User user = currentUser.get(auth);
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        if (!folder.isOwnedBy(user)) {
            throw new ForbiddenException("Not your folder");
        }

        return FolderResponse.fromEntity(folder);
    }

    public FolderResponse update(Long id, String name, Authentication auth) {
        User user = currentUser.get(auth);
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        if (!folder.isOwnedBy(user)) {
            throw new ForbiddenException("Not your folder");
        }

        folder.setName(name);
        folderRepository.save(folder);
        return FolderResponse.fromEntity(folder);
    }

    public void delete(Long id, Authentication auth) {
        User user = currentUser.get(auth);
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        if (!folder.isOwnedBy(user)) {
            throw new ForbiddenException("Not your folder");
        }

        folderRepository.deleteById(id);
    }
}
