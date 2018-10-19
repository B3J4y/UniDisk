package de.unidisk;

import de.unidisk.entities.KeyWordScore;
import de.unidisk.entities.TopicScore;

public interface IScores {
    /**
     * Was bedeutet group in dem Query? TODO
     * public function loadScoreStich($group, $camp) {
     * 		$query = "SELECT " . $group . ", SUM(SolrScore) as Score, Count(" . $group . ") as Count FROM `" . $camp . "_" . $this->props->scoreStich . "` GROUP BY " . $group;
     * 		return $this->querySelect($query, $camp . "_" . $this->props->scoreStich);
     *        }
     *
     *      TODO rewrite
     * @return
     */
    java.util.List<KeyWordScore> getKeyWordScores();


    /**
     *
     * 	public function loadScoreVar($group, $camp) {
     * 		$query = "SELECT " . $group . ", SUM(SolrScore) as Score, Count(" . $group . ") as Count FROM `" . $camp . "_" . $this->props->varMeta . "` GROUP BY " . $group;
     * 		return $this->querySelect($query, $camp . "_" . $this->props->varMeta);
     *        }
     *
     *        TODO rewrite
     * @return
     */
    java.util.List<TopicScore> getTopicScores();

}
