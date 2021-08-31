import { Box, Button, Grid, TextField } from '@material-ui/core';
import { Project } from 'data/entity';
import { useProvider } from 'Provider';
import React from 'react';
import { Subscribe } from 'unstated-typescript';

export type CreateTopicFormProps = {
  projectId: Project['id'];
  existingTopicNames: string[];
};

export function CreateTopicForm(props: CreateTopicFormProps) {
  const { projectId, existingTopicNames } = props;

  const provider = useProvider();
  const [name, setName] = React.useState('');
  const [container, setContainer] = React.useState(provider.getTopicDetailContainer());
  const [inputError, setInputError] = React.useState<string | undefined>(undefined);

  return (
    <Subscribe to={[container]}>
      {(container) => {
        const createTopic = async () => {
          const topicName = name.toLowerCase().trim();
          if (
            existingTopicNames.some(
              (existingName) => existingName.toLowerCase().trim() === topicName,
            )
          ) {
            setInputError('Thema mit gleichem Namen existiert bereits.');
            return;
          }

          setInputError('');

          await container.create({
            name: name.trim(),
            projectId,
          });

          if (container.state.save.isFinished) {
            setName('');
            setContainer(provider.getTopicDetailContainer());
          }
        };

        return (
          <form
            onSubmit={(e) => {
              e.preventDefault();
              createTopic();
            }}
          >
            <Box p={2}>
              <Grid container justify="space-between" alignItems="center" spacing={2}>
                <Grid item style={{ flexGrow: 1, display: 'flex' }}>
                  <TextField
                    onChange={(e) => {
                      setName(e.target.value);
                    }}
                    fullWidth
                    value={name}
                    variant="outlined"
                    placeholder="Thema..."
                  />
                </Grid>
                <Grid item>
                  <Button type="submit">+</Button>
                </Grid>
              </Grid>
              {inputError && <p style={{ color: 'red' }}>{inputError}</p>}
            </Box>
          </form>
        );
      }}
    </Subscribe>
  );
}
