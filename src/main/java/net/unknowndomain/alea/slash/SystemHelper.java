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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import net.unknowndomain.alea.systems.RpgSystemOptions;
import net.unknowndomain.alea.systems.annotations.RpgSystemData;
import net.unknowndomain.alea.systems.annotations.RpgSystemOption;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class SystemHelper
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemHelper.class);
    
    public static SlashCommandOption buildStringOption(String name, String desc, boolean required)
    {
        return SlashCommandOption.create(SlashCommandOptionType.STRING, name, desc, required);
    }
    
    public static SlashCommandOption buildIntegerOption(String name, String desc, boolean required)
    {
        return SlashCommandOption.create(SlashCommandOptionType.INTEGER, name, desc, required);
    }
    
    public static SlashCommandOption buildBooleanOption(String name, String desc, boolean required)
    {
//        return SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, name, desc, required);
        return SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, name, desc, required,
                                    Arrays.asList(
                                        SlashCommandOptionChoice.create("false", "false"),
                                        SlashCommandOptionChoice.create("true", "true")));
    }
    
    public static SlashCommandOption buildListOption(String name, String desc, boolean required)
    {
        return SlashCommandOption.create(SlashCommandOptionType.STRING, name, desc, required);
    }
    
    public static boolean parseBooleanOption(SlashCommandInteraction interaction, String name)
    {
        boolean result = false;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<Boolean> optBool = optBoolean.get().getBooleanValue();
            if (optBool.isPresent())
            {
                result = optBool.get();
            }
            else
            {
                Optional<String> optBoolStr = optBoolean.get().getStringValue();
                if (optBoolStr.isPresent())
                {
                    result = optBoolStr.get().equalsIgnoreCase("true");
                }
            }
        }
        return result;
    }
    
    public static Integer parseIntegerOption(SlashCommandInteraction interaction, String name)
    {
        Integer result = null;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<Integer> optBool = optBoolean.get().getIntValue();
            if (optBool.isPresent())
            {
                result = optBool.get();
            }
        }
        return result;
    }
    
    public static List<String> parseListOption(SlashCommandInteraction interaction, String name)
    {
        List<String> result = null;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<String> optBool = optBoolean.get().getStringValue();
            if (optBool.isPresent())
            {
                result = new LinkedList<>();
                result.addAll(Arrays.asList(optBool.get().split(",")));
            }
        }
        return result;
    }
    
    public static String parseStringOption(SlashCommandInteraction interaction, String name)
    {
        String result = null;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<String> optBool = optBoolean.get().getStringValue();
            if (optBool.isPresent())
            {
                result = optBool.get();
            }
        }
        return result;
    }
    
    public static boolean parseBooleanSubOption(SlashCommandInteraction interaction, String name)
    {
        boolean result = false;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptions().get(0).getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<Boolean> optBool = optBoolean.get().getBooleanValue();
            if (optBool.isPresent())
            {
                result = optBool.get();
            }
            else
            {
                Optional<String> optBoolStr = optBoolean.get().getStringValue();
                if (optBoolStr.isPresent())
                {
                    result = optBoolStr.get().equalsIgnoreCase("true");
                }
            }
        }
        return result;
    }
    
    public static Integer parseIntegerSubOption(SlashCommandInteraction interaction, String name)
    {
        Integer result = null;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptions().get(0).getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<Integer> optBool = optBoolean.get().getIntValue();
            if (optBool.isPresent())
            {
                result = optBool.get();
            }
        }
        return result;
    }
    
    public static List<String> parseListSubOption(SlashCommandInteraction interaction, String name)
    {
        List<String> result = null;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptions().get(0).getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<String> optBool = optBoolean.get().getStringValue();
            if (optBool.isPresent())
            {
                result = new LinkedList<>();
                result.addAll(Arrays.asList(optBool.get().split(",")));
            }
        }
        return result;
    }
    
    public static String parseStringSubOption(SlashCommandInteraction interaction, String name)
    {
        String result = null;
        Optional<SlashCommandInteractionOption> optBoolean = interaction.getOptions().get(0).getOptionByName(name);
        if (optBoolean.isPresent())
        {
            Optional<String> optBool = optBoolean.get().getStringValue();
            if (optBool.isPresent())
            {
                result = optBool.get();
            }
        }
        return result;
    }
    
    public static List<SlashCommandOption> exportOptions(RpgSystemOptions options, Locale lang)
    {
        List<SlashCommandOption> listParams = new LinkedList<>();
        List<Field> fields = new ArrayList<>();
        ResourceBundle i18n = null;
        Map<String,String> subcommanDesc = new TreeMap<>();
        Map<String,List<SlashCommandOption>> subcommands = new TreeMap<>();
        if (options.getClass().isAnnotationPresent(RpgSystemData.class)) 
        {
            RpgSystemData data = options.getClass().getAnnotation(RpgSystemData.class);
            try 
            {
                i18n = ResourceBundle.getBundle(data.bundleName(), lang);
                String [] groups = data.groupsName();
                for (int i=0; i< groups.length; i++)
                {
                    String gn = groups[i];
                    subcommands.put(gn, new LinkedList<>());
                    if (groups.length == data.groupsDesc().length)
                    {
                        subcommanDesc.put(gn, data.groupsDesc()[i]);
                    }
                    else
                    {
                        subcommanDesc.put(gn, gn);
                    }
                }
            }
            catch (MissingResourceException ex)
            {
                LOGGER.warn(null, ex);
            }
        }
        Class workingClass = options.getClass();
        while (true)
        {
            fields.addAll(Arrays.asList(workingClass.getDeclaredFields()));
            if (Objects.equals(RpgSystemOptions.class, workingClass))
            {
                break;
            }
            workingClass = workingClass.getSuperclass();
        }
        for (Field field : fields) 
        {
            field.setAccessible(true);
            if (field.isAnnotationPresent(RpgSystemOption.class)) {
                RpgSystemOption[] annotations = field.getAnnotationsByType(RpgSystemOption.class);

                for(RpgSystemOption annotation : annotations){
                    SlashCommandOption opt = null;
                    String optName = null;
                    if (!annotation.name().isEmpty())
                    {
                        optName = annotation.name();
                    }
                    if ((optName == null) && (!annotation.shortcode().isEmpty()))
                    {
                        optName = annotation.shortcode();
                    }
                    boolean req = annotation.required();
                    String groupName = null;
                    if (!annotation.groupName().isEmpty())
                    {
                        groupName = annotation.groupName();
                        req = annotation.groupRequired();
                    }
                    String desc = "";
                    if ((i18n != null) && (!annotation.description().isEmpty()))
                    {
                        desc = i18n.getString(annotation.description());
                    }
                    Class c = field.getType();
                    if (
                            java.lang.Boolean.class.isAssignableFrom(c) || 
                            java.lang.Boolean.TYPE.isAssignableFrom(c)
                        )
                    {
                        opt = SystemHelper.buildBooleanOption(optName, desc, req);
                    }
                    else if (
                            java.lang.Number.class.isAssignableFrom(c) || 
                            java.lang.Short.TYPE.isAssignableFrom(c) || 
                            java.lang.Integer.TYPE.isAssignableFrom(c) || 
                            java.lang.Long.TYPE.isAssignableFrom(c) || 
                            java.lang.Float.TYPE.isAssignableFrom(c) || 
                            java.lang.Double.TYPE.isAssignableFrom(c)
                            )
                    {
                        opt = SystemHelper.buildIntegerOption(optName, desc, annotation.required());
                    }
                    else if (
                            java.util.Collection.class.isAssignableFrom(c)
                            )
                    {
                        opt = SystemHelper.buildListOption(optName, desc, annotation.required());
                    }
                    else
                    {
                        opt = SystemHelper.buildStringOption(optName, desc, annotation.required());
                    }
                    if (opt != null)
                    {
                        if (groupName != null)
                        {
                            subcommands.get(groupName).add(opt);
                        }
                        else
                        {
                            if (subcommands.isEmpty())
                            {
                                listParams.add(opt);
                            }
                            else
                            {
                                for (String key : subcommands.keySet())
                                {
                                    subcommands.get(key).add(opt);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!subcommands.isEmpty())
        {
            listParams = new LinkedList<>();
            for (String key : subcommands.keySet())
            {
                listParams.add(SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, key, subcommanDesc.get(key), subcommands.get(key)));
            }
        }
        return listParams;
    }
    
    public static void parseOptions(RpgSystemOptions options, SlashCommandInteraction interaction)
    {
        List<Field> fields = new ArrayList<>();
        Class workingClass = options.getClass();
        boolean forceHelp = false;
        boolean subcom = false;
        if (options.getClass().isAnnotationPresent(RpgSystemData.class)) 
        {
            RpgSystemData data = options.getClass().getAnnotation(RpgSystemData.class);
            subcom = (data.groupsName().length > 0);
        }
        if (subcom)
        {
            forceHelp = interaction.getOptions().get(0).getOptions() == null || interaction.getOptions().get(0).getOptions().isEmpty();
        }
        else
        {
            forceHelp = interaction.getOptions() == null || interaction.getOptions().isEmpty();
        }
        while (true)
        {
            fields.addAll(Arrays.asList(workingClass.getDeclaredFields()));
            if (Objects.equals(RpgSystemOptions.class, workingClass))
            {
                break;
            }
            workingClass = workingClass.getSuperclass();
        }
        for (Field field : fields) 
        {
            field.setAccessible(true);
            if (field.isAnnotationPresent(RpgSystemOption.class)) {
                RpgSystemOption[] annotations = field.getAnnotationsByType(RpgSystemOption.class);

                for(RpgSystemOption annotation : annotations){
                    
                    String optName = null;
                    if (!annotation.name().isEmpty())
                    {
                        optName = annotation.name();
                    }
                    if ((optName == null) && (!annotation.shortcode().isEmpty()))
                    {
                        optName = annotation.shortcode();
                    }
                    if (optName != null)
                    {
                        Object opt = null;
                        Class c = field.getType();
                        if (
                            java.lang.Boolean.class.isAssignableFrom(c) || 
                            java.lang.Boolean.TYPE.isAssignableFrom(c)
                            )
                        {
                            if (subcom)
                            {
                                opt = SystemHelper.parseBooleanSubOption(interaction, optName);
                            }
                            else
                            {
                                opt = SystemHelper.parseBooleanOption(interaction, optName);
                            }
                        }
                        else if (
                                java.lang.Number.class.isAssignableFrom(c) || 
                                java.lang.Short.TYPE.isAssignableFrom(c) || 
                                java.lang.Integer.TYPE.isAssignableFrom(c) || 
                                java.lang.Long.TYPE.isAssignableFrom(c) || 
                                java.lang.Float.TYPE.isAssignableFrom(c) || 
                                java.lang.Double.TYPE.isAssignableFrom(c)
                                )
                        {
                            if (subcom)
                            {
                                opt = SystemHelper.parseIntegerSubOption(interaction, optName);
                            }
                            else
                            {
                                opt = SystemHelper.parseIntegerOption(interaction, optName);
                            }
                        }
                        else if (
                                java.util.Collection.class.isAssignableFrom(c)
                                )
                        {
                            if (subcom)
                            {
                                opt = SystemHelper.parseListSubOption(interaction, optName);
                            }
                            else
                            {
                                opt = SystemHelper.parseListOption(interaction, optName);
                            }
                        }
                        else
                        {
                            if (subcom)
                            {
                                opt = SystemHelper.parseStringSubOption(interaction, optName);
                            }
                            else
                            {
                                opt = SystemHelper.parseStringOption(interaction, optName);
                            }
                        }
                        if (opt != null)
                        {
                            try
                            {
                                field.set(options, opt);
                            } catch (IllegalArgumentException | IllegalAccessException ex)
                            {
                                LOGGER.error(null, ex);
                            }
                        }
                    }
                }
            }
        }
        if (forceHelp)
        {
            options.setHelp(true);
        }
    }
}
