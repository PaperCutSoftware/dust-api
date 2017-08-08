# DUST: Device Usage Tracker API

DUST is a tool to track the usage of a fleet of devices in a shared environment. 

With DUST, you can:
 
  * Add new devices, providing descriptive information on them, such:
    * hostname
    * brand
    * model
    * credentials
    * photo
    * ip
    * nickname
    * and more...
  * Claim a device as a user to let others know which device is in use.
  * Integrate a Slack bot with a Slack channel for group communication and convenient commands 
    (like */claim &lt;device&gt;*). 

> Made for PaperCut [Constructival 2016][1]

## Build Setup

Pre-requisites:

* [Maven](https://maven.apache.org/)

``` bash
# run a local instance on port 9090
mvn exec:java
```

## Configuration

Add your own `configuration.yml` based on `configuration.yml.dist` on the top level of the project.
```
slackConfig
    configuration for the Slack integration
```
```
authConfig
    configuration for the user authentication
```
```
server
    configuration for the server app and admin connectors
```
```
logoging
    configuration for the logger
```

## Slack Integration

Edit `configuration.yml` and set the values for `slackConfig`
You will need to configure a [custom bot user][2] and [slash commands][3] for the channels being used.

### Bot

```
name: <slackConfig.botUsername>
API token: <slackConfig.botToken>
```

### Slash commands
```
names: /claim, /unclaim, /list and /show
Token: <slackConfig.commandVerificationTokens> (You can specify more than one)
```

(post as `<slackConfig.botUsername>`)

## Deploying Changes

Pre-requisites:

* [Docker](https://www.docker.com/)

### Build the container

```
mvn clean package docker:build
```



[1]: https://blog.papercut.com/blog/2016/11/22/constructival-papercuts-first-global-hackathon/
[2]: https://api.slack.com/bot-users
[3]: https://api.slack.com/slash-commands
