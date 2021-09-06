from model import TopicWordRecommender
from top2vec import Top2Vec


query_scaling = .7
topic_word_scaling = .5
keyword_scaling = .4

# number of keywords included in calculation
keyword_count = 3

class Top2VecRecommender(TopicWordRecommender):
    def setup(self) -> None:
        self.model = Top2Vec.load("top2vec.model")

    def get_recommendations(self, query: str,topic: str, keywords: [str] = [], n = 10):
        return get_recommendations(topic,query,keywords,self.model,n)


def get_query_recommendations(query,model, ignore_words=[] ,n=10):
    if query.strip() == "":
        return []
    try:
        topic_words, word_scores, topic_scores, topic_nums = model.search_topics(keywords=[query],num_topics=2)
        if len(topic_words) == 0:
            return []
        current_topic = 0
        current_word = 0
        response = []
        used_words = set(ignore_words)

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

def scale_results(results,scale):
    return [(word,score * topic_word_scaling) for word,score in results]

def flatten(t):
    return [item for sublist in t for item in sublist]

def get_recommendations(topic, query, keywords, model,n=10):
    ignore_words = [topic,query] + keywords
    topic_words = scale_results(get_query_recommendations(topic,model,ignore_words,n), topic_word_scaling)
    query_words = scale_results(get_query_recommendations(query,model,ignore_words,n), query_scaling)
    keywords_results = [scale_results(get_query_recommendations(keyword,model,ignore_words,n),keyword_scaling) for keyword in keywords[:keyword_count]]

    scoring_words = flatten(keywords_results+[topic_words,query_words])

    words = {}

    for word,score in scoring_words:
        if word in words:
            words[word] += float(score) 
        else:
            words[word] = float(score)

    sorted_scores = sorted(words.items(), key=lambda kv: kv[1],reverse=True)
    result = sorted_scores[:n]
    return result