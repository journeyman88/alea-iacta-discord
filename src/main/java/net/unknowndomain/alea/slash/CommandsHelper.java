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
package net.unknowndomain.alea.slash;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.unknowndomain.alea.settings.SettingsRepository;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class CommandsHelper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandsHelper.class);
    
    private static String prefix = "test-";
    
    public static void deleteCommands(DiscordApi api, Long guildId, Long commandId)
    {
        Optional<Server> server = api.getServerById(guildId);
        if (server.isPresent())
        {
            api.getServerSlashCommandById(server.get(), commandId).thenAccept(
                serverCommand -> {
                    serverCommand.deleteForServer(server.get());
                }
            );
        }
    }
    
    public static void deleteCommands(DiscordApi api)
    {
        api.getGlobalSlashCommands().thenAccept(
            globalCommands -> {
                for (SlashCommand cmd : globalCommands){
                    cmd.createSlashCommandUpdater();
                    cmd.deleteGlobal();
                }
            }
        );
    }
    
    public static void setupCommands(DiscordApi api, String commandPrefix, SettingsRepository settings)
    {
        prefix = commandPrefix;
        List<SlashCommandBuilder> commands = new LinkedList<>();
        commands.add(setupExprCommand());
        commands.add(setupGuildSettingCommand());
        for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
        {
            LOGGER.debug(cmd.getCommandDesc().getCommand());
            commands.add(setupSystemCommand(cmd));
        }
        api.bulkOverwriteGlobalApplicationCommands(commands).join();
        for (Long guildId : settings.listGuilds())
        {
            updateGuild(api, settings, guildId);
        }
//        List<SlashCommand> cose = api.getGlobalSlashCommands().join();
//        for (SlashCommand sc : cose)
//        {
//            LOGGER.info(sc.getName());
//        }
    }
    
    public static void updateGuild(DiscordApi api, SettingsRepository settings, Long guildId)
    {
        
//        Optional<Server> guild = api.getServerById(guildId);
//        if (guild.isPresent())
//        {
//            List<SlashCommandBuilder> commands = new LinkedList<>();
//            for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
//            {
//                LOGGER.debug(cmd.getCommandDesc().getCommand());
//                if (settings.isSystemEnabled(guildId, cmd.getCommandDesc()))
//                {
//                    commands.add(setupSystemCommand(cmd));
//                }
//            }
//            api.bulkOverwriteServerSlashCommands(guild.get(), commands).thenAccept(slashCommands -> {
//                for (SlashCommand sc : slashCommands)
//                {
//                    String sys = sc.getName().replaceAll(prefix, "");
//                    settings.setSystemCommand(guildId, sys, true, sc.getId());
//                }
//            });
//        }
    }
    
    public static void updateSystemCommand(SettingsRepository settings, DiscordApi api, Long guildId, String systemId)
    {
        Optional<SlashCommandBuilder> scb = setupSystemCommand(systemId);
        Optional<Server> server = api.getServerById(guildId);
        if (scb.isPresent() && server.isPresent())
        {
            scb.get().createForServer(server.get()).thenAccept(slashCommand -> {
                settings.setSystemCommand(guildId, systemId, true, slashCommand.getId());
            });
        }
    }
    
    public static Optional<SlashCommandBuilder> setupSystemCommand(String systemId)
    {
        for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
        {
            if (systemId.equals(cmd.getCommandDesc().getCommand()))
            {
                return Optional.ofNullable(setupSystemCommand(cmd));
            }
        }
        return Optional.empty();
    }
    
    private static SlashCommandBuilder setupExprCommand()
    {
        SlashCommandBuilder exprCommand = new SlashCommandBuilder().setName(prefix +"expr").setDescription("Solve the dice expression (example: 1d8+2d4-1d6+15-7)");
        exprCommand.addOption(SystemHelper.buildStringOption("expression", "The dice expression to solve", false));
        exprCommand.addOption(SystemHelper.buildBooleanOption("help", "Print the help", false));
        exprCommand.addOption(SystemHelper.buildBooleanOption("verbose", "Set the output as verbose", false));
        return exprCommand;
    }
    
    private static SlashCommandBuilder setupGuildSettingCommand()
    {
        List<SlashCommandOptionChoice> systems = new LinkedList<>();
        for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
        {
            systems.add(SlashCommandOptionChoice.create(cmd.getCommandDesc().getCommand(), cmd.getCommandDesc().getCommand()));
        }
        SlashCommandBuilder exprCommand = new SlashCommandBuilder().setName(prefix +"guild-config").setDescription("Edit the guild settings");
        exprCommand.addOption(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "get-lang", "Gets the guild language used by the RPG systems"));
        exprCommand.addOption(SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "set-lang", "Sets the guild language used by RPG systems", 
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("language")
                        .setDescription("The language to use if available")
                        .setRequired(true)));
//        exprCommand.addOption(SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "enable-system", "Enable RPG systems", 
//                new SlashCommandOptionBuilder()
//                        .setType(SlashCommandOptionType.STRING)
//                        .setDescription("The RPG system to enable")
//                        .setName("systemId")
//                        .setChoices(systems)
//                        .setRequired(true)));
//        exprCommand.addOption(SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "disable-system", "Disable RPG systems", 
//                new SlashCommandOptionBuilder()
//                        .setType(SlashCommandOptionType.STRING)
//                        .setDescription("The RPG system to disable")
//                        .setName("systemId")
//                        .setChoices(systems)
//                        .setRequired(true)));
        exprCommand.addOption(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "clear", "Remove all of the guild's stored data"));
//        exprCommand.setDefaultPermission(Boolean.FALSE);
        return exprCommand;
    }
    
    private static SlashCommandBuilder setupSystemCommand(RpgSystemCommand cmd)
    {
        SlashCommandBuilder syscommand = SlashCommand.with(prefix + cmd.getCommandDesc().getCommand(), cmd.getCommandDesc().getSystem());
        for (SlashCommandOption option : SystemHelper.exportOptions(cmd.getCommandDesc().getSystem(), cmd.buildOptions(), Locale.ENGLISH))
        {
            syscommand.addOption(option);
        }
        return syscommand;
    }
}
