/*
 * Copyright 2021 Marco Bignami.
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
package net.unknowndomain.alea.settings;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Optional;
import net.unknowndomain.alea.command.Command;
import net.unknowndomain.alea.messages.MsgBuilder;
import net.unknowndomain.alea.messages.MsgStyle;
import net.unknowndomain.alea.messages.ReturnMsg;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class GuildConfigCommand extends Command
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildConfigCommand.class);
    private static final Options CMD_OPTIONS = new Options();
    private static final CommandLineParser PARSER = new DefaultParser();
    
    static {
        OptionGroup group = new OptionGroup();
        group.addOption(
                Option.builder()
                        .longOpt("get-lang")
                        .desc("Gets the guild language used by the RPG systems")
                        .build()
        );
        group.addOption(
                Option.builder()
                        .longOpt("set-lang")
                        .desc("Sets the guild language used by RPG systems")
                        .hasArg()
                        .argName("language")
                        .build()
        );
        group.addOption(
                Option.builder()
                        .longOpt("clear")
                        .desc("Remove all guilds stored data")
                        .build()
        );
        CMD_OPTIONS.addOptionGroup(group);
    }
    
    private final SettingsRepository settingsRepo;
    
    public GuildConfigCommand(SettingsRepository settingsRepo)
    {
        this.settingsRepo = settingsRepo;
    }
    

    @Override
    protected String getCommandRegex()
    {
        return "guild\\-config";
    }

    public ReturnMsg execCommand(String cmdLine, Long guildId)
    {
        MsgBuilder builder = new MsgBuilder();
        try
        {
            CommandLine cmd = PARSER.parse(CMD_OPTIONS, cmdLine.split(" "));
            if (cmd.hasOption("get-lang"))
            {
                Optional<GuildSettings> settings = settingsRepo.loadGuildSettings(guildId);
                if (settings.isPresent())
                {
                    builder.append("Guild Language: ");
                    builder.append(settings.get().getLanguage().toLanguageTag(), MsgStyle.BOLD);
                }
            }
            if (cmd.hasOption("set-lang"))
            {
                String langCode = cmd.getOptionValue("set-lang");
                Optional<GuildSettings> settings = settingsRepo.loadGuildSettings(guildId);
                if (settings.isPresent())
                {
                    GuildSettings gs = settings.get();
                    builder.append("Guild Language: ");
                    builder.append(gs.getLanguage().toLanguageTag(), MsgStyle.BOLD);
                    gs.setLanguage(Locale.forLanguageTag(langCode));
                    settingsRepo.storeGuildSettings(guildId, gs);
                    builder.append(" => ");
                    builder.append(gs.getLanguage().toLanguageTag(), MsgStyle.BOLD);
                }
            }
            if (cmd.hasOption("clear"))
            {
                settingsRepo.removeGuildSettings(guildId);
                builder.append("Guild Settings: ").append("CLEARED", MsgStyle.BOLD);
            }
        } 
        catch (ParseException ex)
        {
            LOGGER.trace(null, ex);
            HelpFormatter formatter = new HelpFormatter();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final PrintWriter writer = new PrintWriter(bos);
            formatter.printHelp(writer, 80, "guild-config", null, CMD_OPTIONS, 1, 3, null, true);
//            formatter.printUsage(writer,80,"guild-config", CMD_OPTIONS);
            writer.flush();
            builder.append(new String(bos.toByteArray()), MsgStyle.CODE);
        }
        return builder.build();
    }
    
}
