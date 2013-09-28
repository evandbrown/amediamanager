#! /bin/bash -e
mvn clean
mvn package
aws s3 cp --acl public-read --region us-east-1 target/amediamanager.war s3://evbrown/public/