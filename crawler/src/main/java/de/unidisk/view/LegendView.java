package de.unidisk.view;


import de.unidisk.common.ProjectStateMapper;
import de.unidisk.entities.hibernate.ProjectState;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = LegendView.BEAN_NAME)
@ViewScoped
public class LegendView {
    public static final String BEAN_NAME = "legendView";

    private final java.util.List<LegendItem> symbols = Arrays.stream(ProjectState.values()).map(
            (s) -> new LegendItem(ProjectStateMapper.mapToFriendlyName(s), ProjectStateMapper.getStateIconUrl(s))
    ).collect(Collectors.toList());

    public LegendView() {
    }

    public List<LegendItem> getSymbols() {
        return symbols;
    }

    public class LegendItem{
        private String icon;
        private String name;

        public LegendItem(String name,String icon) {
            this.icon = icon;
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }
    }
}
