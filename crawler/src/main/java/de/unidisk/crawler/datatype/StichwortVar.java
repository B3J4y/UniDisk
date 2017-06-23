package de.unidisk.crawler.datatype;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.*;

/**
 * Created by carl on 06.01.16.
 */
public class StichwortVar {
    private HashMap<String, String> elements;
    static private final Logger logger = LogManager.getLogger(StichwortVar.class.getName());

    public StichwortVar() {
        elements = new HashMap<>();
    }

    public HashMap<String, String> getElements() {
        return elements;
    }

    public void addElement(String key, String var) {
        if (elements.containsKey(key) ) {
            if (!elements.get(key).equals(var)) {
                logger.warn("Key " + key + " exists but there was written another var. Old: "
                        + elements.get(key) + " New: " + var + " Will overwrite to new");
            }
        }
        logger.info("addElement key:" + key + " var:" + var);
        elements.put(key, var);
    }

    public HashMap<String, String> toVarStichwort() {
        logger.debug("Entering toVarStichwort");
        HashMap<String, String> result = new HashMap<>();
        for (String key :
                elements.keySet()) {
            if (result.containsKey(elements.get(key))) {
                String str = result.get(elements.get(key));
                if (! Arrays.asList(str.split(";")).contains(key)) {
                    result.put(elements.get(key), str + ";" + key);
                }
            } else {
                result.put(elements.get(key), key);
            }
        }

        String resString = "";
        for (String key :
                result.keySet()) {
            resString += key + ":" + result.get(key) + ",";
        }
        
        logger.debug("Leaving toVarStichwort " + resString.substring(0, resString.length() - 1));
        return result;
    }

    /**
     * @Deprecated ist momentan nicht akkurat
     */
    @Deprecated
    public void insertSynonym() {
        logger.debug("Entering insertSynonym");
        HashMap<String, Integer> synCount = new HashMap<>();
        HashMap<String, String> synonyms = new HashMap<>();
        for (String key :
                elements.keySet()) {
            String strSynonyms = key + ":";
          /*
            for (String synonym :
                    OpenThesaurusSynonymCreator.getSysnonymsAsJava(key)) {
                synonyms.put(synonym, elements.get(key));
                strSynonyms += synonym + ",";
                if (synCount.containsKey(key)) {
                    int count = synCount.get(key);
                    synCount.put(key, count + 1 );
                } else {
                    synCount.put(key, 1);
                }
            }
            logger.debug(strSynonyms.substring(0, strSynonyms.length() - 1));
            */
        }
        elements.putAll(synonyms);
        String res = "";
        for (String key :
                synCount.keySet()) {
            res += key + ":" + String.valueOf(synCount.get(key)) + ";";
        }
        if (res.length() > 0) {
            logger.info("Got new Synonyms in " + res.substring(0, res.length() - 1));
        } else {
            logger.info("No new Synonyms found");
        }
        logger.debug("Leaving insertSynonym");
    }
    

    public String toSolrQuery() {
        logger.debug("Entering toSolrQuery");
        String q = "";

        for (String key : elements.keySet()) {
            q += "\"" + key + "\"+" ;
        }
        q = q.substring(0, q.length() - 1);
        logger.debug("Leaving toSolrQuery with Query:" + q);
        return q;
    }

    public String toSolrQuery(String... keys) {
        logger.debug("Entering toSolrQuery with key:" + keys.length);
        String q = "";
        for (String key : keys) {
            q += "\"" + key + "\"+" ;
        }
        q = q.substring(0, q.length() - 1);
        logger.debug("Leaving toSolrQuery with Query:" + q);
        return q;
    }

    public boolean containsKey(String key) {
        return elements.containsKey(key);
    }
    public void setVarList(HashMap<String, String> elements) {
        this.elements = elements;
    }
}
