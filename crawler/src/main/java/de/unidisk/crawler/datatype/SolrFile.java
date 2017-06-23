package de.unidisk.crawler.datatype;

import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * Data representation of a convertible solr file
 */
public class SolrFile {
    private Date modifiedDate;
    private String content;
    private String title;
    private int id;

    //possible filenames 01-title.txt
    //id-titlename.suffix
    public SolrFile(String absolutFilepath) throws IOException {
        File file = new File(absolutFilepath);
        if (file.isFile()) {
            //remove suffix
            if (file.getName().contains(".")) {
                title = file.getName().substring(0, file.getName().lastIndexOf("."));
            } else {
                title = file.getName();
            }
            if (title.contains("-") && StringUtils.isNumeric(title.split("-")[0])) {
                id = Integer.valueOf(title.split("-")[0]);
                title = title.substring(title.indexOf("-") + 1);
            }
            BufferedReader br = new BufferedReader(new FileReader(absolutFilepath));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
            content = contentBuilder.toString();
            modifiedDate = new Date(file.lastModified());
        }
    }

    public SolrInputDocument getSolrInputDocument() {
        SolrInputDocument document = new SolrInputDocument();
        document.setField(SolrStandardConfigurator.getFieldProperty("id"), id);
        document.setField(SolrStandardConfigurator.getFieldProperty("title"), title);
        document.setField(SolrStandardConfigurator.getFieldProperty("content"), content);
        document.setField(SolrStandardConfigurator.getFieldProperty("date"), modifiedDate);
        return document;
    }
}
