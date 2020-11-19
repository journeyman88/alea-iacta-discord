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

import java.util.List;
import net.unknowndomain.alea.dice.DiceN;
import net.unknowndomain.alea.pools.DicePool;

/**
 *
 * @author journeyman
 */
public class KeepPart extends ExpPart
{
    
    private final DicePool<DiceN> dicePool;
    private final int maxDice;

    public KeepPart(String exp)
    {
        super(exp);
        String clean = exp.replaceAll("\\+|-", "").toLowerCase();
        String [] values = clean.split("d|k");
        Integer diceNumber = Integer.parseInt(values[0]);
        Integer diceClass = Integer.parseInt(values[1]);
        maxDice = Integer.parseInt(values[2]);
        DiceN dice = new DiceN()
        {
            @Override
            public int getMinResult()
            {
                return 1;
            }

            @Override
            public int getMaxResult()
            {
                return diceClass;
            }
        };
        dicePool = new DicePool<>(dice, diceNumber);
    }

    @Override
    public Integer getResult()
    {
        int sum = 0;
        List<Integer> results = dicePool.getResults();
        results.sort((Integer o1, Integer o2) ->
        {
            return -1 * o1.compareTo(o2);
        });
        int limit = (maxDice > results.size()) ? results.size() : maxDice;
        for (int idx = 0; idx < limit; idx++)
        {
            sum += results.get(idx);
        }
        return (isPositive() ? 1 : -1) * sum;
    }
    
}
