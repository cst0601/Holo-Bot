# Holo Bot

## Table of Contents

- [About](#About)
- [Usage](#Usage)
  - [Discord Text Message Commands](#discord-text-message-commands)
  - [Non-Command Functions](#non-command-functions)
  - [Additional Info On YouTube Membership Verification](#additional-info-on-youtube-membership-verification)
- [Setup](#Setup)
  - [Setting Up Tokens](#setting-up-tokens)
    - [Discord](#discord)
	- [HoloDEX, YouTube, Twitter API Token](#holodex-youtube-twitter-api-token)
  - [Enabling/Disabling Some Features](#enablingdisabling-some-features)
  - [Member Verification](#member-verification)
  - [Broadcasting Twitter Content](#broadcasting-twitter-content)
  - [Holo Schedules](#holo-schedules)
  - [Running The Bot](#running-the-bot)
- [Contributing](#contributing)

## About

A simple discord bot that help tracks events of Hololive, including YouTube
stream notification, tweet updates and more.

(Edit: Thanks to Elon Musk, the support of twitter basically died.)

## Usage

### Discord Text Message Commands

* `>man`: Display manual pages (help).
* `>holo <member> [list | schedules | live]`: Shows the current stream of a
  specified member.
  * `list` to display all Hololive member ids used in this command.
  * `schedules` to list the schedules of today, required a running
	`holo_schedule_api` instance.
  * `live` to list all members that are currently streaming on youTube.
* `>ping [ls | sl]`: Pong!
* `>roll <dice_size> <roll_number>`: Roll a dice, old function from 1st gen
  Discord MP Bot.
* `>bonk <@member>`: Bonk the mentioned member.
* `>about`: About Holo Bot.
* `>register <user_youtube_channel_id>`: Register your DiscordID and YouTube ID
  for YouTube member verification.
* `>member_verify`: Verify YouTube's channel membership status. See [member
  verification](#member-verification) to enable this function and [additional
  information](#additional-info-on-youtube-membership-verification) for more
  details.

> There exists some undocumented commands in the bot, will be updated or
> deleted in the near future.

### Non-Command Functions

* Auto Twitter URL translation: Since nowadays URL that starts with
  `twitter.com` or `x.com` will not have previews in Discord, the bot
  automatically detects whenever a user posts a twitter or x URL and translates
  it into `vxtwitter` URL to show previews.
* Event notification: Notifies a specified `@group` whenever the designated
  Hololive member opens or starts a stream on YouTube.
* Tweet update: Basically dead because of some guy at Twitter.

### Additional Info On YouTube Membership Verification

> Basically, "How does the verification works?"

This member verification system first will require to register your YouTube ID
with the bot. Then you leave a message in the designated free chat. After you
came back to the bot and issue the `>member_verify` command, the bot will search
for your message and use it to check if your membership is active.

## Setup

Unless otherwise stated, all configuration files need to be placed under the
`config` directory.

### Setting Up Tokens

#### Discord

Discord bot token needs to be placed in `config.properties` with the form:
```properties
token=<your disocrd bot token>
twitter=<true|false>
member_verification=<true|false>
```

#### HoloDex, YouTube, Twitter API token

HoloDex, YouTube and Twitter API tokens should be placed in
`/config/credentials` directory with the following filename and format:

(I do know this looks like a mess, but I didn't realized until when I'm updating
this readme. I promise I will do something.)
* `holodex.json`
```json
{
	"key": <your holodex api key>
}
```
* `youtube_api.json`
```json
{
	"youtube-api-key": <your youtube api key>
}
```
* `twitter4j.properties`
```
debug=false
oauth.consumerKey=
oauth.consumerSecret=
oauth.accessToken=
oauth.accessTokenSecret=
```

### Enabling/Disabling Some Features

YouTube member verification service and features related to Twitter could be
disabled in `config.properties` file simply by changing the field value to
`false`.

```
twitter=false
member_verification=false
```

### Member Verification

YouTube member verification requires a MongoDB to run alone with to bot for
storing user information (YouTube ID, membership status, renew date).

```json
{
    "db_name": "name of your db",
    "discord_role_id": "your discord role id for verified member",
    "yt_live_stream_chat_id": "Cg0KC3c0ZGdxbF81UnprKicKGFVDLWhNNllKdU5ZVkFtVVd4ZUlyOUZlQRILdzRkZ3FsXzVSems",
    "db_connection": "mongodb://localhost:27017"
}
```

### Broadcasting Twitter content

Tweet broadcasting is disabled in default, to enable the service, add
`twitter=true` in `config.properties`. Edit `config/broadcast.json` to subscribe
to twitter content and broadcast it in designated text channel.

* `query`: The search query for twitter.
* `target`: Target channel ID(s)

Example:
```json
[
	{
		"query": "from:@sakuramiko25",
		"target": [757941157421645924, 757941157421645925]
	}
]
```

### Holo Schedules

`>holo schedules` requires Holo Schedule API to run in local. See more at [Holo
Schedule API](https://github.com/cst0601/holo_schedule_api) repo.

### Running the bot
```
java -jar holo_bot.jar
```

## Contributing

You're welcomed to do so either by sending over pull requests or submitting feed
backs, feature requests in issue.