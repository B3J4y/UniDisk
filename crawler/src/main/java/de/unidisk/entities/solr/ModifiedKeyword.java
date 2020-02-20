package de.unidisk.entities.solr;

import de.unidisk.common.StichwortModifier;

import java.util.List;

public class ModifiedKeyword {
    private String keyword;
    private List<StichwortModifier> modifier;

    public ModifiedKeyword(String keyword, List<StichwortModifier> modifier) {
        this.keyword = keyword;
        this.modifier = modifier;
    }

    public List<StichwortModifier> getModifier() {
        return modifier;
    }

    public void setModifier(List<StichwortModifier> modifier) {
        this.modifier = modifier;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
