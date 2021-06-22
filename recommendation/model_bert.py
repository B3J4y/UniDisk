from model import TopicWordRecommender
from bertopic import BERTopic

class BERTRecommender(TopicWordRecommender):
    def setup(self) -> None:
        self.model = BERTopic().load("german.bert")

    def get_recommendations(self, query: str,topic: str, keywords: [str] = [],n = 10):

        recommendation_keywords = [x.lower() for x in keywords]
        if query.strip() != "":
            recommendation_keywords.append(query.lower())

        return get_recommendations(topic.lower(),self.model,recommendation_keywords,n)


def get_recommendations(q,model,keywords,n=10):
    try:
        similar_topics, similarity = model.find_topics(q.lower(), top_n=5)
        results = []
        for topic in similar_topics:
            missing_results = n - len(results)
            topic_words = list(filter(lambda x: x[0] not in keywords, model.get_topic(topic)))
            if len(topic_words) >= missing_results:
                results += topic_words[:missing_results]
                break
            else:
                results += topic_words

        if len(keywords) > 0:
            keyword_factor = .8
            keyword_results = [get_recommendations(keyword,model,[],n) for keyword in keywords]
            word_scores = {}
            for result in results:
                word_scores[result[0]] = result[1]
            for keyword_result in keyword_results:
                for result in keyword_result:
                    word = result[0]
                    score = result[1] * keyword_factor
                    if word in word_scores:
                        word_scores[word] += score
                    else:
                        word_scores[word] = score
            sorted_scores = sorted(word_scores.items(), key=lambda kv: kv[1],reverse=True)
            return list(filter(lambda x: x[0] not in keywords,sorted_scores))[:n]

        return results
    except ValueError as err:
        # unknown query word
        if "has not been learned" in str(err):
            return []
        raise err