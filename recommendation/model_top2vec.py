from model import TopicWordRecommender
from top2vec import Top2Vec

class Top2VecRecommender(TopicWordRecommender):
    def setup(self) -> None:
        self.model = Top2Vec.load("top2vec.model")

    def get_recommendations(self, query: str, n = 10):
        return get_recommendations(query,self.model,n)

def get_recommendations(query,model,n=10):
    try:
        topic_words, word_scores, topic_scores, topic_nums = model.search_topics(keywords=[query],num_topics=2)
        if len(topic_words) == 0:
            return []
        current_topic = 0
        current_word = 0
        response = []
        used_words = set()

        while len(response) < n:
            words = topic_words[current_topic]
            word = words[current_word]
            score = word_scores[current_topic][current_word]
            if  word not in used_words:
                word_score = (word, score.item())
                response.append(word_score)
                used_words.add(word)
            current_word +=1
            if current_word == len(words):
                if current_topic + 1 == len(topic_words):
                    return response
                else:
                    current_topic += 1
                    current_word = 0
        return response
    except ValueError as err:
        # unknown query word
        if "has not been learned" in str(err):
            return []
        raise err