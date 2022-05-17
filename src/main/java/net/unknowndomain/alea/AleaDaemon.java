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

import net.unknowndomain.alea.bot.SystemListener;
import net.unknowndomain.alea.bot.AleaMsgListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.unknowndomain.alea.server.AleaJoinListener;
import net.unknowndomain.alea.slash.AleaSlashCommands;
import net.unknowndomain.alea.settings.SettingsRepository;
import net.unknowndomain.alea.slash.CommandsHelper;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import net.unknowndomain.alea.utils.EmojiIconSolver;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class AleaDaemon implements Daemon
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AleaDaemon.class);
    
    private AleaConfig aleaConfig;
    private List<DiscordApi> shards;
    
    public AleaDaemon()
    {
        
    }
    
    public AleaDaemon(AleaConfig aleaConfig)
    {
        this.aleaConfig = aleaConfig;
        this.shards = Collections.synchronizedList(new LinkedList<>());
    }
    
    @Override
    public void init(DaemonContext dc) throws DaemonInitException, Exception
    {
        aleaConfig = AleaConfigParser.parseConfig(dc.getArguments());
        shards = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void start() throws Exception
    {
        SettingsRepository settingsRepository = new SettingsRepository(aleaConfig.getSettingsDir());
        DiscordApiBuilder apiBuilder = new DiscordApiBuilder();
        apiBuilder.setToken(aleaConfig.getDiscordToken());
        apiBuilder.addListener(new AleaMsgListener(settingsRepository, aleaConfig.getNamespace()));
        for (Long guildId : settingsRepository.listGuilds())
        {
            for (RpgSystemCommand system : RpgSystemCommand.LOADER)
            {
                settingsRepository.initSystem(guildId, system.getCommandDesc());
            }
        }
        if (aleaConfig.isSystemListener())
        {
            for (RpgSystemCommand system : RpgSystemCommand.LOADER)
            {
                apiBuilder.addListener(new SystemListener(system, settingsRepository, aleaConfig.getNamespace()));
            }
        }
        if (aleaConfig.isEnableInteractions())
        {
            apiBuilder.addListener(new AleaSlashCommands(settingsRepository, aleaConfig.getCommandPrefix(), aleaConfig.getNamespace()));
        }
        apiBuilder.addServerJoinListener(new AleaJoinListener(settingsRepository, aleaConfig.getNamespace()));
        apiBuilder.setRecommendedTotalShards().join();
        apiBuilder.loginAllShards().forEach(shardFuture -> shardFuture.thenAccept(
                api -> {
                    LOGGER.info(api.createBotInvite());
                    if (shards.isEmpty())
                    {
                        shards.add(api);
                        if (aleaConfig.isEnableInteractions())
                        {
                            CommandsHelper.setupCommands(api, aleaConfig.getCommandPrefix(), settingsRepository);
                        }
                        else
                        {
                            CommandsHelper.deleteCommands(api);
                        }
                    }
                    else
                    {
                        shards.add(api);
                    }
                    EmojiIconSolver.build(api);
                }
            ).exceptionally(ExceptionLogger.get())
        );
    }

    @Override
    public void stop() throws Exception
    {
        shards.forEach(api ->
        {
            api.disconnect();
        });
    }

    @Override
    public void destroy()
    {
        aleaConfig = null;
        shards = null;
    }
    
}
