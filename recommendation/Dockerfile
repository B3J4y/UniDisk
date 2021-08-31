# Pull base image
FROM python:3.7
# Set environment varibles
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
WORKDIR /src/
# Install dependencies
RUN pip install pipenv
COPY Pipfile Pipfile.lock /src/
RUN pipenv install --system --dev
COPY . /src/
EXPOSE 8000
CMD ["python", "main.py"]