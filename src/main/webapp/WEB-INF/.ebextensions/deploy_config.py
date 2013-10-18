#!/bin/env python
"""
This script attempts to load configuration found in the OS environment into S3.
It checks S3 to see if the configuration file already exists and exits if so.
If the config filed does not exist, the script scans the OS environ for vars
with a specific prefix, merges them into a key=val ini file and uploads
the file to S3.

The script assumes the existence of S3_CONFIG_BUCKET and S3_CONFIG_KEY env vars
to point to the S3 bucket and key that should hold the configuration.
"""
import os
import sys

import boto
from boto.s3.key import Key

# Env vars that hold pointers
S3_CONFIG_BUCKET = os.environ['S3_CONFIG_BUCKET']
S3_CONFIG_KEY = os.environ['S3_CONFIG_KEY']

# Prefix for env vars that hold config
ENV_VAR_PREFIX = 'AMM_'

def get_conf():
    config = []
    
    # Look for configuration in S3
    s3 = boto.connect_s3()
    cfg_bucket = s3.lookup(S3_CONFIG_BUCKET)
    
    # Fail if bucket doesn't exist
    if cfg_bucket is None:
        print "Could not find bucket " + S3_CONFIG_BUCKET
        sys.exit(-1)
        
    # Bucket exists; look for config file in bucket
    if(cfg_bucket.get_key(S3_CONFIG_KEY)):
      print "Config file found at s3://%s/%s. Exiting\n" % (S3_CONFIG_BUCKET, S3_CONFIG_KEY)
      sys.exit(0)
      
    # Retrieve env vars with AMM_ prefix
    print "Config file not found in S3"
    print "Scanning env for config vars prefixed with " + ENV_VAR_PREFIX
    for var in os.environ:
        if str(var).startswith(ENV_VAR_PREFIX):
            config.append("%s=%s" % (var.replace(ENV_VAR_PREFIX, ''), os.environ[var]))
    config_string = "\n".join(config)
    print "Got config:\n" + config_string
    
    # Store config in S3
    k = Key(cfg_bucket)
    k.key = S3_CONFIG_KEY
    k.set_contents_from_string(config_string)
    print "Config file stored at s3://%s/%s. Exiting\n" % (S3_CONFIG_BUCKET, S3_CONFIG_KEY)
    sys.exit(0)
    
if __name__ == "__main__":
    get_conf()