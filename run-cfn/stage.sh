#! /bin/bash -e

# Upload CFN template to regions
regions=(us-east-1 us-west-2 ap-northeast-1)

echo "Staging CloudFormation templates..."
for var in "${regions[@]}"
do
	echo "> amm-${var}"
  aws s3 cp --quiet --acl public-read --region ${var} amm-master.json s3://amm-${var}/public/
  aws s3 cp --quiet --acl public-read --region ${var} amm-app-env.json s3://amm-${var}/public/
done

echo "Staging Application WAR..."
cd ..
mvn -q clean package

# Upload war to bucket in multiple regions
for var in "${regions[@]}"
do
	echo "> amm-${var}"
  aws s3 cp --quiet --acl public-read --region ${var} target/amediamanager.war s3://amm-${var}/public/
done
cd run-cfn