package de.unidisk.nlp.datatype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by carl on 12.03.17.
 */
public class Rule {
    private String preRule;
    private String postRule;
    private int lengthCondition;

    public Rule(String preRule, String postRule) {
        this.preRule = preRule;
        this.postRule = postRule;
    }

    public Rule(String preRule, String postRule, int lengthCondition) {
        this.preRule = preRule;
        this.postRule = postRule;
        this.lengthCondition = lengthCondition;
    }

    public String transform(String transformable) {
        if (lengthCondition > 0 && transformable.length() < lengthCondition) {
            return transformable;
        }
        Matcher matcher = Pattern.compile(preRule).matcher(transformable);
        if (matcher.find()) {
            return matcher.replaceAll(postRule);
        }
        return transformable;
    }
}
