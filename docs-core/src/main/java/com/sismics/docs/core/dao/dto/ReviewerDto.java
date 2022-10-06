package com.sismics.docs.core.dao.dto;

import com.google.common.base.MoreObjects;

/**
 * Reviewer DTO.
 */
public class ReviewerDto {
    /**
     * Reviewer ID.
     */
    private String id;
    
    /**
     * Name.
     */
    private String name;

    /**
     * Skills score.
     */
    private Integer skillScore;

    /**
     * Experience score.
     */
    private Integer experienceScore;

    /**
     * Hireable.
     */
    private Integer hire;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(Integer skillScore) {
        this.skillScore = skillScore;
    }

    public Integer getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(Integer experienceScore) {
        this.experienceScore = experienceScore;
    }

    public Integer getHire() {
        return hire;
    }

    public void setHire(Integer hire) {
        this.hire = hire;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", name)
                .toString();
    }
}
