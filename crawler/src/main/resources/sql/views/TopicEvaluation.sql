Create or replace view TopicEvaluation as 
Select Project.id as projectId, Project.name as project, Project.projectSubtype, Topic.name as topic, scores.score 
from Project INNER JOIN Topic ON Project.id = Topic.projectId 
INNER JOIN (
select Topic.id, 
SUM(case when ProjectRelevanceScore.resultRelevance = 'RELEVANT' then 1 else 0 end)/COUNT(ProjectRelevanceScore.id) as score
  from Topic 
 INNER JOIN ProjectRelevanceScore on ProjectRelevanceScore.topicId = Topic.id
group by Topic.id
) scores 
ON scores.id = Topic.id;
