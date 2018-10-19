package de.unidisk;

import de.unidisk.entities.Project;

public interface IOverview {
    /**
     * represents old query
     * $query = "SELECT * from " . $this->props->overview;
     * return $this->querySelect($query, $this->props->overview);
     *
     * @return
     */
    java.util.List<Project> getOverview();

    /**
     * represents old query
     * 	public function newCampaign($name) {
     * 		$query= "INSERT INTO " . $this->props->overview . "(Name, Status) VALUE ('" . $name . "', 0)";
     * 		$this->query($query);
     *        }
     *
     * @param project
     */
    void createCampaign(Project project);
}
