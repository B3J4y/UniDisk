package de.unidisk;

import de.unidisk.rest.TopicData;

public interface ITopic {
    /**
     * represents old query
     * $query = "INSERT INTO `" . $camp . "_" . $this->props->stichWort . "` (Stichwort, Variable) VALUE ('" . $stich . "', '" . $var . "')";
     * $this->query($query);ß
     *
     * @param data
     */
    void saveTopic(TopicData data);

    /**
     * represents old query
     * $query = "SELECT * FROM `" . $camp . "_" . $this->props->stichWort . "`";
     * return $this->querySelect($query, $camp . "_" . $this->props->stichWort);
     *
     * @return
     */
    java.util.List<TopicData> getTopics();
}
