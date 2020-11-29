# Discord MP Bot

A simple discord bot that help tracks events of hololive.
The name `Discord MP Bot` is based on an old discord bot, might change that name after some time.


## Usage

* `>man`: Display manual pages (help) 
* `>holo <member> [list | schedules | live]`: Shows the current stream of a specified member.
	* `list` to display all hololive member ids used in this command.
	* `schedules` to list the schedules of today, requires holo_schedule_api.
	* `live` to list all members that are currently streaming on youtube.
* `>ping [ls | sl]`: Pong!
* `>roll`: Roll a dice, old function from 1st gen Discord MP Bot.
* `>bonk @member`: Bonk the mentioned member.
* `>about`: About Holo Bot.

## Setup

### YoutubeAPI key and Discord bot token
To run this bot, discord bot token needs to be placed in `config.properties` with the form:
```properties
token=
```

### Running the bot
```
java -jar discord_mp_bot.jar
```
If you do not with to activate twitter content broadcasting, do
```
java -jar discord_mp_bot.jar noTwitter
```

## Broadcasting Twitter content

Edit `config/broadcast.json` to subscribe to twitter content and broadcast it in designated text channel.

* `query`: The search query for twitter.
* `target`: Target channel ID

Example: 
```json
[
	{
		"query": "from:@sakuramiko25",
		"target": [757941157421645924, 757941157421645925]
	}
]
```

Also, keys and tokens for Twitter API needs to specified in `config/credentials/twitter4j.properties` with the form:
```properties
debug=false
oauth.consumerKey=
oauth.consumerSecret=
oauth.accessToken=
oauth.accessTokenSecret=
```

## Note
`>holo schedules` requires Holo Schedule API to run in local.
See more at [Holo Schedule API](https://github.com/cst0601/holo_schedule_api) repo.

