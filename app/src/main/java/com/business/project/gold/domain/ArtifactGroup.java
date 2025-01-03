package com.business.project.gold.domain;

import java.util.List;

public class ArtifactGroup {

    private String artifactGroup;
    private List<ArtifactDTO> artifacts;

    public String getArtifactGroup() {
        return artifactGroup;
    }

    public ArtifactGroup setArtifactGroup(String artifactGroup) {
        this.artifactGroup = artifactGroup;
        return this;
    }

    public List<ArtifactDTO> getArtifacts() {
        return artifacts;
    }

    public ArtifactGroup setArtifacts(List<ArtifactDTO> artifacts) {
        this.artifacts = artifacts;
        return this;
    }
}
