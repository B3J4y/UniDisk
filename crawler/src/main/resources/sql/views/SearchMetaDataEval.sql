Create or replace view SearchMetaDataEval as 
Select *, SUBSTRING_INDEX(smd.url,'?',1) as scoreUrl
from SearchMetaData smd