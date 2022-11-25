import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# use ProjectScores query from crawler/src/main/resources/sql/ProjectScores.sql

input = "scores"

def map_project_type(type):
    if type == "CUSTOM_ONLY":
        return "Selbstgewählt"
    elif type == "BY_TOPICS":
        return "Thema"
    return "Gemischt"

# File format: id;score;projectSubtype;userId
def parse_csv_row(row):
    columns = row.split(";")
    score = float(columns[1].replace("\"",""))
    subtype = map_project_type(columns[2])
    return (subtype,score)

def get_data():
    with open(f"{input}.csv","r") as file:
        content = file.read()
        rows = content.split("\n")[1:]
        parsed_rows = [parse_csv_row(x) for x in rows]
        grouped = {}
        for subtype,score in parsed_rows:
            if subtype in grouped:
                grouped[subtype] += [score]
            else:
                grouped[subtype] = [score]
        return grouped

data = get_data()

def plot_scores(data):
    df = pd.DataFrame( data )
    ax = df.plot(kind='bar',width=0.8, figsize=(8,5))
    ax.set_ylabel('Güte')
    ax.set_xlabel('Projekt')
    for c in ax.containers:
        # set the bar label
        ax.bar_label(c, fmt='%.3f', label_type='edge', rotation='vertical', padding=5)
    ax.set_ylim([0,.4])
    plt.tight_layout()
    plt.savefig(f'{input}_scores.png')

def plot_total(data):
    group_sum = {k: sum(scores) for k, scores in data.items()}
    max_sum = max(group_sum.values())
    normalized_sums = {k: [score/max_sum ] for k, score in group_sum.items()}
    df = pd.DataFrame( normalized_sums )
    ax = df.plot(kind='bar')
    ax.set_ylabel('Güte')
    ax.set_xlabel('Kategorie')
    ax.get_xaxis().set_ticks([])
    for c in ax.containers:
        # set the bar label
        ax.bar_label(c, fmt='%.2f', label_type='edge')

    plt.tight_layout()
    plt.savefig(f'{input}_total.png')

plot_total(data)
plot_scores(data)
