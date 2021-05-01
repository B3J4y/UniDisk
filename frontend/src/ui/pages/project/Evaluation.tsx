import { Grid, IconButton, Link, makeStyles, Tooltip } from '@material-ui/core';
import React, { useEffect } from 'react';
import ThumbUpIcon from '@material-ui/icons/ThumbUp';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import { useProvider } from 'Provider';
import { Subscribe } from 'unstated-typescript';
import { FeedbackStatus, ProjectEvaluationResult, ProjectType } from 'data/repositories';
import { KeywordResult, TopicResult } from 'data/entity';
import { LocalizedTable } from 'ui/components/Table';

export type ProjectEvaluationPageProps = {
  result: ProjectEvaluationResult;
};

export function ProjectEvaluationPage(props: ProjectEvaluationPageProps) {
  return <FeedbackTable results={props.result} />;
}

type CustomTopicResult = {
  id: string;
  university: UniversityFeedbackUrl;
  relevance: FeedbackStatus;
};

type UniversityFeedbackUrl = {
  url: string;
  name: string;
};

//https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
function shuffleArray<T>(array: T[]): T[] {
  const arrayCopy = array.slice(0);
  for (var i = arrayCopy.length - 1; i > 0; i--) {
    var j = Math.floor(Math.random() * (i + 1));
    var temp = arrayCopy[i];
    arrayCopy[i] = arrayCopy[j];
    arrayCopy[j] = temp;
  }
  return arrayCopy;
}

type FeedbackTableProps = {
  results: ProjectEvaluationResult;
};

const useStyles = makeStyles(() => ({
  feedbackIcon: {
    '& svg': {
      fontSize: 20,
    },
  },
}));

function prepareProjectResults(results: TopicResult[]) {
  console.log(results);
  const topicResults = results.map((topic) => {
    const { keywords } = topic;

    const urlScores: Record<string, number> = {};

    keywords.forEach((keyword) => {
      const url = keyword.searchMetaData.url;
      const score = keyword.score;
      if (urlScores[url] !== undefined) {
        urlScores[url] += score;
      } else {
        urlScores[url] = score;
      }
    });

    const reverseScoreMap = Object.keys(urlScores).reduce((prev, cur) => {
      const value = urlScores[cur] as number;
      return {
        ...prev,
        [value]: cur,
      };
    }, {} as Record<number, string>);

    const scores = Object.values(urlScores);
    scores.sort((v1, v2) => v2 - v1);

    const topN = 3;

    const topScoreUrls = scores
      .slice(0, topN)
      .map((score) => ({ url: reverseScoreMap[score], score }));

    const mappedScores = topScoreUrls.map((topScore) => {
      const result = {
        score: topScore.score,
        url: topScore.url,
      };
      return result;
    });

    return {
      topScores: mappedScores,
      topic: topic.topic,
      university: topic.university,
    };
  });

  return topicResults;
}

function FeedbackTable(props: FeedbackTableProps) {
  const enums: ProjectType[] = shuffleArray(
    Object.values(ProjectType).filter((v) => typeof v === 'number'),
  ) as ProjectType[];

  const results = props.results.results;

  if (Object.keys(results).length !== enums.length)
    return <ProjectResults topicScores={results[ProjectType.Default]} />;

  //https://stackoverflow.com/questions/6913512/how-to-sort-an-array-of-objects-by-multiple-fields
  const fieldSorter = (fields) => (a, b) =>
    fields
      .map((o) => {
        let dir = 1;
        if (o[0] === '-') {
          dir = -1;
          o = o.substring(1);
        }
        return a[o] > b[o] ? dir : a[o] < b[o] ? -dir : 0;
      })
      .reduce((p, n) => (p ? p : n), 0);

  const mappedResults = Object.keys(results).map((key) => {
    const projectType = (key as unknown) as ProjectType;
    const projectResults = results[projectType];
    const topicResults = prepareProjectResults(projectResults);

    topicResults.sort(fieldSorter(['topic', 'university']));
    return {
      results: topicResults,
      type: projectType,
    };
  });

  return (
    <Grid container>
      {mappedResults.map((r) => {
        return (
          <Grid item xs>
            {r.results.map((result) => {
              return (
                <>
                  <h4>
                    {result.topic.name} {result.university.name}
                  </h4>
                  {result.topScores.map((score) => {
                    return (
                      <p>
                        <a href={score.url}>{score.url}</a>
                      </p>
                    );
                  })}
                </>
              );
            })}
          </Grid>
        );
      })}
    </Grid>
  );
}

type TableItemProps = {
  result: KeywordResult | undefined;
};
function TableItem(props: TableItemProps) {
  const { result } = props;

  useEffect(() => {
    if (result?.relevance) setFeedbackState(result.relevance);
  }, [result?.relevance]);
  const styles = useStyles();
  const provider = useProvider();
  const [feedbackState, setFeedbackState] = React.useState<FeedbackStatus>(
    result?.relevance ?? FeedbackStatus.None,
  );

  if (!result) return <p></p>;

  const {
    searchMetaData: { url },
  } = result;

  const container = provider.getFeedbackResultContainer();

  const isRelevant = feedbackState === FeedbackStatus.Relevant;
  const isNotRelevant = feedbackState === FeedbackStatus.NotRelevant;

  return (
    <Subscribe to={[container]}>
      {(c) => {
        return (
          <Grid container xs={12} justify="center" alignItems="center">
            <Grid item>
              <Link href={url} target="_blank">
                {url}
              </Link>
            </Grid>
            <Grid item>
              <Tooltip title="Relevant">
                <IconButton
                  className={styles.feedbackIcon}
                  style={{ color: isRelevant ? 'green' : undefined }}
                  onClick={() => {
                    const newState = isRelevant ? FeedbackStatus.None : FeedbackStatus.Relevant;
                    c.rate(result.id, newState ?? FeedbackStatus.None);
                    setFeedbackState(newState);
                  }}
                >
                  <ThumbUpIcon />
                </IconButton>
              </Tooltip>
            </Grid>
            <Grid item>
              <Tooltip title="Nicht Relevant">
                <IconButton
                  className={styles.feedbackIcon}
                  style={{ color: isNotRelevant ? 'red' : undefined }}
                  onClick={() => {
                    const newState = isNotRelevant
                      ? FeedbackStatus.None
                      : FeedbackStatus.NotRelevant;
                    c.rate(result.id, newState ?? FeedbackStatus.None);
                    setFeedbackState(newState);
                  }}
                >
                  <ThumbDownIcon />
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>
        );
      }}
    </Subscribe>
  );
}

type ProjectResultsProps = {
  topicScores: TopicResult[];
};
function ProjectResults(props: ProjectResultsProps) {
  const { topicScores } = props;

  return (
    <LocalizedTable
      columns={[
        { title: 'Universität', field: 'university.name' },
        { title: 'Thema', field: 'topic.name' },
        { title: 'Score', field: 'score', type: 'numeric' },
        {
          title: 'Anzahl Einträge',
          field: 'entryCount',
          type: 'numeric',
        },
      ]}
      data={topicScores}
      title="Auswertung"
    />
  );
}
