package de.unidisk.view.variables;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="projectViewConverter")
public class ProjectViewConverter  implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s.equals(ProjectViews.Results.toString()))
            return ProjectViews.Results;
        else if(s.equals(ProjectViews.Topics.toString()))
            return ProjectViews.Topics;
        else if(s.equals(ProjectViews.Visual.toString()))
            return ProjectViews.Visual;

        return ProjectViews.Default;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return o == null ? ProjectViews.Default.toString() : ((ProjectViews) o).toString();
    }
}
