package de.unidisk.common;

import de.unidisk.entities.hibernate.*;

import java.util.ArrayList;
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
                new University("TU Berlin",52.5125322,13.3247559,"https://www.tu-berlin.de/menue/home"),
                new University("FH Potsdam", 52.4132004,13.0483612,"https://www.fh-potsdam.de")
        );
    }

    public static List<Project> getMockProjects(){
        final Project p1 = new Project("E-Learning VR", ProjectState.RUNNING,
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
                            "Software", 1, Arrays.asList(
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
                new Topic("Aufwendiges Thema",0,Arrays.asList(
                        new Keyword("schweres Stichwort"),
                        new Keyword("langes Stichwort")
                ))
        ));

        final Project p4 = new Project("Fehlerprojekt", ProjectState.ERROR,Arrays.asList(
                new Topic("E-Learning", 0, Arrays.asList(
                        new Keyword("digital"),
                        new Keyword("Digitalisierung")
                ))
        ));


        final Project p5 = new Project("E-Learning VR", ProjectState.IDLE,
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
                                "Software", 1, Arrays.asList(
                                new Keyword("Microsoft"),
                                new Keyword("O365"),
                                new Keyword("Moodle"),
                                new Keyword("Office")
                        )
                        )
                )
        );

        return Arrays.asList(p1,p2,p3,p4,p5);
    }
}