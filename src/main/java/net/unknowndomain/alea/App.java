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

import java.util.logging.Level;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class App
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final Options CMD_OPTIONS = new Options();
    private static final CommandLineParser PARSER = new DefaultParser();
    
    static {
        CMD_OPTIONS.addOption(
                Option.builder("d")
                        .longOpt("discordToken")
                        .desc("Discord Token")
                        .hasArg()
                        .required()
                        .argName("token")
                        .build()
        );
        CMD_OPTIONS.addOption(
                Option.builder("l")
                        .longOpt("systemListener")
                        .desc("Enable system specific listeners")
                        .build()
        );
    }
    
    public static void main(String ... args)
    {
        try
        {
            CommandLine cmd = PARSER.parse(CMD_OPTIONS, args);
            DiscordApiBuilder apiBuilder = new DiscordApiBuilder();
            apiBuilder.setToken(cmd.getOptionValue("discordToken"));
            apiBuilder.addListener(new AleaListener());
            if (cmd.hasOption("systemListener"))
            {
                for (RpgSystemCommand system : RpgSystemCommand.LOADER)
                {
                    apiBuilder.addListener(new SystemListener(system));
                }
            }
            apiBuilder.setRecommendedTotalShards().join();
            apiBuilder.loginAllShards().forEach(
                    shardFuture -> shardFuture
                            .thenAccept(App::onShardLogin)
                            .exceptionally(ExceptionLogger.get())
            );
        } catch (ParseException ex)
        {
            LOGGER.error(null, ex);
        }
    }
    
    private static void onShardLogin(DiscordApi api) 
    {
        LOGGER.info(api.createBotInvite());
    }
}
