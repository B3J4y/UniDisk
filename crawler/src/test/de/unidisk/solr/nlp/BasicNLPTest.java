package de.unidisk.solr.nlp;

import de.unidisk.common.StichwortModifier;
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
    public void testEnhancedWithRegExp() throws Exception {
        List<StichwortModifier> modifiers = new ArrayList<>();
        Stichwort regexStichwort = new RegExpStichwort("Test");
        Matcher matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("Test");
        assertTrue(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("Tester");
        assertTrue(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("tester");
        assertFalse(matcher.find());

        modifiers.add(StichwortModifier.NOT_CASE_SENSITIVE);
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("tester");
        assertTrue(matcher.find());

        modifiers.add(StichwortModifier.END_OF_WORD);
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("tester");
        assertFalse(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("test ");
        assertTrue(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("test");
        assertTrue(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("Atest");
        assertTrue(matcher.find());

        modifiers.add(StichwortModifier.START_OF_WORD);
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("Atest");
        assertFalse(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("test");
        assertTrue(matcher.find());
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("test ");
        assertTrue(matcher.find());

        modifiers.remove(StichwortModifier.START_OF_WORD);
        modifiers.remove(StichwortModifier.END_OF_WORD);
        modifiers.remove(StichwortModifier.NOT_CASE_SENSITIVE);

        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("ThisTestIsGood");
        assertFalse(matcher.matches());
        modifiers.add(StichwortModifier.PART_OF_WORD);
        matcher = Pattern.compile(regexStichwort.buildExpression(modifiers)).matcher("ThisTestIsGood");
        assertTrue(matcher.matches());

    }

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