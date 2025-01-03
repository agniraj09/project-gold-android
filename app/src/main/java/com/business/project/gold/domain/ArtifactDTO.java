package com.business.project.gold.domain;

public class ArtifactDTO {

    private Long artifactId;
    private String artifact;

    public Long getArtifactId() {
        return artifactId;
    }

    public ArtifactDTO setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public String getArtifact() {
        return artifact;
    }

    public ArtifactDTO setArtifact(String artifact) {
        this.artifact = artifact;
        return this;
    }
}
