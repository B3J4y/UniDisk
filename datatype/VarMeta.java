package uzuzjmd.competence.crawler.datatype;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import neo4j.Neo4JConnector;

import java.util.*;

/**
 * Created by carl on 06.01.16.
 */
public class VarMeta {
    private HashMap<String, DateScore> elements;
    static private final Logger logger = LogManager.getLogger(VarMeta.class.getName());
    private int sizeOfVarMeta = 0;
    public VarMeta() {
        elements = new HashMap<>();
    }

    public HashMap<String, DateScore> getElements() {
        return elements;
    }

    public void addElement(String key, String var) {
        logger.info("addElement key:" + key + " var:" + var);
        if (elements.containsKey(key)) {
            DateScore ds = elements.get(key);
            ds.metaVar.add(var);
            elements.put(key, ds);
        } else {
            elements.put(key, new DateScore(var));
        }
        sizeOfVarMeta++;
    }

    public void addElement(String key, DateScore ds) {
        logger.info("addElement key:" + key + " var:" + ds.toString());
        sizeOfVarMeta += ds.metaVar.size();
        elements.put(key, ds);
    }

    public String[] toNeo4JQuery() {
        logger.debug("Entering toNeo4JQuery");
        String[] result = new String[sizeOfVarMeta];
        int i = 0;
        for (String key : elements.keySet()) {
            DateScore ds = elements.get(key);
            for (String metaVar : elements.get(key).metaVar) {
                String str = Neo4JConnector.mergeRelation(
                        new AbstractMap.SimpleEntry<String, String>("Variable", (String) key),
                        new AbstractMap.SimpleEntry<String, String>("Meta", metaVar),
                        "classOf");
                result[i] = str;
                i++;
            }
        }
        logger.debug("Leaving toNeo4JQuery with query:" + Arrays.toString(result));
        return result;
    }

    public HashMap<String, String> toSolrQuery(StichwortVar swv) {
        logger.debug("Entering toSolrQuery");
        String q = "";
        HashMap<String,String> resultMap = new HashMap<>();
        HashMap<String, List<String>> varStich = new HashMap<>();
        for (String key : elements.keySet()) {
            varStich.put(key, new ArrayList<String>());
        }
        HashMap<String, String> swvElements = swv.getElements();
        for (String stich : swvElements.keySet()) {
            List<String> stichList = varStich.get(swvElements.get(stich));
            stichList.add(stich);
            varStich.put(swvElements.get(stich), stichList);
        }
        for (String key : varStich.keySet()) {
            q += key + ", ";
            resultMap.put(key, StringUtils.join(varStich.get(key), "\" OR \""));
        }
        q = q.substring(0, Math.max(0, q.length()-2));
        logger.debug("Leaving toSolrQuery with keys:" + q);
        return resultMap;
    }

    public void setSolrResult(SolrDocumentList docs, String key) {
        elements.get(key).documentList = docs;
    }

    public void setElements(HashMap<String, DateScore> elements) {
        this.elements = elements;
    }

    public List<String> getVariables() {
        List<String> resList = new ArrayList<String>();
        for (String key : elements.keySet()){
            resList.add(key);
        }
        return resList;
    }
}
