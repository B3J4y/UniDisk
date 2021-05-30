package de.unidisk.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.KeywordRecommendation;
import de.unidisk.contracts.services.recommendation.RecommendationResult;

import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("http://localhost:8083/similiar?q="+topic))
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() != 200)
                return null;


            ObjectMapper mapper = new ObjectMapper();
            ResponseDto dto = mapper.readValue(response.body(),ResponseDto.class);
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
