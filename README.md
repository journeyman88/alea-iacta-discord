# alea-iacta-est
Alea Iacta Est is a Discord bot designed to help running RPG sessions over the network.

It can be added to your server using this link:

https://discord.com/oauth2/authorize?client_id=777835776757989427&scope=bot&permissions=0

The bot supports system specific rolls and also has an expression solver to work on more generic rolls.
The bot will listen to messages prefixed by `!alea`, and can also be configured to listen on the supported system-specific `!bang`: for example `!7s2` is a shortcut for `!alea 7s2` or `!alea 7th-sea-2nd`. In the official instance this behaviour is enabled by default. 

## Expression Engine
The expression engine is built to be used as a simple dice roller and can be invoked using the `!alea expr <expression>` command.
It can solve compounded expression in algebrical sum form, and can interpret - beside simple integer constants - the following notations:

- **Simple Dice notation** *NdX*, for example 2d8 - which means roll two dice each with eight faces.
- **Roll'n'Keep (upper) notation** *NdXkY*, for example 10d6k5 - which means roll ten dice each with six faces, then sum together the 5 highest values
- **Roll'n'Keep (lower) notation** *NdXlY*, for example 10d6l5 - which means roll ten dice each with six faces, then sum together the 5 lowest values
- **Roll Above notation** *NdX/Y*, for example 5d10/7 - which means roll five dice each with ten faces, then count those with a value higher than, or equal to seven
- **Roll Below notation** *NdX\Y*, for example 3d20\13 - which means roll three dice each with twenty faces, then count those with a value lesser than, or equal to thirteen

For example, a perfecly valid expression could be: `4d4/3  + 10d6k5 - 1d8 - 1d12\5 +7` which could solve to anything between 3 and 40.

## Supported systems
The system-specific code is isolated in several plugins, as such the best way to know which systems the instance can support is using the `!alea system` command, which reports a list of systems and the relative commands (generally two, a 3 character shortcut and a long command).

At the moment of writing the systems supported are the following:

| System  | Shortcut  | Command  |
| :------------ | :------------ | :------------ |
| 7th Sea 2nd Edition  | `!alea 7s2`  | `!alea 7th-sea-2nd`  |
| AGE System  | `!alea age`  | `!alea age-system`  |
| Blacksad RPG  | `!alea sad`  | `!alea blacksad`  |
| Brass Age  | `!alea ba1`  | `!alea brass-age`  |
| Klothos (Thread System)  | `!alea kts`  | `!alea klothos`  |
| Lex Arcana 2nd Edition  | `!alea lex`  | `!alea lex-arcana-2nd`  |
| Not The End  | `!alea nte`  | `!alea not-the-end`  |
| Project H.O.P.E. 2nd Edition  | `!alea ph2`  | `!alea project-hope-2nd`  |
| Shadowrun 5th Edition  | `!alea sr5`  | `!alea shadowrun-5th`  |
| Shintiara RPG (Modern d100)  | `!alea shn`  | `!alea shintiara`  |
| The Dark Eye 5th Edition  | `!alea da5`  | `!alea the-dark-eye-5th`  |
| Vampire the Masquerade 5th Edition  | `!alea vt5`  | `!alea vampire-5th`  |
| Walkthrough RPG  | `!alea wkt`  | `!alea walkthrough`  |

For system-specific syntax and usage you can run the `!alea <system> -h` command or check in the github wiki for the system-specific page.


### Next implementations in line:

- All systems: fix the descriptors for the <system> help to fit the system (to kickstart the impl was copied from 7th Sea)
- Guild Language: add stateful configuration, to allow guilds set the language to use for their messages for commands that support i18n 
- Blacksad RPG : roll modifiers
- Knight of the Round Academy : base roll

## Run-It-Yourself
Aside from the official instance, you can run yourself an instance of the bot, simply by building the code and run it with the command line argument `--discordToken <YOUR_TOKEN_HERE>`, also if you want enable the system-specifics `!bang` you should also add the `--systemListener` parameter.

You can also exclude systems that you're not interested into, simply by removing the appropriate jar from classpath before running it.
