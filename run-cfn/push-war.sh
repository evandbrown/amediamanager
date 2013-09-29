#! /bin/bash -e
cd ..
mvn clean
mvn package
aws s3 cp --acl public-read --region us-east-1 target/amediamanager.war s3://evbrown/public/
cd run-cfn