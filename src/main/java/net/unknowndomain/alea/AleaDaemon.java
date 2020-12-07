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
import net.unknowndomain.alea.bot.AleaListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.unknowndomain.alea.systems.RpgSystemCommand;
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
        this.shards = Collections.synchronizedList(new ArrayList<>());
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
        DiscordApiBuilder apiBuilder = new DiscordApiBuilder();
        apiBuilder.setToken(aleaConfig.getDiscordToken());
        apiBuilder.addListener(new AleaListener());
        if (aleaConfig.isSystemListener())
        {
            for (RpgSystemCommand system : RpgSystemCommand.LOADER)
            {
                apiBuilder.addListener(new SystemListener(system));
            }
        }
        apiBuilder.setRecommendedTotalShards().join();
        apiBuilder.loginAllShards().forEach(
                shardFuture -> shardFuture
                        .thenAccept(api -> {
                            LOGGER.info(api.createBotInvite());
                            shards.add(api);
                        })
                        .exceptionally(ExceptionLogger.get())
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
