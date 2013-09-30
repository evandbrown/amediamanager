#! /bin/bash -e
cd ..
mvn clean
mvn package

# Upload war to bucket in multiple regions
regions=(us-east-1 us-west-2)
for var in "${regions[@]}"
do
	echo "Uploading to amm-${var}"
  aws s3 cp --acl public-read --region ${var} target/amediamanager.war s3://amm-${var}/public/
done
cd run-cfn