package uzuzjmd.competence.crawler.datatype;

import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 06.01.16.
 */
public class DateScore {
    public List<String> metaVar;
    public SolrDocumentList documentList;

    public DateScore() {
        metaVar = new ArrayList<>();
    }
    public DateScore(String metaVar) {
        this.metaVar = new ArrayList<>();
        this.metaVar.add(metaVar);
    }
    public DateScore(List<String> metaVar) {
        this.metaVar = metaVar;
    }
}
