import React from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import CircularProgress from '@material-ui/core/CircularProgress';

export type SearchFieldProps<T> = {
  fetcher: (value: string) => Promise<T[]>;
  getOptionLabel: (value: T) => string;
  getOptionSelected: (option: T, value: T) => boolean;
  noOptionsText?: string;
  onValueChanged: (value: T | undefined) => void;
  reset: (reset: () => void) => void;
  filter?: (entities: T[]) => T[];
};

export function SearchField<T>(props: SearchFieldProps<T>) {
  const {
    fetcher,
    getOptionLabel,
    getOptionSelected,
    noOptionsText,
    onValueChanged,
    filter,
  } = props;
  const [open, setOpen] = React.useState(false);
  const [options, setOptions] = React.useState<T[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [input, setInput] = React.useState('');

  const [error, setError] = React.useState<string | undefined>();

  const reset = () => {
    setInput('');
    if (filter) {
      setOptions(filter(options));
    }
  };
  props.reset(reset);

  const search = async (query: string) => {
    setLoading(true);
    setError(undefined);

    try {
      const results = await fetcher(query);
      setOptions(filter ? filter(results) : results);
    } catch (e) {
      setError('Fehler bei der Suche.');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (!open) {
      setOptions([]);
    }
  }, [open]);

  return (
    <Autocomplete
      id="asynchronous-demo"
      open={open}
      inputValue={input}
      onOpen={() => {
        setOpen(true);
      }}
      onClose={() => {
        setOpen(false);
      }}
      noOptionsText={noOptionsText}
      getOptionSelected={getOptionSelected}
      getOptionLabel={getOptionLabel}
      options={options}
      loading={loading}
      onChange={(event: any, newValue: T | null) => {
        onValueChanged(newValue ?? undefined);
      }}
      onInputChange={(value, query) => {
        setInput(query);
        search(query);
      }}
      renderInput={(params) => (
        <div>
          <TextField
            {...params}
            label="Gruppe..."
            variant="outlined"
            InputProps={{
              ...params.InputProps,
              endAdornment: (
                <React.Fragment>
                  {loading ? <CircularProgress color="inherit" size={20} /> : null}
                  {params.InputProps.endAdornment}
                </React.Fragment>
              ),
            }}
          />
          {error && <p>{error}</p>}
        </div>
      )}
    />
  );
}
