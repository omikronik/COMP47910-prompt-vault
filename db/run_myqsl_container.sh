#!/bin/bash

# https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/docker-mysql-getting-started.html

docker run --name=mysql1 \
        --restart on-failure \
        -d \
        -p 3306:3306 \
        -v mysql1-data:/var/lib/mysql \
        -e MYSQL_ROOT_PASSWORD=yourpassword \
        container-registry.oracle.com/mysql/community-server:latest

