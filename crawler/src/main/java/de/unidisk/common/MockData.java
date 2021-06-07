package de.unidisk.common;

import de.unidisk.common.exceptions.SeedData;
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
        return SeedData.getSeedUniversities();
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