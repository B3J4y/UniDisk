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

        final Project p3 = new Project("Biologie", ProjectState.WAITING,Arrays.asList(
                new Topic(
                        "Zelle",0, Arrays.asList(
                        new Keyword(
                                "Ribosomen"
                        ),

                        new Keyword(
                                "Zellkern"
                        ),
                        new Keyword("Proteine"),
                        new Keyword("Bindung")
                )
                ),
                new Topic(
                        "Medizin", 0, Arrays.asList(
                        new Keyword("Ambulanz"),
                        new Keyword("Krankenhaus"),
                        new Keyword("Pflege"),
                        new Keyword("Covid")
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

        final Project p5 = new Project("Informatik", ProjectState.WAITING,Arrays.asList(
                new Topic(
                        "Machine Learning",0, Arrays.asList(
                        new Keyword(
                                "Neuronale Netzwerke"
                        ),

                        new Keyword(
                                "Computer Vision"
                        ),
                        new Keyword("Statistik"),
                        new Keyword("Topic labeling")
                )
                ),
                new Topic(
                        "Netzwerk", 0, Arrays.asList(
                        new Keyword("Knoten"),
                        new Keyword("P2P"),
                        new Keyword("Kryptowährung"),
                        new Keyword("Internet")
                    )
                ),
                new Topic(
                        "Software", 0, Arrays.asList(
                        new Keyword("Java"),
                        new Keyword("Python"),
                        new Keyword("Software Engineering")
                    )
                )
        ));



        return Arrays.asList(p1,p2,p3,p4,p5);
    }
}