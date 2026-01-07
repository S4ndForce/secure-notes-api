package com.example.folder;

public class FolderResponse {

    private Long id;
    private String name;
    private String userName;

    public FolderResponse(Long id, String name, String userName) {
        this.id = id;
        this.name = name;
        this.userName = userName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUserName() { return userName; }

    public static FolderResponse fromEntity(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getOwner().getEmail()
        );
    }
}
