#! /bin/bash -e

# Upload CFN template to regions
regions=(us-east-1 us-west-2)
for var in "${regions[@]}"
do
	echo "Uploading to amm-${var}"
  aws s3 cp --acl public-read --region ${var} launch-app.json s3://amm-${var}/public/
done