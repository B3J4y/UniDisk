package de.unidisk.util;

import com.google.common.collect.Range;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Topic;
import org.apache.commons.lang.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataFactory {

    static String randomString(){
        return RandomStringUtils.randomAlphabetic(10);
    }

    static int randomInt(){
        return randomString().hashCode();
    }

    public static Keyword createKeyword(){
        Keyword k = new Keyword();
        k.setName(randomString());
        return k;
    }

    public static Topic createTopic(int keywordCount){
        Topic t = new Topic();
        t.setName(randomString());
        List<Keyword> keywords = IntStream.range(0,keywordCount).mapToObj(i -> createKeyword()).collect(Collectors.toList());
        t.setKeywords(keywords);
        return t;
    }
}
