#!/bin/bash

# Creates unidisc core in docker container.
# Fails if core already exists.

docker exec -it unidisk_solr_1 solr create_core -c unidisc