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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class AleaConfigParser
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AleaConfigParser.class);
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
    
    public static AleaConfig parseConfig(String ... args) throws ParseException
    {
        CommandLine cmd = PARSER.parse(CMD_OPTIONS, args);
        AleaConfig retVal = new AleaConfig(cmd.getOptionValue("discordToken"), cmd.hasOption("systemListener"));
        return retVal;
    }
    
}
