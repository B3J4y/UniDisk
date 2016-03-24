package uzuzjmd.competence.crawler.datatype;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by carl on 06.01.16.
 */
public class ImportedData {
    private HashMap<String, String> elements;
    static private final Logger logger = LogManager.getLogger(StichwortVar.class.getName());

    public ImportedData() {
        elements = new HashMap<>();
    }

    public HashMap<String, String> getVarList() {
        return elements;
    }

    public void addElement(String key, String var) {
        if (elements.containsKey(key) ) {
            if (!elements.get(key).equals(var)) {
                logger.warn("Key " + key + "exists but there was written another var. Old: "
                        + elements.get(key) + " New: " + var + " Will overwrite to new");
            }
        }
        elements.put(key, var);
    }

    public boolean containsKey(String key) {
        return elements.containsKey(key);
    }
    public void setVarList(HashMap<String, String> elements) {
        this.elements = elements;
    }

}
