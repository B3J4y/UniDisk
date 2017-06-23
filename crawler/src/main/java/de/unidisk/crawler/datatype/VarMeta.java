package de.unidisk.crawler.datatype;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;

import java.util.*;

/**
 * Created by carl on 06.01.16.
 */
public class VarMeta {
    private HashMap<String, SolrDocumentList> elements;
    static private final Logger logger = LogManager.getLogger(VarMeta.class.getName());
    private int sizeOfVarMeta = 0;
    public VarMeta() {
        elements = new HashMap<>();
    }

    public HashMap<String, SolrDocumentList> getElements() {
        return elements;
    }

    public void addElement(String key) {
        logger.info("addElement key:" + key);
        elements.put(key, null);
        sizeOfVarMeta++;
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
        elements.put(key, docs);
    }

    public void setElements(HashMap<String, SolrDocumentList> elements) {
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
