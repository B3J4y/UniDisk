package de.unidisk.common;

import de.unidisk.entities.hibernate.*;

import java.util.Arrays;
import java.util.List;

public class MockData {

    public static ApplicationState getMockState(){
        return new ApplicationState(
                getMockProjects(),
                getMockUniversities()
        );
    }

    public static List<University> getMockUniversities(){
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

    public static List<Project> getMockProjects(){
        final Project p1 = new Project("E-Learning VR", ProjectState.IDLE,
                Arrays.asList(
                    new Topic(
                        "Geräte",0, Arrays.asList(
                            new Keyword(
                                    "Tablet"
                            ),

                            new Keyword(
                                    "Smartphone"
                            ),
                            new Keyword("Laptop"),
                            new Keyword("IOT")
                            )
                        ),
                    new Topic(
                            "Software", 0, Arrays.asList(
                            new Keyword("Microsoft"),
                            new Keyword("O365"),
                            new Keyword("Moodle"),
                            new Keyword("Office")
                        )
                    )
                )
        );


        final Project p2 = new Project("E-Learning Mathe", ProjectState.FINISHED,Arrays.asList(
                new Topic("Themen",0, Arrays.asList(
                    new Keyword("Analysis")
                ))
        ));

        final Project p3 = new Project("Beispielprojekt", ProjectState.WAITING,Arrays.asList(
                new Topic(
                        "Geräte",0, Arrays.asList(
                        new Keyword(
                                "Tablet"
                        ),

                        new Keyword(
                                "Smartphone"
                        ),
                        new Keyword("Laptop"),
                        new Keyword("IOT")
                )
                ),
                new Topic(
                        "Software", 0, Arrays.asList(
                        new Keyword("Microsoft"),
                        new Keyword("O365"),
                        new Keyword("Moodle"),
                        new Keyword("Office")
                )
                )
        ));

        final Project p4 = new Project("Fehlerprojekt", ProjectState.ERROR,Arrays.asList(
                new Topic("E-Learning", 0, Arrays.asList(
                        new Keyword("digital"),
                        new Keyword("Digitalisierung")
                )),
                new Topic(
                        "Geräte",0, Arrays.asList(
                        new Keyword(
                                "Tablet"
                        ),

                        new Keyword(
                                "Smartphone"
                        ),
                        new Keyword("Laptop"),
                        new Keyword("IOT")
                )
                )
        ));
        p4.setProcessingError("Fehler bei der Verarbeitung.");




        return Arrays.asList(p1,p2,p3,p4);
    }
}