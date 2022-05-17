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
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.unknowndomain.alea.command.Command;
import net.unknowndomain.alea.parser.PicocliParser;
import net.unknowndomain.alea.roll.GenericResult;
import net.unknowndomain.alea.settings.GuildSettings;
import net.unknowndomain.alea.settings.SettingsRepository;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import net.unknowndomain.alea.systems.RpgSystemOptions;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

/**
 *
 * @author journeyman
 */
public class SystemListener extends GenericListener implements MessageCreateListener
{
    private final Pattern PATTERN; 
    
    private final RpgSystemCommand system;
    private final SettingsRepository settingsRepository;
    
    public SystemListener(RpgSystemCommand system, SettingsRepository settingsRepository, UUID namespace)
    {
        super(namespace);
        this.system = system;
        this.settingsRepository = settingsRepository;
        PATTERN = Pattern.compile("^!(?<" + Command.CMD_NAME + ">" + system.getCommandRegex() + ")(( )(?<" + Command.CMD_PARAMS + ">.*))?$");
    }
    
    @Override
    public void onMessageCreate(MessageCreateEvent event)
    {
        Matcher checkPrefix = PATTERN.matcher(event.getMessageContent());
        if (checkPrefix.matches()) {
            Locale locale = Locale.ENGLISH;
            if (event.getServer().isPresent())
            {
                Long guildId = event.getServer().get().getId();
                Optional<GuildSettings> guildSettings = settingsRepository.loadGuildSettings(guildId);
                if (guildSettings.isPresent())
                {
                    locale = guildSettings.get().getLanguage();
                    if (!guildSettings.get().getSystems().get(system.getCommandDesc().getCommand()).isEnabled())
                    {
                        return;
                    }
                }
            }
            MessageBuilder builder = new MessageBuilder();
            String cmdLine = event.getMessageContent().substring(1);
            RpgSystemOptions options = system.buildOptions();
            String args = checkPrefix.group(Command.CMD_PARAMS);
            if (args != null)
            {
                PicocliParser.parseArgs(options, args.split(" "));
            }
            else
            {
                PicocliParser.parseArgs(options, "-h");
            }
            MessageAuthor author = event.getMessageAuthor();
//            Optional<Long> callerId = readUserId(author);
            Optional<UUID> callerId = buildCallerId(author);
            builder.replyTo(event.getMessageId());
//            if (author.isUser() && !event.isPrivateMessage() && author.asUser().isPresent())
//            {
//                builder.append(author.asUser().get()).appendNewLine();
//            }
            Optional<GenericResult> res = system.execCommand(options, locale, callerId);
            if (res.isPresent())
            {
                MsgFormatter.appendMessage(builder, res.get().buildMessage());
            }
            else
            {
                MsgFormatter.appendMessage(builder, PicocliParser.printHelp(checkPrefix.group(Command.CMD_NAME), options, locale));
            }
            builder.send(event.getChannel());
            
        }
    }
    
}
