package de.unidisk.nlp.basics;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by carl on 08.03.17.
 */
public class EnhancedWithRegExp {
    protected Set<Modifier> modifiers;

    public EnhancedWithRegExp() {
        modifiers = new HashSet<>();
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public void removeModifier(Modifier modifier) {
        modifiers.remove(modifier);
    }

    /**
     * Builds a Regular Expression with several predefined standard modifiers
     */
    public String buildRegExp(String search) {
        StringBuilder resultString = new StringBuilder();
        if (modifiers.contains(Modifier.START_OF_WORD)) {
            resultString.append("^[^a-zA-Z]*");
        } else {
            if (modifiers.contains(Modifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        if (modifiers.contains(Modifier.NOT_CASE_SENSITIVE)) {
            search = "[" + search.substring(0, 1).toLowerCase() + search.substring(0, 1).toUpperCase() + "]" + search.substring(1);
        }
        resultString.append(search);
        if (modifiers.contains(Modifier.END_OF_WORD)) {
            resultString.append("[^a-zA-Z]*$");
        } else {
            if (modifiers.contains(Modifier.PART_OF_WORD)) {
                resultString.append("[a-zA-Z]*");
            }
        }
        return resultString.toString();
    }

    public enum Modifier {
        START_OF_WORD,
        NOT_CASE_SENSITIVE,
        END_OF_WORD,
        PART_OF_WORD
    }
}
