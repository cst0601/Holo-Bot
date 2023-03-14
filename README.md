# Holo Bot

A simple discord bot that help tracks events of hololive, including Youtube
stream notification, tweet updates and more.

## Usage

* `>man`: Display manual pages (help) 
* `>holo <member> [list | schedules | live]`: Shows the current stream of a specified member.
	* `list` to display all hololive member ids used in this command.
	* `schedules` to list the schedules of today, requires holo_schedule_api.
	* `live` to list all members that are currently streaming on youtube.
* `>ping [ls | sl]`: Pong!
* `>roll <dice_size> <roll_number>`: Roll a dice, old function from 1st gen Discord MP Bot.
* `>bonk @member`: Bonk the mentioned member.
* `>about`: About Holo Bot.
* `>register <user_youtube_channel_id>`: Register your DiscordID and YoutubeID for Youtube member verification.
* `>member_verify`: Verify Youtube's channel membership status.

## Setup

### Discord bot token
To run this bot, discord bot token needs to be placed in `config.properties` with the form:
```properties
token=<your disocrd bot token>
twitter=
member_verification=
```

### Running the bot
```
java -jar discord_mp_bot.jar
```

### Member Verification
The bot supports Youtube membership verification and gives role to discord users.
The service is disabled in default, to enable the service, add `member_verification=true` in `config.properties`.

Moreover, MongoDB is required for member verificaiton to function. (Details not available yet)

### Broadcasting Twitter content
Tweet broadcasting is disabled in default, to enable the service, add `twitter=true` in `config.properties`.
Edit `config/broadcast.json` to subscribe to twitter content and broadcast it in designated text channel.

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

