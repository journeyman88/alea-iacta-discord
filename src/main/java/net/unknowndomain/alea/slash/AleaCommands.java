/*
 * Copyright 2021 m.bignami.
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
import java.util.UUID;
import net.unknowndomain.alea.GenericListener;
import net.unknowndomain.alea.bot.MsgFormatter;
import net.unknowndomain.alea.command.PrintableOutput;
import net.unknowndomain.alea.expr.Expression;
import net.unknowndomain.alea.expr.ExpressionCommand;
import net.unknowndomain.alea.expr.ExpressionResult;
import net.unknowndomain.alea.messages.MsgBuilder;
import net.unknowndomain.alea.messages.ReturnMsg;
import net.unknowndomain.alea.parser.PicocliParser;
import net.unknowndomain.alea.roll.GenericResult;
import net.unknowndomain.alea.settings.GuildSettings;
import net.unknowndomain.alea.settings.SettingsRepository;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import net.unknowndomain.alea.systems.RpgSystemOptions;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class AleaCommands extends GenericListener implements SlashCommandCreateListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AleaCommands.class);
    
    private final SettingsRepository settingsRepository;
    
    public AleaCommands(SettingsRepository settingsRepository, UUID namespace)
    {
        super(namespace);
        this.settingsRepository = settingsRepository;
    }
    
    public static void setupCommands(DiscordApi api)
    {
        List<SlashCommandBuilder> commands = new LinkedList<>();
        commands.add(setupExprCommand());
        for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
        {
            LOGGER.debug(cmd.getCommandDesc().getCommand());
            commands.add(setupSystemCommand(cmd));
        }
        api.bulkOverwriteGlobalSlashCommands(commands).join();
//        List<SlashCommand> cose = api.getGlobalSlashCommands().join();
//        for (SlashCommand sc : cose)
//        {
//            LOGGER.info(sc.getName());
//        }
    }
    
    private static SlashCommandBuilder setupExprCommand()
    {
        SlashCommandBuilder exprCommand = new SlashCommandBuilder().setName("expr").setDescription("Solve the dice expression (example: 1d8+2d4-1d6+15-7)");
        exprCommand.addOption(SystemHelper.buildStringOption("expression", "The dice expression to solve", false));
        exprCommand.addOption(SystemHelper.buildBooleanOption("help", "Print the help", false));
        exprCommand.addOption(SystemHelper.buildBooleanOption("verbose", "Set the output as verbose", false));
        return exprCommand;
    }
    
    private static SlashCommandBuilder setupSystemCommand(RpgSystemCommand cmd)
    {
        SlashCommandBuilder syscommand = SlashCommand.with(cmd.getCommandDesc().getCommand(), cmd.getCommandDesc().getSystem());
        for (SlashCommandOption option : SystemHelper.exportOptions(cmd.buildOptions(), Locale.ENGLISH))
        {
            syscommand.addOption(option);
        }
        return syscommand;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event)
    {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String commandName = interaction.getCommandName();
        Long guildId = null;
        Locale locale = Locale.ENGLISH;
        Optional<UUID> callerId = buildCallerId(interaction.getUser());
        if (interaction.getServer().isPresent())
            {
                guildId = interaction.getServer().get().getId();
                Optional<GuildSettings> guildSettings = settingsRepository.loadGuildSettings(guildId);
                if (guildSettings.isPresent())
                {
                    LOGGER.debug("GuildSettings found");
                    locale = guildSettings.get().getLanguage();
                }
            }
        ReturnMsg result = new MsgBuilder().build();
        if ("expr".equalsIgnoreCase(commandName))
        {
            Optional<SlashCommandInteractionOption> optExpr = interaction.getOptionByName("expression");
            result = (new ExpressionCommand()).printHelp(locale);
            boolean help = SystemHelper.parseBooleanOption(interaction, "help");
            boolean verbose = SystemHelper.parseBooleanOption(interaction, "verbose");
            if (optExpr.isPresent() && !help)
            {
                String expression = optExpr.get().getStringValue().get();
                Expression expressionEngine = new Expression(expression);
                ExpressionResult exprRes = expressionEngine.getResult();
                exprRes.setVerbose(verbose);
                Optional<PrintableOutput> out = Optional.of(exprRes);
                if (out.isPresent())
                {
                    result = out.get().buildMessage();
                }
            }
        }
        else
        {
            Optional<RpgSystemCommand> foundCmd = Optional.empty();
            for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
            {
                if (cmd.getCommandDesc().getCommand().equals(commandName))
                {
                    foundCmd = Optional.of(cmd);
                }
            }
            if (foundCmd.isPresent())
            {
                RpgSystemOptions options = foundCmd.get().buildOptions();
                SystemHelper.parseOptions(options, interaction);
                Optional<GenericResult> res = foundCmd.get().execCommand(options, locale, callerId);
                if (res.isPresent())
                {
                    result = res.get().buildMessage();
                }
                else
                {
                    result = PicocliParser.printHelp(interaction.getCommandName(), options, locale);
                }
            }
        }
        InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();
        MsgFormatter.appendMessage(responder, result);
        responder.respond();
    }
}
