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

import net.unknowndomain.alea.dice.DiceN;
import net.unknowndomain.alea.pools.DicePool;

/**
 *
 * @author journeyman
 */
public class UpperPart extends ExpPart
{
    
    private final DicePool<DiceN> dicePool;
    private final int threshold;

    public UpperPart(String exp)
    {
        super(exp);
        String clean = exp.replaceAll("\\+|-", "").toLowerCase();
        String [] values = clean.split("d|\\/");
        Integer diceNumber = Integer.parseInt(values[0]);
        Integer diceClass = Integer.parseInt(values[1]);
        threshold = Integer.parseInt(values[2]);
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
        for (Integer res : dicePool.getResults())
        {
            if (res >= threshold)
            {
                sum ++;
            }
        }
        return (isPositive() ? 1 : -1) * sum;
    }
    
}
