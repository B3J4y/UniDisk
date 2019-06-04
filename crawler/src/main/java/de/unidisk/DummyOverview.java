package de.unidisk;

import de.unidisk.entities.hibernate.Project;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;



public class DummyOverview implements IOverview {

    private PodamFactory factory = new PodamFactoryImpl();

    @Override
    public List<Project> getOverview() {
        ArrayList<Project> result = new ArrayList<>();
        for (int i = 0; i< 20;i++) {
            result.add(factory.manufacturePojo(Project.class));
        }
        return result;
    }

    @Override
    public void createCampaign(Project project) {
        System.out.println("habe natürlich etwas angelegt angelegt");
    }
}
