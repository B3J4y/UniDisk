Select p.id,score, p.projectSubtype, (case when (`p`.`parentProjectId` is null) then `p`.`id` else `p`.`parentProjectId` end) AS parentId
  from Project p INNER JOIN (
select p.id, SUM(score)/Count(tId) as score
FROM Project p 
Inner join Topic t on t.projectId = p.id
INNER JOIN (
SELECT  tId,uId, SUM(case when relevance = 'RELEVANT' then 1 else 0 end)/3 as score FROM (
SELECT pId,tId, uId, score, resultRelevance as relevance, scoreUrl as url FROM (

SELECT pId, tId,uId,score,
@topic_rank := IF(@current_topic = tId AND @current_uni = uId AND @current_project = pId, @topic_rank + 1, 1) AS topic_rank,
scoreUrl,
              @current_topic := tId,
@current_project := pId,
              @current_uni := uId FROM(
SELECT p.id as pId, t.id as tId, smde.university_id as uId, SUM(scores.score) as score,
smde.scoreUrl

from Project p 
INNER JOIN Topic t on p.id = t.projectId 
inner join Keyword k on k.topicId = t.id
INNER JOIN KeyWordScore kws ON kws.keyword_id = k.id
INNER JOIN SearchMetaDataEval smde ON smde.id = kws.searchMetaData_id
INNER JOIN (
select sum(score) as score, url from KeyWordScore kws 
INNER JOIN SearchMetaDataEval smd 
ON kws.searchMetaData_id = smd.id
group by smd.url 
) scores ON scores.url = smde.scoreUrl
group by p.id, t.id, smde.university_id, smde.scoreUrl
order by p.id, t.id, smde.university_id, score DESC) as source

) ranked 
INNER JOIN UniqueTopicRelevanceScores utrs ON ranked.tId = utrs.topicId AND ranked.scoreUrl = utrs.url
where ranked.topic_rank <= 3
) ranked 
GROUP BY tId, uId
) ranked On ranked.tId = t.id
GROUP BY p.id
) projectScores ON projectScores.id = p.id
order by p.id