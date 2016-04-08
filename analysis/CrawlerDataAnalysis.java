package uzuzjmd.competence.crawler.analysis;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by dehne on 07.04.2016.
 */
public class CrawlerDataAnalysis {

    public static final Integer minResults = 10;
    public static final Integer maxResults = 25;

    static Logger logger = Logger.getLogger(CrawlerDataAnalysis.class);

    /**
     * @param idScoreMap
     * @return
     */
    public static Collection<String> selectRelevantDataForPlotting(HashMap<Double, String> idScoreMap) {
        double[] values = ArrayUtils.toPrimitive(idScoreMap.keySet().toArray(new Double[0]));
        logger.info("there are " + values.length + " inputvalues!");
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(values);
        double guess = 100; // init value
        Boolean notInRange = true;
        while (notInRange) {
            final double percentile = descriptiveStatistics.getPercentile(guess);
            logger.trace("percentile for guess " + guess + " is: " + percentile);
            final Set<Double> filteredSet = Sets.filter(idScoreMap.keySet(), new Predicate<Double>() {
                @Override
                public boolean apply(@Nullable Double input) {
                    return input > percentile;
                }
            });
            logger.info("number of values over percentile are: " + filteredSet.size());
            guess = guess - 1.0;
            if (guess == 0) {
                break;
            } else {
                if (filteredSet.size() > minResults && filteredSet.size() < maxResults) {
                    Collection<String> finalResult = new LinkedHashSet<>();
                    for (Double aDouble : filteredSet) {
                        finalResult.add(idScoreMap.get(aDouble));
                    }
                    return finalResult;
                }
            }
        }
        return null;
    }
}
