package com.business.project.gold.domain;

public class ArtifactDetailsDTO {
    private int id;
    private String artifactGroup;
    private String artifact;
    private String status;

    public ArtifactDetailsDTO(int id, String artifactGroup, String artifact, String status) {
        this.id = id;
        this.artifactGroup = artifactGroup;
        this.artifact = artifact;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public ArtifactDetailsDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getArtifactGroup() {
        return artifactGroup;
    }

    public ArtifactDetailsDTO setArtifactGroup(String artifactGroup) {
        this.artifactGroup = artifactGroup;
        return this;
    }

    public String getArtifact() {
        return artifact;
    }

    public ArtifactDetailsDTO setArtifact(String artifact) {
        this.artifact = artifact;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ArtifactDetailsDTO setStatus(String status) {
        this.status = status;
        return this;
    }
}
