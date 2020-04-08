package de.unidisk.solr.nlp.basics;

import de.unidisk.solr.nlp.datatype.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 12.03.17.
 */
public class GermanStemming {
    List<Rule> accentRules;
    List<Rule> pluralRules;

    public GermanStemming() {
        accentRules = new ArrayList<>();
        pluralRules = new ArrayList<>();
        removeAccent();
        removePlural();
    }

    private void removeAccent() {
        accentRules.add(new Rule("ä", "a"));
        accentRules.add(new Rule("ö", "o"));
        accentRules.add(new Rule("ü", "u"));
    }

    private void removePlural() {
        pluralRules.add(new Rule("(en)$", "", 5));
        pluralRules.add(new Rule("(se)$", "", 4));
        pluralRules.add(new Rule("(es)$", "", 4));
        pluralRules.add(new Rule("(er)$", "", 4));
        pluralRules.add(new Rule("n$", ""));
        pluralRules.add(new Rule("s$", ""));
        pluralRules.add(new Rule("r$", ""));
        pluralRules.add(new Rule("e$", ""));
    }

    public String stem(String word) {
        if (word.length() <= 3) {
            return word;
        }
        for (Rule accentRule : accentRules) {
            word = accentRule.transform(word);
        }
        for (Rule pluralRule : pluralRules) {
            String newWord = pluralRule.transform(word);
            if (newWord.length() < word.length()) {
                return newWord;
            }
        }
        return word;
    }
}
