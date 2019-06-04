package de.unidisk.rest;

import de.unidisk.IKeyword;
import de.unidisk.IOverview;
import de.unidisk.IScores;
import de.unidisk.ITopic;
import de.unidisk.entities.hibernate.KeyWordScore;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.TopicScore;

import javax.ws.rs.*;
import java.util.List;

@Path("/view")
public class RestView implements IKeyword, IOverview, IScores, ITopic {

    @Path("/keywords/{keyword}/projects/{projectName}")
    @POST
    @Override
    public void deleteKeyword(@PathParam("keyword") String keyword, Project project) {
        throw new NotImplementedError();
    }


    @Path("/overview")
    @GET
    @Override
    public List<Project> getOverview() {
        throw new NotImplementedError();
    }

    @Path("/project")
    @PUT
    @Override
    public void createCampaign(Project project) {
        throw new NotImplementedError();
    }

    /**
     * Params still unclear
     * @return
     */
    @Path("/keywords/scores")
    @GET
    @Override
    public List<KeyWordScore> getKeyWordScores() {
        throw new NotImplementedError();
    }

    /**
     * Params still unclear
     * @returns
     */
    @GET
    @Path("/topics/scores")
    @Override
    public List<TopicScore> getTopicScores() {
        throw new NotImplementedError();
    }

    /**
     *
     * @param data
     */
    @PUT
    @Path("/topics")
    @Override
    public void saveTopic(TopicData data) {
        throw new NotImplementedError();
    }

    /**
     *
     * @return
     */
    @GET
    @Path("/topics")
    @Override
    public List<TopicData> getTopics() {
        throw new NotImplementedError();
    }
}
