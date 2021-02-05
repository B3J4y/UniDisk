package de.unidisk.crawler.rest.dto.keyword;

public class CreateKeywordDto {

    String topicId;
    String name;

    public  CreateKeywordDto(){}


    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
