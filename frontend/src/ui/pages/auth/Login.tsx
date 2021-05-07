import { Box, Card, CircularProgress } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import InputAdornment from '@material-ui/core/InputAdornment';
import InputLabel from '@material-ui/core/InputLabel';
import OutlinedInput from '@material-ui/core/OutlinedInput';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import clsx from 'clsx';
import { THEME } from 'config';
import { History, Location } from 'history';
import React, { Component } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { Subscribe } from 'unstated-typescript';
import { UserContainer } from '../../../model/LoginState';
import { Center } from '../../components/util/Center';

type LoginState = {
  email: string;
  password: string;
  showPassword: boolean;
};

type Props = {
  userContainer: UserContainer;
  history: History;
  location: Location<{ originalLocation?: string }>;
  originalLocation?: string;
};

function HigherOrderLogin(props: { userContainer: UserContainer }): JSX.Element {
  const history = useHistory();
  const location = useLocation<{ originalLocation?: string }>();

  const { userContainer } = props;

  return (
    <Box
      bgcolor={THEME.primary}
      height="100%"
      width="100%"
      display="flex"
      alignItems="center"
      justifyContent="center"
    >
      <Center
        child={
          <div>
            <Box mb={6}>
              <h1 style={{ color: 'white' }}>Unidisk</h1>
            </Box>
            <Card>
              <LoginForm userContainer={userContainer} history={history} location={location} />
            </Card>
          </div>
        }
      />
    </Box>
  );
}

class LoginForm extends Component<Props, LoginState> {
  classes: any;

  constructor(props: Props) {
    super(props);

    this.state = {
      email: '',
      password: '',
      showPassword: false,
    };

    this.classes = makeStyles((theme) => ({
      root: {
        display: 'flex',
        flexWrap: 'wrap',
      },
      margin: {
        margin: theme.spacing(1),
      },
      withoutLabel: {
        marginTop: theme.spacing(3),
      },
      textField: {
        width: '25ch',
      },
    }));
  }

  componentDidUpdate(prevProps: Props): void {
    if (this.props.userContainer.isAuthorized) {
      this.onLogin();
    }
  }

  private onLogin = (): void => {
    const pathname = this.props.location.state.originalLocation;

    const { from } = { from: { pathname: pathname ?? '/' } };
    this.props.history.replace(pathname === '/login' ? { pathname: '/' } : from);
  };

  updateEmail = (event: any) => {
    const { value } = event.target;
    const { state } = this;
    this.setState({
      ...state,
      email: value,
    });
  };

  toggleVisibility = (event: any): void => {
    this.setState({
      ...this.state,
      showPassword: !this.state.showPassword,
    });
  };

  updatePassword = (event: any): void => {
    const { value } = event.target;
    this.setState({
      ...this.state,
      password: value,
    });
  };

  login = (): void => {
    this.props.userContainer.login(this.state.email, this.state.password);
  };

  onSubmit = (e): void => {
    e.preventDefault();
    this.login();
  };

  render(): JSX.Element {
    const { state } = this;
    const classNames = clsx(this.classes.margin, this.classes.textField);
    const { userContainer } = this.props;
    const userState = this.props.userContainer.state;

    return (
      <div className={this.classes.root}>
        <Box pt={2} pb={2} pr={4} pl={4}>
          <h1>Login</h1>
          <form
            onSubmit={this.onSubmit}
            onKeyPress={(e) => {
              if (e.which === 13 /* Enter */) {
                e.preventDefault();
              }
            }}
          >
            <Box mb={2}>
              <TextField
                id="outlined-basic"
                label="E-Mail"
                variant="outlined"
                className={classNames}
                onChange={this.updateEmail}
                fullWidth={true}
                required
              />
            </Box>
            <Box mb={4}>
              <FormControl className={classNames} variant="outlined">
                <InputLabel htmlFor="standard-adornment-password">Passwort</InputLabel>
                <OutlinedInput
                  id="standard-adornment-password"
                  type={state.showPassword ? 'text' : 'password'}
                  value={state.password}
                  onChange={this.updatePassword}
                  labelWidth={70}
                  required
                  fullWidth={true}
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                        onClick={this.toggleVisibility}
                        aria-label="toggle password visibility"
                      >
                        {state.showPassword ? <Visibility /> : <VisibilityOff />}
                      </IconButton>
                    </InputAdornment>
                  }
                />
              </FormControl>
            </Box>

            {userContainer.isAuthenticated && !userContainer.isAuthorized && (
              <p>{'Nicht authorisiert.'}</p>
            )}

            {userState.user.hasError ? <p>{`${userState.user.error!}`}</p> : null}
            {userState.user.isLoading ? (
              <CircularProgress />
            ) : (
              <Button
                variant="contained"
                color="primary"
                onClick={this.login}
                className={clsx(this.classes.margin)}
              >
                Anmelden
              </Button>
            )}
          </form>
        </Box>
      </div>
    );
  }
}

export const StatedLoginForm = (): JSX.Element => (
  <Subscribe to={[UserContainer]}>
    {(c): JSX.Element => {
      return <HigherOrderLogin userContainer={c} />;
    }}
  </Subscribe>
);
