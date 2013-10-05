import os


def generateConfFile():
    currentConfig = []
    configPath = '/var/amm-web/'
    if not os.path.exists(configPath):
        os.makedirs(configPath)
    for var in os.environ:
        if str(var).startswith('AMM_'):
            print var.replace('AMM_', '')
            print os.environ[var]
            currentConfig.append("%s=%s" % (var.replace('AMM_', ''), os.environ[var]))
    with open(configPath + 'config', 'w') as outfile:
        for item in currentConfig:
            outfile.write('%s\n' % item)


if __name__ == "__main__":
    generateConfFile()
