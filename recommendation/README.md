# UniDisk - Keyword Recommendation

Currently work in progress.

Model files are not tracked in version control because of their size (> 500MB).
Place model file called _fasttext.mode_ into the root directory of this service and run the application.

## Prerequisites

- Install pipenv

### API

Execute _run.sh_ to start the API. If you use a Windows machine, copy the command from the file and run it
in a terminal.

### Running with Docker

The model files are quite large and can cause Docker to run out of memory.
In case this happens, go to _Preferences > Resources > Advanced_ and increase the memory limit.

## Development

Docker doesn't do a lot at the moment so developing without it is a bit easier. To do this, change into the root directory
of this service and run `pipenv shell`. Afterwards the API can be started by running `python main.py`. However, keep in mind that Docker exposes the endpoint on port _8081_ whereas the server itself uses _8080_.
