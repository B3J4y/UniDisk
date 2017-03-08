package de.unidisk.nlp;

import de.unidisk.nlp.basics.EnhancedWithRegExp;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    }
}
