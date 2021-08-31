package de.unidisk.common.exceptions;

import de.unidisk.entities.hibernate.University;

import java.util.Arrays;
import java.util.List;

public class SeedData {


    public static List<University> getSeedUniversities(){
        return Arrays.asList(
                new University("Universität Potsdam",52.4010314,13.0097211,"https://www.uni-potsdam.de"),
                new University("TU Berlin",52.5125322,13.3247559,"https://www.tu-berlin.de"),
                new University("FH Potsdam", 52.4132004,13.0483612,"https://www.fh-potsdam.de"),
                new University("HPI", 52.4132004,13.0097211,"https://hpi.de/"),
                new University("TU München",48.1486926,11.5686501,"https://www.tum.de/"),
                new University("FH Aachen",50.7647135,6.0792746,"https://www.fh-aachen.de"),
                new University("Fernuni Hagen",51.3776306,7.4923537,"https://www.fernuni-hagen.de"),
                new University("Universität Köln",50.9281865,6.928763,"https://www.uni-koeln.de"),
                new University("LMU München",48.1510078,11.5798872,"https://www.lmu.de"),
                new University("RWTH Aachen",50.7776697,6.0779794,"https://www.rwth-aachen.de"),
                new University("Johann Wolfgang Göthe Universität Frankfurt",50.1270675,8.6655748,"https://www.uni-frankfurt.de")
        );
    }
}
