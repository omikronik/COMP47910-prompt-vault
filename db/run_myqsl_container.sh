#!/bin/bash

# https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/docker-mysql-getting-started.html

docker run --name=mysql1 --restart on-failure -d container-registry.oracle.com/mysql/community-server:latest
