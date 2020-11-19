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
package net.unknowndomain.alea.expr;

import java.util.regex.Matcher;
import net.unknowndomain.alea.command.Command;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;

/**
 *
 * @author journeyman
 */
public class ExpressionCommand extends Command
{

    @Override
    protected String getCommandRegex()
    {
        return "expr";
    }

    @Override
    public MessageBuilder execCommand(String cmdLine)
    {
        MessageBuilder retVal = new MessageBuilder();
        Matcher prefixMatcher = PREFIX.matcher(cmdLine);
        if (prefixMatcher.matches())
        {
            if (cmdLine.contains(CMD_HELP) || cmdLine.contains("-h"))
            {
                return printHelp();
            }
            else
            {
                String params = prefixMatcher.group(CMD_PARAMS);
                Expression solver = new Expression(params);
                retVal.append(params).append(" = ").append(solver.getResult().toString(), MessageDecoration.BOLD);
            }
        }
        return retVal;
    }
    
    public MessageBuilder printHelp()
    {
        MessageBuilder retVal = new MessageBuilder();
        retVal.append("Expression Engine (expr) Help", MessageDecoration.BOLD, MessageDecoration.UNDERLINE).appendNewLine().appendNewLine();
        retVal.append("The expr command performs and solves a simple set of addition/subtraction operations using the following dice annotation").appendNewLine();
        retVal.append(" * NdX: roll N dice with X faces and sum them (es: 2d12)").appendNewLine();
        retVal.append(" * N: add an Integer number (es: +5, -3)").appendNewLine();
        retVal.append(" * NdXkY: roll N dice with X faces, sum the Y with the highest face value (es: 10d6k8)").appendNewLine();
        retVal.append(" * NdX/Y: roll N dice with X faces, count the ones with a face value higher or equal than Y (es: 10d10/7)").appendNewLine();
        retVal.append(" * NdX\\Y: roll N dice with X faces, count the ones with a face value lower or equal than Y (es: 10d10\\4)").appendNewLine();
        retVal.append("These operation can be compounded in any way: 4d4/3  + 10d6k5 - 1d8 - 1d12\\5 +7").appendNewLine();
        return retVal;
    }
    
}
