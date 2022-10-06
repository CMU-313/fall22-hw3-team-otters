package com.sismics.docs.rest;

import org.junit.Assert;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.Locale;

/**
 * Exhaustive test of the reviewer resource.
 */
public class TestReviewerResource extends BaseJerseyTest {
    /**
     * Test the reviewer resource.
     */
    @Test
    public void testReviewerResource() {
        // Check anonymous reviewer information
        JsonObject json = target().path("/reviewer").request()
                .acceptLanguage(Locale.US)
                .get(JsonObject.class);
        
        // Create alice reviewer
        clientUtil.createReviewer("alice");
        
        // List all reviewers
        json = target().path("/reviewer/list")
                .queryParam("sort_column", 2)
                .queryParam("asc", false)
                .request()
                .get(JsonObject.class);
        JsonArray reviewers = json.getJsonArray("reviewers");
        Assert.assertTrue(reviewers.size() > 0);
        JsonObject rev = reviewers.getJsonObject(0);
        Assert.assertNotNull(rev.getString("id"));
        Assert.assertNotNull(rev.getString("name"));
        Assert.assertNotNull(rev.getJsonNumber("skill_score"));
        Assert.assertNotNull(rev.getJsonNumber("experience_score"));
        Assert.assertNotNull(rev.getJsonNumber("hire"));

        // Create a reviewer bob OK
        Form form = new Form()
                .param("name", "bob")
                .param("skill_score", "3")
                .param("experience_score", "3")
                .param("hire", "1");
        target().path("/reviewer").request()
                .put(Entity.form(form), JsonObject.class);
        
        // Check alice reviewer information
        json = target().path("/reviewer/alice").request()
                .get(JsonObject.class);
        Assert.assertEquals(0, json.getJsonNumber("skill_score").intValue());
        Assert.assertEquals(0, json.getJsonNumber("experience_score").intValue());
        Assert.assertEquals(-1, json.getJsonNumber("hire").intValue());
        
        // Check bob reviewer information
        json = target().path("/reviewer/bob").request()
                .get(JsonObject.class);
                Assert.assertEquals(3, json.getJsonNumber("skill_score").intValue());
                Assert.assertEquals(3, json.getJsonNumber("experience_score").intValue());
                Assert.assertEquals(1, json.getJsonNumber("hire").intValue());
        
        // Check the average of reviews
        json = target().path("/reviewer/average").request()
                .get(JsonObject.class);
                Assert.assertEquals("1.5", json.getJsonNumber("skill_score").intValue());
                Assert.assertEquals("1.5", json.getJsonNumber("experience_score").intValue());
                Assert.assertEquals("0", json.getJsonNumber("hire").intValue());

        // Delete reviewer alice
        target().path("/reviewer/alice").request()
                .delete();
        
        // Create a reviewer sandy OK
        form = new Form()
        .param("name", "sandy")
        .param("skill_score", "5")
        .param("experience_score", "5")
        .param("hire", "1");
        target().path("/reviewer").request()
        .put(Entity.form(form), JsonObject.class);

        // Check the average of reviews
        json = target().path("/reviewer/average").request()
                .get(JsonObject.class);
                Assert.assertEquals("4", json.getJsonNumber("skill_score").intValue());
                Assert.assertEquals("4", json.getJsonNumber("experience_score").intValue());
                Assert.assertEquals("1", json.getJsonNumber("hire").intValue());

    }
}