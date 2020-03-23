package de.unidisk.contracts.repositories;

import de.unidisk.entities.hibernate.University;

import java.util.List;
import java.util.Optional;

public interface IUniversityRepository {
    List<University> getUniversities();

    /*
    Returns all university entities where the difference
    between the last crawl and now is greater than the
    given time period.
     */
    List<University> getUniversities(long timeSinceLastCrawl);
    Optional<University> getUniversity(int id);

    void setLastCrawlTime(int universityId, long timestamp);
}
