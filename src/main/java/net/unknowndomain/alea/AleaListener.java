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
package net.unknowndomain.alea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.unknowndomain.alea.command.Command;
import net.unknowndomain.alea.expr.ExpressionCommand;
import net.unknowndomain.alea.messages.ReturnMsg;
import net.unknowndomain.alea.systems.ListSystemsCommand;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

/**
 *
 * @author journeyman
 */
public class AleaListener implements MessageCreateListener
{
    public static final String PREFIX = "!alea";
    private static final Pattern PATTERN = Pattern.compile("^(" + PREFIX + ")(( +)(?<parameters>.*))?$");
    private static final List<Command> AVAILABLE_COMMANDS = new ArrayList<>();
    
    static {
        AVAILABLE_COMMANDS.add(new ListSystemsCommand());
        AVAILABLE_COMMANDS.add(new ExpressionCommand());
    }
    
    @Override
    public void onMessageCreate(MessageCreateEvent event)
    {
        Matcher checkPrefix = PATTERN.matcher(event.getMessageContent());
        if (checkPrefix.matches()) {
            String params = checkPrefix.group("parameters"); 
            if (params == null || params.isEmpty() || params.startsWith("help"))
            {
                printHelp(event.getChannel());
            }
            else
            {
                MessageBuilder builder = new MessageBuilder();
                Optional<Long> callerId = readUserId(event.getMessageAuthor());
//                MessageAuthor author = event.getMessageAuthor();
//                if (author.isUser() && !event.isPrivateMessage() && author.asUser().isPresent())
//                {
//                    builder.append(author.asUser().get()).appendNewLine();
//                }
                Optional<Command> parsedCmd = parseCommand(params);
                
                if (parsedCmd.isPresent())
                {
                    ReturnMsg msg = parsedCmd.get().execCommand(params, callerId);
                    MsgFormatter.appendMessage(builder, msg);
                    builder.send(event.getChannel());
                }
                else
                {
                    builder.append("Error: command not available");
                    builder.send(event.getChannel());
                    printHelp(event.getChannel());
                }
            }
        }
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
        for (Command cmd : RpgSystemCommand.LOADER)
        {
            if (cmd.checkCommand(parameters))
            {
                return Optional.of(cmd);
            }
        }
        
        return Optional.empty();
    }
    
    private Optional<Long> readUserId(MessageAuthor author)
    {
        Optional<Long> retVal = Optional.empty();
        if (author.isUser() && author.asUser().isPresent())
        {
            User discordUser = author.asUser().get();
            Optional.of(discordUser.getId());
        }
        return retVal;
    }
    
    private void printHelp(TextChannel channel)
    {
        MessageBuilder output = new MessageBuilder();
        StringBuilder sb = new StringBuilder("Usage: ").append("!alea <command> <params>\n");
        sb.append("Commands:\n");
        sb.append(StringUtils.rightPad("   help  ", 20)).append(" | Print this help").append("\n");
        sb.append(StringUtils.rightPad("   system", 20)).append(" | Print the list of all available systems and their commands").append("\n");
        sb.append(StringUtils.rightPad("   expr <expression>", 20)).append(" | Solve the dice expression (example: 1d8+2d4-1d6+15-7)").append("\n");
        sb.append(StringUtils.rightPad("   <system> <params>", 20)).append(" | Roll a system specific roll (see <system> --help)").append("\n");
        output.append(sb.toString(), MessageDecoration.CODE_LONG);
        output.send(channel);
    }
    
}
