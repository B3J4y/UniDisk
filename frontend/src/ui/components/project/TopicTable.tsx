import {
  Button,
  Grid,
  IconButton,
  List,
  ListItem,
  ListItemSecondaryAction,
  ListItemText,
  Paper,
  TextField,
  Toolbar,
} from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/Delete';
import { Keyword, Project, Topic } from 'data/entity';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import AlertDialog from 'ui/components/form/AlertDialog';
import { CreateTopicForm } from './CreateTopic';
import { CreateKeywordForm } from './keyword/CreateForm';

export type ProjectTopicsProps = {
  projectId: Project['id'];
  selected?: Topic;
  onSelect: (topic: Topic) => void;
  topics: Topic[];
  disabled?: boolean;
};
export function ProjectTopics(props: ProjectTopicsProps) {
  const disabled = props.disabled ?? false;
  const [selected, setSelected] = React.useState(props.selected);
  const { onSelect, topics } = props;

  useEffect(() => {
    setSelected(props.selected);
  }, [props.selected]);

  return (
    <Grid container xs={12} spacing={2}>
      <Grid item xs={6}>
        <Paper>
          <Toolbar>
            <Grid container justify="space-between" alignItems="center">
              <h2>Themen</h2>
            </Grid>
          </Toolbar>
          {!disabled && (
            <CreateTopicForm
              projectId={props.projectId}
              existingTopicNames={topics.map((topic) => topic.name)}
            />
          )}
          {topics.length === 0 && <p>Noch keine Themen hinzugefügt.</p>}
          {topics.length > 0 && (
            <List style={{ paddingBottom: 0, maxHeight: '500px', overflowY: 'auto' }}>
              {topics.map((topic) => {
                return (
                  <TopicItem
                    key={topic.id}
                    topic={topic}
                    disabled={disabled}
                    selected={selected?.id === topic.id}
                    onSelect={() => onSelect(topic)}
                  />
                );
              })}
            </List>
          )}
        </Paper>
      </Grid>
      <Grid item xs={6}>
        {selected && (
          <TopicKeywords
            topicId={selected?.id}
            keywords={selected?.keywords ?? []}
            disabled={disabled}
          />
        )}
      </Grid>
    </Grid>
  );
}

type TopicKeywordsProps = {
  topicId: Topic['id'];
  keywords: Keyword[];
  disabled?: boolean;
};

function TopicKeywords(props: TopicKeywordsProps) {
  const disabled = props.disabled ?? false;
  const provider = useProvider();
  const { keywords } = props;
  return (
    <Paper>
      <Toolbar>
        <Grid container justify="space-between" alignItems="center">
          <h2>Stichworte</h2>
        </Grid>
      </Toolbar>
      {!disabled && (
        <CreateKeywordForm topicId={props.topicId} topicKeywords={keywords.map((k) => k.name)} />
      )}
      {keywords.length === 0 && <p>Noch keine Stichworte hinzugefügt</p>}
      {keywords.length > 0 && (
        <List style={{ paddingBottom: 0, maxHeight: '500px', overflowY: 'auto' }}>
          {keywords.map((keyword) => {
            return (
              <ListItem key={keyword.id}>
                <ListItemText primary={keyword.name} />
                {!disabled && (
                  <ListItemSecondaryAction>
                    <Button
                      onClick={async () => {
                        const container = provider.getKeywordDetailContainer();
                        container.setEntity(keyword);
                        await container.delete();
                      }}
                    >
                      X
                    </Button>
                  </ListItemSecondaryAction>
                )}
              </ListItem>
            );
          })}
        </List>
      )}
    </Paper>
  );
}

type TopicItemProps = {
  topic: Topic;
  disabled?: boolean;
  selected: boolean;
  onSelect: () => void;
};

function TopicItem(props: TopicItemProps) {
  const provider = useProvider();
  const disabled = props.disabled ?? false;
  const { topic, selected, onSelect } = props;
  const [name, setName] = React.useState(topic.name);

  return (
    <ListItem
      key={topic.id}
      button
      selected={selected}
      onClick={() => {
        onSelect();
      }}
    >
      <ListItemText
        primary={
          <TextField
            value={name}
            disabled={disabled}
            onChange={(e) => setName(e.target.value)}
            onBlur={async (e) => {
              if (name !== topic.name) {
                const container = provider.getTopicDetailContainer();
                container.setEntity(topic);
                await container.update({ id: topic.id, name });
              }
            }}
          />
        }
      />
      {!disabled && (
        <ListItemSecondaryAction>
          <AlertDialog
            title="Thema löschen"
            contentText={`Soll das Thema ${topic.name} wirklich gelöscht werden? Alle Stichworte werden ebenfalls gelöscht.`}
            action={async () => {
              const container = provider.getTopicDetailContainer();
              container.setEntity(topic);
              await container.delete();
              const { state } = container;

              return {
                success: state.delete.isFinished,
                error: state.delete.hasError ? 'Thema konnte nicht gelöscht werden.' : undefined,
              };
            }}
            builder={(setOpen) => {
              return (
                <IconButton
                  edge="end"
                  aria-label="delete"
                  onClick={() => {
                    setOpen(true);
                  }}
                >
                  {' '}
                  <DeleteIcon />
                </IconButton>
              );
            }}
          />
        </ListItemSecondaryAction>
      )}
    </ListItem>
  );
}
