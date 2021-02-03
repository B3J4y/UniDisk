export type KeywordRecommedation = {
  keyword: string;
  // Value between [0,1]
  probability: number;
};

export class KeywordRecommendationService {
  public async search(query: string, count: number = 10): Promise<KeywordRecommedation[]> {
    return [
      {
        keyword: 'test',
        probability: 1,
      },
    ];
  }
}
