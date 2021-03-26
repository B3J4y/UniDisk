import { Grid, IconButton, Link, makeStyles, Tooltip } from '@material-ui/core';
import React, { useEffect } from 'react';
import ThumbUpIcon from '@material-ui/icons/ThumbUp';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import { useProvider } from 'Provider';
import { Subscribe } from 'unstated-typescript';
import { FeedbackStatus, ProjectEvaluationResult, ProjectType } from 'data/repositories';
import { TopicResult } from 'data/entity';
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

function FeedbackTable(props: FeedbackTableProps) {
  const enums: ProjectType[] = shuffleArray(
    Object.values(ProjectType).filter((v) => typeof v === 'number'),
  ) as ProjectType[];

  type ItemType = {
    topic: {
      name: string;
    };
  } & Record<ProjectType, CustomTopicResult>;
  const results = props.results.results;

  if (Object.keys(results).length !== enums.length)
    return <ProjectResults topicScores={results[ProjectType.Default]} />;

  const items: ItemType[] = results[0].map((item, index) => {
    const subtypeScores: Record<ProjectType, CustomTopicResult> = enums.reduce((prev, cur) => {
      const subtypeResults = results[cur];
      return {
        ...prev,
        [cur]: subtypeResults[index],
      };
    }, {} as Record<ProjectType, CustomTopicResult>);

    return {
      topic: {
        name: item.university.name,
      },
      ...subtypeScores,
    };
  });

  return (
    <LocalizedTable
      columns={[
        { title: 'Thema', field: 'topic.name', grouping: true, defaultGroupOrder: 0 },
        ...Object.keys(enums).map((key, i) => {
          const type = (key as unknown) as ProjectType;
          return {
            title: `Universität ${i + 1}`,
            field: type,

            render: (item: ItemType) => {
              return <TableItem result={item[type]} />;
            },
          };
        }),
      ]}
      data={items}
      title="Auswertung"
    />
  );
}

type TableItemProps = {
  result: CustomTopicResult;
};
function TableItem(props: TableItemProps) {
  const { result } = props;
  const { university } = result;
  const styles = useStyles();
  const provider = useProvider();
  const container = provider.getFeedbackResultContainer();
  const [feedbackState, setFeedbackState] = React.useState<FeedbackStatus>(result.relevance);

  useEffect(() => {
    setFeedbackState(props.result.relevance);
  }, [props.result.relevance]);

  const isRelevant = feedbackState === FeedbackStatus.Relevant;
  const isNotRelevant = feedbackState === FeedbackStatus.NotRelevant;

  return (
    <Subscribe to={[container]}>
      {(c) => {
        return (
          <Grid container xs={12} justify="center" alignItems="center">
            <Grid item>
              <Link href={university.url} target="_blank">
                {university.name}
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
