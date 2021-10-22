/*
 * Copyright 2020 Marco Bignami.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.unknowndomain.alea.bot;

import net.unknowndomain.alea.GenericListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.unknowndomain.alea.command.BasicCommand;
import net.unknowndomain.alea.command.Command;
import net.unknowndomain.alea.command.PrintableOutput;
import net.unknowndomain.alea.expr.ExpressionCommand;
import net.unknowndomain.alea.messages.MsgBuilder;
import net.unknowndomain.alea.messages.ReturnMsg;
import net.unknowndomain.alea.parser.PicocliParser;
import net.unknowndomain.alea.roll.GenericResult;
import net.unknowndomain.alea.settings.GuildConfigCommand;
import net.unknowndomain.alea.settings.GuildSettings;
import net.unknowndomain.alea.settings.SettingsRepository;
import net.unknowndomain.alea.systems.ListSystemsCommand;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import net.unknowndomain.alea.systems.RpgSystemOptions;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class AleaListener extends GenericListener implements MessageCreateListener
{
    public static final String PREFIX = "!alea";
    private static final Pattern PATTERN = Pattern.compile("^(" + PREFIX + ")(( +)(?<parameters>.*))?$");
    
    private static final List<BasicCommand> AVAILABLE_COMMANDS = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AleaListener.class);
    
    private final SettingsRepository settingsRepository;
    private final List<Command> SETTINGS_COMMANDS = new ArrayList<>();
    
    static {
        AVAILABLE_COMMANDS.add(new ListSystemsCommand());
        AVAILABLE_COMMANDS.add(new ExpressionCommand());
    }
    
    public AleaListener(SettingsRepository settingsRepository, UUID namespace)
    {
        super(namespace);
        this.settingsRepository = settingsRepository;
        SETTINGS_COMMANDS.add(new GuildConfigCommand(settingsRepository));
    }
    
    @Override
    public void onMessageCreate(MessageCreateEvent event)
    {
        Matcher checkPrefix = PATTERN.matcher(event.getMessageContent());
        if (checkPrefix.matches()) {
            Locale locale = Locale.ENGLISH;
            LOGGER.debug("Default Locale: {}", locale);
            Long guildId = null;
            if (event.getServer().isPresent())
            {
                guildId = event.getServer().get().getId();
                Optional<GuildSettings> guildSettings = settingsRepository.loadGuildSettings(guildId);
                if (guildSettings.isPresent())
                {
                    LOGGER.debug("GuildSettings found");
                    locale = guildSettings.get().getLanguage();
                }
            }
            boolean guildAdmin = event.getMessageAuthor().isServerAdmin();
            LOGGER.debug("Locale: {}", locale);
            String params = checkPrefix.group("parameters"); 
            if (params == null || params.isEmpty() || params.startsWith("help"))
            {
                printHelp(event.getChannel(), guildAdmin);
            }
            else
            {
                MessageBuilder builder = new MessageBuilder();
                MessageAuthor author = event.getMessageAuthor();
//                Optional<Long> callerId = readUserId(author);
                Optional<UUID> callerId = buildCallerId(author);
                builder.replyTo(event.getMessageId());
                
                Optional<Command> parsedCmd = parseCommand(params);
                
                if (parsedCmd.isPresent())
                {
                    Command cmd = parsedCmd.get();
                    ReturnMsg msg;
                    LOGGER.debug("guildAdmin:", guildAdmin);
                    LOGGER.debug("guildId:", guildId);
                    if ((cmd instanceof GuildConfigCommand) && (guildId != null) && (guildAdmin))
                    {
                        GuildConfigCommand gcc = (GuildConfigCommand) cmd;
                        msg = gcc.execCommand(params, guildId);
                    }
                    else
                    {
                        msg = runCommand(cmd, params, locale, callerId);
                    }
                    MsgFormatter.appendMessage(builder, msg);
                    builder.send(event.getChannel());
                }
                else
                {
                    builder.append("Error: command not available");
                    builder.send(event.getChannel());
                    printHelp(event.getChannel(), guildAdmin);
                }
            }
        }
    }
    
    private ReturnMsg runCommand(Command cmd, String params, Locale locale, Optional<UUID> callerId)
    {
        MsgBuilder bld = new MsgBuilder();
        ReturnMsg msg = bld.append("Error").build();
        if (cmd instanceof RpgSystemCommand)
        {
            RpgSystemCommand rpg = (RpgSystemCommand) cmd;
            RpgSystemOptions options = rpg.buildOptions();
            Pattern sysPattern = Pattern.compile("^(?<" + Command.CMD_NAME + ">" + rpg.getCommandRegex() + ")(( )(?<" + Command.CMD_PARAMS + ">.*))?$");
            Matcher sysFilter = sysPattern.matcher(params);
            if (sysFilter.matches())
            {
                String sysArgs = sysFilter.group(Command.CMD_PARAMS);
                if (sysArgs != null)
                {
                    PicocliParser.parseArgs(options, sysArgs.split(" "));
                }
                else
                {
                    PicocliParser.parseArgs(options, "-h");
                }
                Optional<GenericResult> ret = rpg.execCommand(options, locale, callerId);
                if (ret.isPresent())
                {
                    msg = ret.get().buildMessage();
                }
                else
                {
                    msg = PicocliParser.printHelp(sysFilter.group(Command.CMD_NAME), options, locale);
                }
            }
        }
        if (cmd instanceof BasicCommand)
        {
            BasicCommand basic = (BasicCommand) cmd;
            Optional<PrintableOutput> ret = basic.execCommand(params, callerId);
            if (ret.isPresent())
            {
                msg = ret.get().buildMessage();
            }
            else
            {
                msg = basic.printHelp(locale);
            }
        }
        return msg;
    }
    
    private Optional<Command> parseCommand(String parameters)
    {
        for (Command cmd : AVAILABLE_COMMANDS)
        {
            if (cmd.checkCommand(parameters))
            {
                return Optional.of(cmd);
            }
        }
        for (Command cmd : SETTINGS_COMMANDS)
        {
            if (cmd.checkCommand(parameters))
            {
                return Optional.of(cmd);
            }
        }
        for (Command cmd : RpgSystemCommand.LOADER)
        {
            if (cmd.checkCommand(parameters))
            {
                return Optional.of(cmd);
            }
        }
        
        return Optional.empty();
    }
    
    private void printHelp(TextChannel channel, boolean guildAdmin)
    {
        MessageBuilder output = new MessageBuilder();
        StringBuilder sb = new StringBuilder("Usage: ").append("!alea <command> <params>\n");
        sb.append("Commands:\n");
        sb.append(StringUtils.rightPad("   help  ", 20)).append(" | Print this help").append("\n");
        sb.append(StringUtils.rightPad("   system", 20)).append(" | Print the list of all available systems and their commands").append("\n");
        sb.append(StringUtils.rightPad("   expr <expression>", 20)).append(" | Solve the dice expression (example: 1d8+2d4-1d6+15-7)").append("\n");
        sb.append(StringUtils.rightPad("   <system> <params>", 20)).append(" | Roll a system specific roll (see <system> --help)").append("\n");
        if (guildAdmin)
        {
            sb.append(StringUtils.rightPad("   guild-config", 20)).append(" | (WIP) Set config variables for the current guild").append("\n");
        }
        output.append(sb.toString(), MessageDecoration.CODE_LONG);
        output.send(channel);
    }
    
}
