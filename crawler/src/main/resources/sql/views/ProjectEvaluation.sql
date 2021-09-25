Create or replace view ProjectEvaluation as SELECT Project.id, scores.score, Project.projectSubtype, userId FROM Project inner join (
select Project.id, 
SUM(case when ProjectRelevanceScore.resultRelevance = 'RELEVANT' then 1 else 0 end)/COUNT(ProjectRelevanceScore.id) as score
  from Project 
 INNER JOIN Topic on Project.id = Topic.projectId
 INNER JOIN ProjectRelevanceScore on ProjectRelevanceScore.topicId = Topic.id
group by Project.id
) scores on scores.id = Project.id