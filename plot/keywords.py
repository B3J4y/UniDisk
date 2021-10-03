"""
Query
select p.id as projectId, 
   SUM(case when isSuggestion then 1 else 0 end) as suggestions,
SUM(case when isSuggestion then 0 else 1 end) as manual
 from 
Project p INNER JOIN 
Topic t ON p.id = t.projectId
INNER JOIN Keyword k ON 
t.id = k.topicId
where p.projectSubtype = 'DEFAULT'
group by p.id
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# File format: ProjectId;Suggestion count; Manual count
def parse_csv_row(row):
    columns = row.split(";")
    suggestions = int(columns[1].replace("\"",""))
    manual = int(columns[2].replace("\"",""))
    return (suggestions,manual)

def get_data():
    with open("keywords.csv","r") as file:
        content = file.read()
        rows = content.split("\n")[1:]
        parsed_rows = [parse_csv_row(x) for x in rows]
        grouped = {"suggestion": [], "manual": [],"total": []}
        for suggestion,manual in parsed_rows:
            grouped["suggestion"] += [suggestion]
            grouped["manual"] += [manual]
            grouped["total"] += [manual+suggestion]
        return grouped

data = get_data()
df = pd.DataFrame( data )
df.rename(columns={'suggestion': 'Vorschlag', 'manual': 'Benutzer','total':'Gesamt'}, inplace=True)
ax = df.plot(kind='bar')
ax.set_ylabel('Stichwortzahl')
ax.set_xlabel('Projekt')
plt.tight_layout()
plt.savefig('keywords.png')