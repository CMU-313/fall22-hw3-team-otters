package com.sismics.docs.rest.resource;

import com.google.common.base.Strings;
import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.*;
import com.sismics.docs.core.dao.criteria.ReviewerCriteria;
import com.sismics.docs.core.dao.dto.ReviewerDto;
import com.sismics.docs.core.model.jpa.*;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * User REST resources.
 * 
 * @author jtremeaux
 */
@Path("/reviewer")
public class ReviewerResource extends BaseResource {
    /**
     * Creates a new scoring row for a reviewer.
     *
     * @api {put} /reviewer Registers a new reviewer for an applicant
     * @apiName PutReviewer
     * @apiGroup Reviewer
     * @apiParam {String{3..50}} name Reviewer Name
     * @apiParam {String{1..3}} skill_score Scoring of applicant's skills
     * @apiParam {String{1..3}} experience_score Scoring of applicant's experience
     * @apiParam {String{1}} hire Applicant's hireability
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) ValidationError Validation error
     * @apiError (server) PrivateKeyError Error while generating a private key
     * @apiError (server) UnknownError Unknown server error
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param name Reviewer's name
     * @return Response
     */
    @PUT
    public Response register(
        @FormParam("name") String name,
        @FormParam("skill_score") String skillScoreStr,
        @FormParam("experience_score") String experienceScoreStr,
        @FormParam("hire") String hireStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Validate the input data
        name = ValidationUtil.validateLength(name, "name", 1, 50);
        ValidationUtil.validateUsername(name, "name");
        
        // Create the user
        Reviewer rev = new Reviewer();
        rev.setName(name);

        if (Strings.isNullOrEmpty(skillScoreStr)) {
            rev.setSkillScore(0);
        } else {
            Integer skillScore = ValidationUtil.validateInteger(skillScoreStr, "skillScore");
            rev.setSkillScore(skillScore);
        }

        if (Strings.isNullOrEmpty(experienceScoreStr)) {
            rev.setExperienceScore(0);
        } else {
            Integer experienceScore = ValidationUtil.validateInteger(experienceScoreStr, "experienceScore");
            rev.setExperienceScore(experienceScore);
        }

        if (Strings.isNullOrEmpty(hireStr)) {
            rev.setHire(false);
        } else {
            Boolean hire = false;
            Integer hireInt = ValidationUtil.validateInteger(hireStr, "hire");
            if (hireInt != 0) {
                hire = true;
            }
            rev.setHire(hire);
        }

        // Create the user
        ReviewerDao revDao = new ReviewerDao();
        try {
            revDao.create(rev, principal.getId());
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Updates a reviewer's row.
     *
     * @api {post} /reviewer/:name Update a reviewer's row
     * @apiName PostReviewerName
     * @apiGroup Reviewer
     * @apiParam {String} name Name
     * @apiParam {String{1..3}} skill_score Scoring of applicant's skills
     * @apiParam {String{1..3}} experience_score Scoring of applicant's experience
     * @apiParam {String{1}} hire Applicant's hireability
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) ValidationError Validation error
     * @apiError (client) UserNotFound User not found
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param name Name
     * @return Response
     */
    @POST
    @Path("{name: [a-zA-Z0-9_@\\.]+}")
    public Response update(
        @PathParam("name") String name,
        @FormParam("skill_score") String skillScoreStr,
        @FormParam("experience_score") String experienceScoreStr,
        @FormParam("hire") String hireStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Check if the reviewer exists
        ReviewerDao revDao = new ReviewerDao();
        Reviewer rev = revDao.getActiveByName(name);
        if (rev == null) {
            throw new ClientException("UserNotFound", "The user does not exist");
        }

        // Update the user
        if (Strings.isNullOrEmpty(skillScoreStr)) {
            rev.setSkillScore(rev.getSkillScore());
        } else {
            int skillScore = ValidationUtil.validateInteger(skillScoreStr, "skillScore");
            rev.setSkillScore(skillScore);
        }
        if (Strings.isNullOrEmpty(experienceScoreStr)) {
            rev.setExperienceScore(rev.getExperienceScore());
        } else {
            int experienceScore = ValidationUtil.validateInteger(experienceScoreStr, "experienceScore");
            rev.setExperienceScore(experienceScore);
        }
        if (Strings.isNullOrEmpty(hireStr)) {
            rev.setHire(rev.getHire());
        } else {
            Integer hireInt = ValidationUtil.validateInteger(hireStr, "hire");
            Boolean hire = false;
            if (hireInt != 0) {
                hire = true;
            }
            rev.setHire(hire);
        }

        rev = revDao.update(rev, principal.getId());
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Deletes a reviewer's row.
     *
     * @api {delete} /reviewer/:name Delete a reviewer's row
     * @apiDescription All associated entities will be deleted as well.
     * @apiName DeleteReviewerName
     * @apiGroup Reviewer
     * @apiParam {String} name Name
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied or the user cannot be deleted
     * @apiError (client) UserNotFound The user does not exist
     * @apiError (client) UserUsedInRouteModel The user is used in a route model
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param name Name
     * @return Response
     */
    @DELETE
    @Path("{name: [a-zA-Z0-9_@\\.]+}")
    public Response delete(@PathParam("name") String name) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Cannot delete the guest user
        if (Constants.GUEST_USER_ID.equals(name)) {
            throw new ClientException("ForbiddenError", "The guest user cannot be deleted");
        }

        // Check that the reviewer exists
        ReviewerDao revDao = new ReviewerDao();
        Reviewer rev = revDao.getActiveByName(name);
        if (rev == null) {
            throw new ClientException("UserNotFound", "The user does not exist");
        }
        
        // Delete the reviewer
        revDao.delete(rev.getName(), principal.getId());
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Returns the reviewer's row.
     *
     * @api {get} /reviewer/:name Get a reviewer's row
     * @apiName GetUserUsername
     * @apiGroup User
     * @apiParam {String} name Name
     * @apiSuccess {String} name Name
     * @apiSuccess {Number} skill_score Scoring of applicant's skills
     * @apiSuccess {Number} experience_score Scoring of applicant's experience
     * @apiSuccess {Boolean} hire Applicant's hireability
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) UserNotFound The user does not exist
     * @apiPermission user
     * @apiVersion 1.5.0
     *
     * @param name Name
     * @return Response
     */
    @GET
    @Path("{name: [a-zA-Z0-9_@\\.]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response view(@PathParam("name") String name) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        ReviewerDao revDao = new ReviewerDao();
        Reviewer rev = revDao.getActiveByName(name);
        if (rev == null) {
            throw new ClientException("UserNotFound", "The user does not exist");
        }
        
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("name", rev.getName())
                .add("skill_score", rev.getSkillScore())
                .add("experience_score", rev.getExperienceScore())
                .add("hire", rev.getHire());
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Returns all reviews.
     *
     * @api {get} /reviewer/list Get review list
     * @apiName GetReviewerList
     * @apiGroup Reviewer
     * @apiParam {Number} sort_column Column index to sort on
     * @apiParam {Boolean} asc If true, sort in ascending order
     * @apiSuccess {Object[]} reviewers List of reviewers
     * @apiSuccess {String} reviewers.id ID
     * @apiSuccess {String} reviewers.name Name
     * @apiSuccess {Number} reviewers.skill_score Skill score
     * @apiSuccess {Number} reviewers.experience_score Experience score
     * @apiSuccess {Boolean} reviewers.hire Hireability
     * @apiError (client) ForbiddenError Access denied
     * @apiPermission user
     * @apiVersion 1.5.0
     *
     * @param sortColumn Sort index
     * @param asc If true, ascending sorting, else descending
     * @return Response
     */
    @GET
    @Path("list")
    public Response list(
            @QueryParam("sort_column") Integer sortColumn,
            @QueryParam("asc") Boolean asc) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        JsonArrayBuilder reviewers = Json.createArrayBuilder();
        SortCriteria sortCriteria = new SortCriteria(sortColumn, asc);
        
        ReviewerDao revDao = new ReviewerDao();
        List<ReviewerDto> revDtoList = revDao.findByCriteria(new ReviewerCriteria(), sortCriteria);
        for (ReviewerDto revDto : revDtoList) {
            reviewers.add(Json.createObjectBuilder()
                    .add("id", revDto.getId())
                    .add("name", revDto.getName())
                    .add("skill_score", revDto.getSkillScore())
                    .add("experience_score", revDto.getExperienceScore())
                    .add("hire", revDto.getHire()));
        }
        
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("reviewers", reviewers);
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Averages all reviews.
     *
     * @api {get} /reviewer/average Gets average of all reviews
     * @apiName GetReviewerAverage
     * @apiGroup Reviewer
     * @apiSuccess {String} average Average label
     * @apiSuccess {Number} skill_avg Average of all reviewers' skills scoring
     * @apiSuccess {Number} experience_avg Average of all reviewers' experience scoring
     * @apiSuccess {Boolean} hire Applicant's hireability based on all reviewers
     * @apiError (client) ForbiddenError Access denied
     * @apiPermission user
     * @apiVersion 1.5.0
     *
     * @return Response
     */
    @GET
    @Path("average")
    public Response average() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        JsonArrayBuilder reviewers = Json.createArrayBuilder();
        ReviewerDao revDao = new ReviewerDao();
        int skill_avg = revDao.getAverageSkillScore();
        int experience_avg = revDao.getAverageExperienceScore();
        int hire = revDao.getAverageHire(); // is this number out of 0-1 or 100?, how to do bools
        
        reviewers.add(Json.createObjectBuilder()
                .add("name", "Average")
                .add("skill_score", skill_avg)
                .add("experience_score", experience_avg)
                .add("hire", hire));
        
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("reviewers_avg", reviewers);
        return Response.ok().entity(response.build()).build();
    }

}