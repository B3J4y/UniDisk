package de.unidisk.crawler.io;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import de.unidisk.crawler.datatype.Model;
import de.unidisk.crawler.exception.NoResultsException;

import java.io.*;

/**
 * Created by carl on 06.01.16.
 */
public class ReadCsv {
    static private final Logger logger = LogManager.getLogger(ReadCsv.class.getName());
    private static final java.lang.String LINEDELIMITER = ";";

    private String fileName;
    public ReadCsv(String fileName) {
        logger.debug("Entering ReadCsv Constructor with fileName" + fileName);
        File file = new File(fileName);
        if (file.canRead()) {
            logger.debug("File exists");
            this.fileName = fileName;
        } else {
            logger.error("File doesn't exist");
            new FileNotFoundException("Cannot read File: " + fileName);
        }
        logger.debug("Leaving ReadCsv Constructor");
    }

    public Model convertToModel() throws IOException, NoResultsException {
        logger.debug("Entering convertToModel");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = "";
        Model m = new Model();
        Boolean header = true;
        while ((line = br.readLine()) != null) {
            logger.debug(line);
            if (header) {
                header = false;
                continue;
            }
            String[] args = line.split(LINEDELIMITER);
            if (args.length >= 3) {
                m.addDate(args[0].replace("\"", "").replace("/", "\\/"),
                        args[1].replace("\"", ""),
                        args[2].replace("\"", "").split("##"));
            } else {
                logger.error("Something went wrong with the csv File. Only " + Integer.toString(args.length)
                    + " arguments. CSV Line " + br);
            }
        }
        logger.debug("Leaving convertToModel");
        return m;
    }
}
