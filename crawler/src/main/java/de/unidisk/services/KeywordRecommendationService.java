package de.unidisk.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.KeywordRecommendation;
import de.unidisk.contracts.services.recommendation.RecommendationResult;

import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
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
            URL url =  new URL("http://localhost:8083/similiar?"+queryParam);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if(status != 200)
                return null;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"UTF-8"));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            ObjectMapper mapper = new ObjectMapper();
            ResponseDto dto = mapper.readValue(content.toString(),ResponseDto.class);
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
