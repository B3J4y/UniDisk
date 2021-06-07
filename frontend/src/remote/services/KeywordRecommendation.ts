import axios from 'axios';
import { KEYWORD_SERVICE_ENDPOINT } from 'config';
export type KeywordRecommedation = {
  keyword: string;
  // Value between [0,1]
  probability: number;
};

export type KeywordRecommedationResult = {
  recommendations: KeywordRecommedation[];
  id: string;
};

export type RecommendationUsedParams = {
  requestId: string;
  keyword: string;
};
export class KeywordRecommendationService {
  private client = axios.create({
    baseURL: KEYWORD_SERVICE_ENDPOINT,
  });

  public async recommendationUsed(params: RecommendationUsedParams): Promise<void> {
    await this.client.post(`/recommendation`, {
      keyword: params.keyword,
      requestId: params.requestId,
    });
  }

  public async search(query: string, count: number = 10): Promise<KeywordRecommedationResult> {
    const result = await this.client.get(`/similiar`, {
      params: {
        q: query,
        count,
      },
    });

    const data = result.data as {
      requestId: string;
      similiar: [string, number][];
    };

    const recommendations = data.similiar.map((value) => {
      return {
        keyword: value[0],
        probability: value[1],
      };
    });
    return {
      recommendations,
      id: data.requestId,
    };
  }
}
