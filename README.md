# azure-sdk

## Introduction

This is an API plugin for Azure libraries. Users should not install this directly

## Configuration

It is possible to configure some settings with Java System Properties, specified with `-Dproperty=value` in the command line.

### Http Client

* `io.jenkins.plugins.azuresdk.HttpClientRetriever.readTimeoutSeconds` - This sets the read timeout (in seconds) of the HTTP client used for Azure API requests.
* `io.jenkins.plugins.azuresdk.HttpClientRetriever.responseTimeoutSeconds` - This sets the response timeout (in seconds) of the HTTP client used for Azure API requests.
* `io.jenkins.plugins.azuresdk.HttpClientRetriever.writeTimeoutSeconds` - This sets the write timeout (in seconds) of the HTTP client used for Azure API requests.

## Contributing

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)
