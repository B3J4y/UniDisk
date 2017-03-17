package de.unidisk.nlp;

import de.unidisk.nlp.basics.EnhancedWithRegExp;
import de.unidisk.nlp.basics.GermanStemming;
import de.unidisk.nlp.datatype.Rule;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by carl on 08.03.17.
 */
public class BasicNLPTester {
    @Test
    public void testEnhancedWithRegExp() throws Exception {
        EnhancedWithRegExp enhanced = new EnhancedWithRegExp();
        Matcher matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("Test");
        assertTrue(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("Tester");
        assertTrue(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("tester");
        assertFalse(matcher.find());

        enhanced.addModifier(EnhancedWithRegExp.Modifier.NOT_CASE_SENSITIVE);
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("tester");
        assertTrue(matcher.find());

        enhanced.addModifier(EnhancedWithRegExp.Modifier.END_OF_WORD);
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("tester");
        assertFalse(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("test ");
        assertTrue(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("test");
        assertTrue(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("Atest");
        assertTrue(matcher.find());

        enhanced.addModifier(EnhancedWithRegExp.Modifier.START_OF_WORD);
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("Atest");
        assertFalse(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("test");
        assertTrue(matcher.find());
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher(" test ");
        assertTrue(matcher.find());

        enhanced.removeModifier(EnhancedWithRegExp.Modifier.START_OF_WORD);
        enhanced.removeModifier(EnhancedWithRegExp.Modifier.END_OF_WORD);
        enhanced.removeModifier(EnhancedWithRegExp.Modifier.NOT_CASE_SENSITIVE);

        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("ThisTestIsGood");
        assertFalse(matcher.matches());
        enhanced.addModifier(EnhancedWithRegExp.Modifier.PART_OF_WORD);
        matcher = Pattern.compile(enhanced.buildRegExp("Test")).matcher("ThisTestIsGood");
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