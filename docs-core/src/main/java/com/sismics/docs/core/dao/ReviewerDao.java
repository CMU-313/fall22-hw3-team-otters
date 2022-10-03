package com.sismics.docs.core.dao;

import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.dao.criteria.ReviewerCriteria;
import com.sismics.docs.core.dao.dto.ReviewerDto;
import com.sismics.docs.core.model.jpa.Reviewer;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.docs.core.util.jpa.QueryParam;
import com.sismics.docs.core.util.jpa.QueryUtil;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

/**
 * Reviewer DAO.
 */
public class ReviewerDao {

    /**
     * Authenticates an reviewer.
     * 
     * @param name Reviewer login
     * @return The authenticated user or null
     */
    public Reviewer authenticate(String name) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select u from Reviewer u where u.name = :name and u.deleteDate is null");
        q.setParameter("name", name);
        try {
            Reviewer rev = (Reviewer) q.getSingleResult();
            return rev;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Creates a new reviewer.
     * 
     * @param rev Reviewer to create
     * @param revId Reviewer ID
     * @return Reviewer ID
     * @throws Exception e
     */
    public String create(Reviewer rev, String revId) throws Exception {
        // Create the user UUID
        rev.setId(UUID.randomUUID().toString());
        
        // Checks for user unicity
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select u from Reviewer u where u.name = :name and u.deleteDate is null");
        q.setParameter("name", rev.getName());
        List<?> l = q.getResultList();
        if (l.size() > 0) {
            throw new Exception("AlreadyExistingUsername");
        }
        
        // Create the user
        em.persist(rev);
        
        // Create audit log
        AuditLogUtil.create(rev, AuditLogType.CREATE, revId);
        
        return rev.getId();
    }
    
    /**
     * Updates a reviewer.
     * 
     * @param rev Reviewer to update
     * @param revId Reviewer ID
     * @return Updated reviewer
     */
    public Reviewer update(Reviewer rev, String revId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Get the reviewer
        Query q = em.createQuery("select u from Reviewer u where u.id = :id and u.deleteDate is null");
        q.setParameter("id", rev.getId());
        Reviewer revDb = (Reviewer) q.getSingleResult();

        // Update the reviewer (except password)
        revDb.setSkillScore(rev.getSkillScore());
        revDb.setExperienceScore(rev.getExperienceScore());
        revDb.setHire(rev.getHire());

        // Create audit log
        AuditLogUtil.create(revDb, AuditLogType.UPDATE, revId);
        
        return rev;
    }

    /**
     * Gets a reviewer by its ID.
     * 
     * @param id Reviewer ID
     * @return Reviewer
     */
    public Reviewer getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(Reviewer.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Gets an active reviewer by its name.
     * 
     * @param name Reviewer's name
     * @return Reviewer
     */
    public Reviewer getActiveByUsername(String name) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select u from Reviewer u where u.name = :name and u.deleteDate is null");
            q.setParameter("name", name);
            return (Reviewer) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Gets an active reviewer by its name.
     * 
     * @param name Reviewer's name
     * @return Reviewer
     */
    public Reviewer getActiveByName(String name) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select u from Reviewer u where u.name = :name and u.deleteDate is null");
            q.setParameter("name", name);
            return (Reviewer) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Deletes a reviewer.
     * 
     * @param name Reviewer's name
     * @param revId Reviewer ID
     */
    public void delete(String name, String revId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
            
        // Get the reviewer
        Query q = em.createQuery("select u from Reviewer u where u.name = :name and u.deleteDate is null");
        q.setParameter("name", name);
        Reviewer revDb = (Reviewer) q.getSingleResult();
        
        // Delete the reviewer
        Date dateNow = new Date();
        revDb.setDeleteDate(dateNow);

        q.setParameter("revId", revDb.getId());
        q.executeUpdate();
        
        // Create audit log
        AuditLogUtil.create(revDb, AuditLogType.DELETE, revId);
    }

    /**
     * Returns the list of all reviewers.
     * 
     * @param criteria Search criteria
     * @param sortCriteria Sort criteria
     * @return List of reviewers
     */
    public List<ReviewerDto> findByCriteria(ReviewerCriteria criteria, SortCriteria sortCriteria) {
        Map<String, Object> parameterMap = new HashMap<>();
        List<String> criteriaList = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder("select u.REV_ID_C as c0, u.REV_NAME_C as c1, u.REV_SKILLS_C as c2, u.REV_EXPERIENCE_C as c3, u.REV_HIRE_C as c4");
        sb.append(" from T_REV u ");
        
        // Add search criterias
        if (criteria.getSearch() != null) {
            criteriaList.add("lower(u.REV_NAME_C) like lower(:search)");
            parameterMap.put("search", "%" + criteria.getSearch() + "%");
        }
        if (criteria.getUserId() != null) {
            criteriaList.add("u.REV_ID_C = :userId");
            parameterMap.put("userId", criteria.getUserId());
        }
        if (criteria.getName() != null) {
            criteriaList.add("u.REV_NAME_C = :name");
            parameterMap.put("name", criteria.getName());
        }
        
        criteriaList.add("u.USE_DELETEDATE_D is null");
        
        if (!criteriaList.isEmpty()) {
            sb.append(" where ");
            sb.append(Joiner.on(" and ").join(criteriaList));
        }
        
        // Perform the search
        QueryParam queryParam = QueryUtil.getSortedQueryParam(new QueryParam(sb.toString(), parameterMap), sortCriteria);
        @SuppressWarnings("unchecked")
        List<Object[]> l = QueryUtil.getNativeQuery(queryParam).getResultList();
        
        // Assemble results
        List<ReviewerDto> revDtoList = new ArrayList<>();
        for (Object[] o : l) {
            int i = 0;
            ReviewerDto revDto = new ReviewerDto();
            revDto.setId((String) o[i++]);
            revDto.setName((String) o[i++]);
            revDto.setSkillScore(((Number) o[i++]).intValue());
            revDto.setExperienceScore(((Number) o[i++]).intValue());
            revDto.setHire(((Boolean) o[i++]).booleanValue());
            revDtoList.add(revDto);
        }
        return revDtoList;
    }

    /**
     * Returns the number of active reviewers.
     *
     * @return Number of active reviewers
     */
    public long getActiveReviewerCount() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query query = em.createNativeQuery("select count(u.REV_ID_C) from T_REV u where u.REV_DELETEDATE_D is null");
        DateTime fromDate = DateTime.now().minusMonths(1).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime toDate = fromDate.plusMonths(1);
        query.setParameter("fromDate", fromDate.toDate());
        query.setParameter("toDate", toDate.toDate());
        return ((Number) query.getSingleResult()).longValue();
    }
}
