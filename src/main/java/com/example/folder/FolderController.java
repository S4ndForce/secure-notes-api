package com.example.folder;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public FolderResponse create(@RequestBody String name, Authentication auth) {
        return folderService.create(name, auth);
    }
    /*
    @GetMapping
    public List<FolderResponse> getMyFolders(Authentication auth) {
        return folderService.getMyFolders(auth);
    }
    */

    @GetMapping("/{id}")
    public FolderResponse get(@PathVariable Long id, Authentication auth) {

        return folderService.getById(id, auth);
    }

    @PatchMapping("/{id}")
    public FolderResponse update(@PathVariable Long id, @RequestBody String name, Authentication auth) {
        return folderService.update(id, name, auth);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {

        folderService.delete(id, auth);
    }

    @PostMapping("/{id}/restore")
    public void restore(@PathVariable Long id, Authentication auth) {
        folderService.restore(id, auth);
    }
}
