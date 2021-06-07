package de.unidisk.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unidisk.common.HttpHelper;
import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.KeywordRecommendation;
import de.unidisk.contracts.services.recommendation.RecommendationResult;

import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class KeywordRecommendationService implements IKeywordRecommendationService {


    static class ResponseDto {
        String requestId;
        Object[] similiar;

        public ResponseDto() {
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public Object[] getSimiliar() {
            return similiar;
        }

        public void setSimiliar(Object[] similiar) {
            this.similiar = similiar;
        }
    }

    @Override
    public RecommendationResult getTopicRecommendations(String topic) {
        try {
            String queryParam = "q="+URLEncoder.encode(topic,"UTF-8");
            String url ="http://localhost:8083/similiar?"+queryParam;
            HttpHelper.HttpResponse response = HttpHelper.get(url);
            if(response.getStatusCode() != 200)
                return null;

            ObjectMapper mapper = new ObjectMapper();
            ResponseDto dto = mapper.readValue(response.getBody(),ResponseDto.class);
            List<KeywordRecommendation> recommendations =    Arrays.stream(dto.getSimiliar()).map(pair -> {
                ArrayList<Object> values =  (ArrayList<Object>) pair;
                double score = (double)values.get(1);
                String word = (String) values.get(0);
                return new KeywordRecommendation(score,word);
            }).collect(Collectors.toList());

            return new RecommendationResult(
                    recommendations,
                    dto.getRequestId()
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
