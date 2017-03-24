package de.unidisk.nlp.datatype;

import de.unidisk.crawler.datatype.AbstractStichwort;
import de.unidisk.crawler.datatype.StichwortModifier;

import java.util.List;

/**
 * Created by carl on 21.03.17.
 */
public class RegExpStichwort extends AbstractStichwort {
    public RegExpStichwort(String name) {
        super(name);
    }

    @Override
    public String buildExpression(List<StichwortModifier> modifiers) {
        StringBuilder resultString = new StringBuilder();
        if (modifiers.contains(StichwortModifier.START_OF_WORD)) {
            resultString.append("^[^a-zA-Z]*");
        } else {
            if (modifiers.contains(StichwortModifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        String resultName = getName();
        if (modifiers.contains(StichwortModifier.NOT_CASE_SENSITIVE)) {
            resultName = "[" + getName().substring(0, 1).toLowerCase() + getName().substring(0, 1).toUpperCase()
                    + "]" + getName().substring(1);
        }
        resultString.append(resultName);
        if (modifiers.contains(StichwortModifier.END_OF_WORD)) {
            resultString.append("[^a-zA-Z]*$");
        } else {
            if (modifiers.contains(StichwortModifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        return resultString.toString();
    }



    @Override
    public String getBegin() {
        return "/";
    }

    @Override
    public String getEnd() {
        return "/";
    }

    @Override
    public String getSeparator() {
        return "|";
    }
}
