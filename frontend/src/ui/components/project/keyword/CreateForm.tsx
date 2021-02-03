import { Box, Button, Grid, List, ListItem, ListItemText, TextField } from '@material-ui/core';
import { Topic } from 'data/entity';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import { KeywordRecommedation } from 'remote/services/KeywordRecommendation';
import { Subscribe } from 'unstated-typescript';

export type CreateKeywordFormProps = {
  topicId: Topic['id'];
  topicKeywords: string[];
  disabled?: boolean;
};

export function CreateKeywordForm(props: CreateKeywordFormProps) {
  const { topicId, topicKeywords } = props;
  const provider = useProvider();
  const disabled = props.disabled ?? false;
  const [name, setName] = React.useState('');
  const [container, setContainer] = React.useState(provider.getKeywordDetailContainer());

  const [loading, setLoading] = React.useState(false);
  const [recommendations, setRecommendations] = React.useState<KeywordRecommedation[] | undefined>(
    undefined,
  );
  const [showRecommendations, setShowRecommendations] = React.useState(false);

  const [inputError, setInputError] = React.useState<string | undefined>(undefined);

  const x = async () => {
    try {
      const results = await provider.getKeywordRecommendationService().search(name);
      const filtered = results.filter((result) => !topicKeywords.includes(result.keyword));
      setRecommendations(filtered);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setLoading(true);
    x();
  }, [name]);

  const keywordRecommendationClass = 'keyword-rec-item';

  return (
    <Subscribe to={[container]}>
      {(container) => {
        const createKeyword = async (keyword: string, reset: boolean = false) => {
          await container.create({
            name: keyword,
            topicId,
          });

          if (container.state.save.isFinished) {
            if (reset) {
              setName('');
              setRecommendations(undefined);
              setShowRecommendations(false);
            }
            setContainer(provider.getKeywordDetailContainer());
          }
        };

        return (
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (topicKeywords.includes(name)) {
                setInputError('Stichwort kann Thema nur einmal zugewiesen werden.');
                return;
              }
              createKeyword(name, true);
            }}
          >
            <Box p={2}>
              <Grid container alignItems="center" spacing={2}>
                <Grid item style={{ position: 'relative', display: 'flex', flexGrow: 1 }}>
                  <TextField
                    onBlur={(e) => {
                      if (e.relatedTarget) {
                        const className = (e.relatedTarget as { className?: string }).className;
                        // Ignore keyword selection otherwise the selected keyword is not used
                        if (className?.includes(keywordRecommendationClass) ?? false) return;
                      }
                      setShowRecommendations(false);
                    }}
                    onFocus={() => {
                      setShowRecommendations(true);
                    }}
                    onChange={(e) => {
                      setInputError(undefined);
                      setShowRecommendations(true);
                      setName(e.target.value);
                    }}
                    fullWidth
                    value={name}
                    variant="outlined"
                    placeholder="Stichwort..."
                  />
                  <div style={{ position: 'absolute', bottom: '-60px', zIndex: 2, width: '100%' }}>
                    {recommendations && showRecommendations && recommendations.length > 0 && (
                      <List style={{ background: 'white' }}>
                        {recommendations.map((r) => {
                          return (
                            <ListItem
                              className={keywordRecommendationClass}
                              button
                              onClick={() => {
                                createKeyword(r.keyword, false);

                                setRecommendations(
                                  recommendations.filter((k) => r.keyword !== k.keyword),
                                );
                              }}
                            >
                              <ListItemText primary={r.keyword} />
                            </ListItem>
                          );
                        })}
                      </List>
                    )}
                  </div>
                </Grid>
                <Grid item>
                  <Button type="submit">+</Button>
                </Grid>
              </Grid>
              <Grid>
                {inputError && <p style={{ color: 'red', marginBottom: 0 }}>{inputError}</p>}
              </Grid>
            </Box>
          </form>
        );
      }}
    </Subscribe>
  );
}
