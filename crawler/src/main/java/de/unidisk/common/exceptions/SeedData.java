package de.unidisk.common.exceptions;

import de.unidisk.entities.hibernate.University;

import java.util.Arrays;
import java.util.List;

public class SeedData {


    public static List<University> getSeedUniversities(){
        return Arrays.asList(
                new University("Uni Tübingen",52.4010314,13.0097211,"https://www.uni-tuebingen.de"),
                new University("Uni Potsdam",52.4010314,13.0097211,"https://publishup.uni-potsdam.de"),
                new University("TU München",48.1486926,11.5686501,"https://www.tum.de/")

      );
    }
}
