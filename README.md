# Discord MP Bot

A simple discord bot that help tracks events of hololive
The name `Discord MP Bot` is based on an old discord bot, might change that namge after some time.


## Usage

* `>man`: Display manual pages (help) 
* `>holo <member> [list | schedules | live]`: Shows the current stream of a specified member.
	* `list` to display all hololive member ids used in this command.
	* `schedules` to list the schedules of today, requires holo_schedule_api.
	* `live` to list all members that are currently streaming on youtube.
* `>ping [ls | sl]`: Pong!
* `>roll`: Roll a dice, old function from 1st gen Discord MP Bot

## Setup

### YoutubeAPI key and Discord bot token
To run this bot, youtube key and discord bot token needs to be placed in `config.properties` with the form:
```properties
token=
yt_key=
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

Example: 
```json
[
	{
		"query": "from:@sakuramiko25",
		"target": [channel_id_1, channel_id_2]
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
`>holo schedules` requires holo_schedule_api to run in local.

