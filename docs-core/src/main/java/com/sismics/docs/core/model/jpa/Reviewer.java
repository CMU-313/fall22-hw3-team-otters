package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Reviewer entity.
 */
@Entity
@Table(name = "T_REV")
public class Reviewer implements Loggable {
    /**
     * Reviewer ID.
     */
    @Id
    @Column(name = "REV_ID_C", length = 36)
    private String id;

    /**
     * Reviewer's name.
     */
    @Column(name = "REV_NAME_C", nullable = false, length = 50)
    private String name;

    /**
     * Skills score.
     */
    @Column(name = "REV_SKILLS_N")
    private Integer skillScore;

    /**
     * Experience score.
     */
    @Column(name = "REV_EXPERIENCE_N")
    private Integer experienceScore;

    /**
     * Hireable.
     */
    @Column(name = "REV_HIRE_N")
    private Boolean hire;

    /**
     * Deletion date.
     */
    @Column(name = "REV_DELETEDATE_D")
    private Date deleteDate;

    public String getId() {
        return id;
    }

    public Reviewer setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Reviewer setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getSkillScore() {
        return skillScore;
    }

    public Reviewer setSkillScore(Integer skillScore) {
        this.skillScore = skillScore;
        return this;
    }

    public Integer getExperienceScore() {
        return experienceScore;
    }

    public Reviewer setExperienceScore(Integer experienceScore) {
        this.experienceScore = experienceScore;
        return this;
    }

    public Boolean getHire() {
        return hire;
    }

    public Reviewer setHire(Boolean hire) {
        this.hire = hire;
        return this;
    }

    @Override
    public Date getDeleteDate() {
        return deleteDate;
    }

    public Reviewer setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("reviewerId", id)
                .add("username", name)
                .toString();
    }

    @Override
    public String toMessage() {
        return name;
    }
}