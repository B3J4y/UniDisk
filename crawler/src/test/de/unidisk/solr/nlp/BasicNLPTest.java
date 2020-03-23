package de.unidisk.solr.nlp;

import de.unidisk.crawler.datatype.Stichwort;
import de.unidisk.solr.nlp.basics.GermanStemming;
import de.unidisk.solr.nlp.datatype.RegExpStichwort;
import de.unidisk.solr.nlp.datatype.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class BasicNLPTest {

    @Test
    public void testRules() throws Exception {
        Rule rule = new Rule("en", "");
        assertEquals("kauf", rule.transform("kaufen"));
        rule = new Rule("ä", "a");
        assertEquals("Hauser", rule.transform("Häuser"));
        assertEquals("kauf", new Rule("er", "").transform(new Rule("ä", "a").transform("käufer")));
        rule = new Rule("(en)$", "", 5);
        assertEquals("lauf", rule.transform("laufen"));
        assertEquals("chen", rule.transform("chen"));
        rule = new Rule("ö", "o");
        assertEquals("Käufer", rule.transform("Käufer"));
    }

    @Test
    public void testStemGerman() throws Exception {
        GermanStemming germanStemming = new GermanStemming();
        assertEquals("Kauf", germanStemming.stem("Käufer"));
        assertEquals("Handel", germanStemming.stem("Handeln"));
        assertEquals("sei", germanStemming.stem("sein"));
        assertEquals("Dat", germanStemming.stem("Daten"));
    }
}