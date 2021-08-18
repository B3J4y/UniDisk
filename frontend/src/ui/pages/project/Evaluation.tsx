import { Grid, IconButton, Link, makeStyles, Tooltip } from '@material-ui/core';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import ThumbUpIcon from '@material-ui/icons/ThumbUp';
import { TopicResult } from 'data/entity';
import { FeedbackStatus, ProjectEvaluationResult, ProjectType } from 'data/repositories';
import { ProjectDetailContainer } from 'model';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import { LocalizedTable } from 'ui/components/Table';
import { Subscribe } from 'unstated-typescript';

export type ProjectEvaluationPageProps = {
  result: ProjectEvaluationResult;
};

export function ProjectEvaluationPage(props: ProjectEvaluationPageProps) {
  return (
    <Subscribe to={[ProjectDetailContainer]}>
      {(container) => <FeedbackTable results={props.result} container={container} />}
    </Subscribe>
  );
}

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
  container: ProjectDetailContainer;
};

const useStyles = makeStyles(() => ({
  feedbackIcon: {
    '& svg': {
      fontSize: 20,
    },
  },
}));

function prepareProjectResults(results: TopicResult[], top: number = 3) {
  const topicResults = results.map((topic) => {
    const { keywords } = topic;

    const urlScores: Record<string, number> = {};
    const urlPageNames: Record<string, string> = {};

    keywords.forEach((keyword) => {
      const url = keyword.searchMetaData.url;
      urlPageNames[url] = keyword.pageTitle;
      const score = keyword.score;
      if (urlScores[url] !== undefined) {
        urlScores[url] += score;
      } else {
        urlScores[url] = score;
      }
    });

    const scores = Object.entries(urlScores);
    // Sort by score descending
    scores.sort(([__, v1], [_, v2]) => v2 - v1);

    const topScoreUrls = scores.slice(0, top).map(([url, score]) => ({ url, score }));

    const mappedScores = topScoreUrls.map((topScore) => {
      const result = {
        score: topScore.score,
        url: topScore.url,
        pageTitle: urlPageNames[topScore.url],
      };
      return result;
    });

    return {
      topScores: mappedScores,
      topic: topic.topic,
      topicName: topic.topic.name,
      universityName: topic.university.name,
      university: topic.university,
    };
  });

  return topicResults;
}

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

function FeedbackTable(props: FeedbackTableProps) {
  const { container } = props;

  const enums: ProjectType[] = Object.values(ProjectType).filter(
    (v) => typeof v === 'number',
  ) as ProjectType[];

  const results = props.results.results;

  if (Object.keys(results).length !== enums.length)
    return <ProjectResults topicScores={results[ProjectType.Default].topicResults} />;

  const mappedResults = Object.keys(results).map((key) => {
    const projectType = (key as unknown) as ProjectType;
    const projectResults = results[projectType];
    const topicResults = prepareProjectResults(projectResults.topicResults);

    topicResults.sort(fieldSorter(['topicName', 'universityName']));
    return {
      results: topicResults,
      type: projectType,
    };
  });

  const maxResults = Math.max(...mappedResults.map((r) => r.results.length));

  return (
    <Grid container>
      {Array.from({ length: maxResults }).map((_, index) => {
        const children = enums.map((projectType) => {
          // @ts-ignore
          const results = mappedResults.find((r) => r.type === projectType.toString());
          if (!results) return <Grid item xs></Grid>;
          const result = index < results.results.length ? results.results[index] : undefined;
          if (!result) return <Grid item xs></Grid>;

          return (
            <Grid item xs>
              <h4>
                {result.topic.name} {result.university.name}
              </h4>
              {result.topScores.map((score) => {
                const relevance = container.getRelevance({
                  url: score.url,
                  topicId: result.topic.id,
                });
                return (
                  <TableItem
                    url={score.url}
                    pageTitle={score.pageTitle}
                    relevance={relevance}
                    topicId={result.topic.id}
                  />
                );
              })}
            </Grid>
          );
        });

        return <Grid container>{children}</Grid>;
      })}
    </Grid>
  );
}

type TableItemProps = {
  url: string;
  pageTitle: string;
  relevance: FeedbackStatus;
  topicId: string;
};
function TableItem(props: TableItemProps) {
  const { url, pageTitle, topicId } = props;

  useEffect(() => {
    setFeedbackState(props.relevance);
  }, [props.relevance]);
  const styles = useStyles();
  const provider = useProvider();
  const [feedbackState, setFeedbackState] = React.useState<FeedbackStatus>(props.relevance);

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
                {pageTitle}
              </Link>
            </Grid>
            <Grid item>
              <Tooltip title="Relevant">
                <IconButton
                  className={styles.feedbackIcon}
                  style={{ color: isRelevant ? 'green' : undefined }}
                  onClick={() => {
                    const newState = isRelevant ? FeedbackStatus.None : FeedbackStatus.Relevant;
                    c.rate({
                      relevance: newState ?? FeedbackStatus.None,
                      url,
                      topicId,
                    });
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
                    c.rate({
                      relevance: newState ?? FeedbackStatus.None,
                      url,
                      topicId,
                    });
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

export type ProjectResultsProps = {
  topicScores: TopicResult[];
};
export function ProjectResults(props: ProjectResultsProps) {
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
