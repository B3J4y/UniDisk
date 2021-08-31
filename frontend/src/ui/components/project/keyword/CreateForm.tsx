import { Box, Button, Grid, List, ListItem, ListItemText, TextField } from '@material-ui/core';
import { Topic } from 'data/entity';
import { useProvider } from 'Provider';
import React, { useEffect } from 'react';
import { KeywordRecommedationResult } from 'remote/services/KeywordRecommendation';
import { Subscribe } from 'unstated-typescript';

export type CreateKeywordFormProps = {
  topicId: Topic['id'];
  topicName: string;
  topicKeywords: string[];
  disabled?: boolean;
};

export function CreateKeywordForm(props: CreateKeywordFormProps) {
  const { topicId, topicKeywords, topicName } = props;
  const provider = useProvider();
  const recommendationService = provider.getKeywordRecommendationService();
  const disabled = props.disabled ?? false;
  const [name, setName] = React.useState('');
  const [container, setContainer] = React.useState(provider.getKeywordDetailContainer());

  const [, setLoading] = React.useState(false);
  const [currentRecommendation, setCurrentRecommendation] = React.useState<
    KeywordRecommedationResult | undefined
  >(undefined);

  const [showRecommendations, setShowRecommendations] = React.useState(false);

  const [inputError, setInputError] = React.useState<string | undefined>(undefined);

  const search = async () => {
    if (name.trim().length < 3) {
      setCurrentRecommendation(undefined);
      return undefined;
    }
    try {
      const results = await recommendationService.search(topicName, name, undefined, topicKeywords);
      setCurrentRecommendation(results);
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };
  const recommendations = currentRecommendation?.recommendations.filter(
    (result) => !topicKeywords.includes(result.keyword),
  );
  useEffect(() => {
    setLoading(true);

    search();
  }, [name]);

  const keywordRecommendationClass = 'keyword-rec-item';

  return (
    <Subscribe to={[container]}>
      {(container) => {
        const createKeyword = async (
          {
            keyword,
            isSuggestion,
          }: {
            keyword: string;
            isSuggestion: boolean;
          },
          reset: boolean = false,
        ) => {
          await container.create({
            name: keyword.trim(),
            topicId,
            isSuggestion: isSuggestion,
          });

          const isRecommendation =
            currentRecommendation !== undefined &&
            currentRecommendation.recommendations.some((r) => r.keyword === keyword.trim());

          if (isRecommendation) {
            recommendationService.recommendationUsed({
              requestId: currentRecommendation!.id,
              keyword,
            });
          }

          if (container.state.save.isFinished) {
            if (reset) {
              setName('');
              setCurrentRecommendation(undefined);
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
              const isSuggestion =
                currentRecommendation?.recommendations.some(
                  (recommendation) => recommendation.keyword.trim() === name.trim(),
                ) ?? true;
              createKeyword(
                {
                  keyword: name,
                  isSuggestion,
                },
                true,
              );
            }}
          >
            <Box p={2}>
              <Grid container alignItems="center" spacing={2}>
                <Grid item style={{ position: 'relative', display: 'flex', flexGrow: 1 }}>
                  <TextField
                    disabled={disabled}
                    id="recommendation-input"
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
                  {recommendations && showRecommendations && recommendations.length > 0 && (
                    <div
                      style={{
                        position: 'absolute',
                        top: '64px',
                        zIndex: 2,
                        width: '94%',
                        maxHeight: '200px',
                        overflowY: 'auto',
                        border: '1px solid grey',
                        borderRadius: '4px',
                      }}
                    >
                      <List style={{ background: 'white', padding: 0 }}>
                        {recommendations.map((r) => {
                          return (
                            <ListItem
                              className={keywordRecommendationClass}
                              button
                              onClick={() => {
                                createKeyword(
                                  {
                                    keyword: r.keyword,
                                    isSuggestion: true,
                                  },
                                  false,
                                );

                                setCurrentRecommendation(currentRecommendation);
                                document.getElementById('recommendation-input')?.focus();
                              }}
                            >
                              <ListItemText primary={r.keyword} />
                            </ListItem>
                          );
                        })}
                      </List>
                    </div>
                  )}
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
