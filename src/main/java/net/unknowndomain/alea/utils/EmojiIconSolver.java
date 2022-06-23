/*
 * Copyright 2022 journeyman.
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
package net.unknowndomain.alea.utils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import net.unknowndomain.alea.icon.AleaIcon;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.CustomEmoji;

/**
 *
 * @author journeyman
 */
public class EmojiIconSolver
{
    
    private static EmojiIconSolver INSTANCE = null;
    
    private final Cache<String, CustomEmoji> iconCache;
    
    private final DiscordApi api;
    
    private EmojiIconSolver(DiscordApi api)
    {
    
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<String, CustomEmoji> config = new MutableConfiguration<>();
        config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new ModifiedExpiryPolicy(new Duration( TimeUnit.MINUTES, 30 ))));
        this.iconCache = cacheManager.createCache("EmojiIcon_" + UUID.randomUUID(), config);
        this.api = api;
    }
    
    public static synchronized void build(DiscordApi api)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new EmojiIconSolver(api);
        }
    }
    
    public static EmojiIconSolver getInstance()
    {
        return INSTANCE;
    }
    
    public Optional<CustomEmoji> solveIcon(AleaIcon icon)
    {
        String iconKey = icon.getNamespace() + "_" + icon.getIconId();
        if (iconCache.containsKey(iconKey))
        {
            return Optional.ofNullable(iconCache.get(iconKey));
        }
        return solveIconImpl(iconKey);
    }
    
    private Optional<CustomEmoji> solveIconImpl(String iconKey)
    {
        Optional<CustomEmoji> retVal = Optional.empty();
        for (CustomEmoji emoji : api.getCustomEmojisByName(iconKey))
        {
            retVal = Optional.ofNullable(emoji);
        }
        return retVal;
    }
    
}
