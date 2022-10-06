package com.sismics.docs.core.dao.criteria;

/**
 * Reviewer criteria.
 */
public class ReviewerCriteria {
    /**
     * Search query.
     */
    private String search;

    /**
     * User ID.
     */
    private String userId;

    /**
     * Name.
     */
    private String name;

    public String getSearch() {
        return search;
    }

    public ReviewerCriteria setSearch(String search) {
        this.search = search;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ReviewerCriteria setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ReviewerCriteria setName(String name) {
        this.name = name;
        return this;
    }
}
