#!/bin/bash

# Check if .env file exists and source it
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Verify required environment variables
if [ -z "$CV_ADMIN_USERNAME" ] || [ -z "$CV_ADMIN_PASSWORD" ]; then
    echo "Error: Required environment variables are not set."
    echo "Please set CV_ADMIN_USERNAME and CV_ADMIN_PASSWORD"
    exit 1
fi

# Run the application
java -jar target/cv-site-1.0-SNAPSHOT.jar 