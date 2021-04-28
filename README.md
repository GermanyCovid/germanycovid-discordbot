<p align="center">
  <a href="https://www.germanycovid.de">
    <img alt="atomicradio" src="https://i.imgur.com/tQEj7At.png" width="150" />
  </a>
</p>
<h1 align="center">
  Discord bot 🤖
</h1>
<p align="center">
  We offer you a daily overview of several data in order to keep you up to date with the latest data.<br>In addition to new infections and deaths, we also offer figures for intensive care units, as well as for the various states and counties.
</p>
<p align="center">
  <a href="https://github.com/GermanyCovid/germanycovid-api/actions">
      <img src="https://github.com/GermanyCovid/germanycovid-discordbot/actions/workflows/push_action.yml/badge.svg" alt="Workflow">
  </a>
</p>

## Commands 🎉
```
c!info - Find facts and information about the bot.
```
```
c!stats - See the most important data for Germany.
```
```
c!states <name or abbreviation> - Find the data for the states.
```
```
c!districts <name> - Find the data for the 412 counties or independent cities.
```
```
c!hospital - Find the most important data from the intensive care units.
```


## Admin Commands 💣
```
c!prefix <prefix> - Change the prefix
```
```
c!channel <#channel> - Specify a channel where only commands should be executable.
```

## Installation 🔌
#### Requirements
* Make sure that you have Java v8.x or later installed.
* You also need your bot's token. Check this <a href="https://anidiots.guide/getting-started/getting-started-long-version">page</a> for more information.

#### Installation
* Download our build discord bot jar from here <a href="https://github.com/GermanyCovid/germanycovid-discordbot/actions">https://github.com/GermanyCovid/germanycovid-discordbot/actions</a>
* Run `java -jar germanycovid-discordbot-X.X.X-dependencies.jar` from the project folder to start the bot.

#### Configuration
The `config.json` will be created when the bot is starting. In the `config.json` file you need your bot token that was mentioned in the prerequisites.<br>
`⚠️ The data in the config.json should not be published publicly otherwise third parties can gain access to all services that are used here. `

## Invite our bot to your guild 📨
We host our own bot. If the installation is too stressful for you or you want to set up the bot quickly, you can simply invite our bot to your guild. <a href="https://www.germanycovid.de/discord">Just press here if you want to invite our bot.</a>

## Credits 🔥
This project uses the data from the API <a href="https://github.com/marlon360/rki-covid-api">rki-covid-api</a> by <a href="https://github.com/marlon360">marlon360</a>. Be sure to check out his work on GitHub.

## License 📑
This code is available under the <a href="https://github.com/GermanyCovid/germanycovid-discordbot/blob/master/LICENSE">Mozilla Public License Version 2.0</a>.
