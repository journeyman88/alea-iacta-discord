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
package net.unknowndomain.alea.pools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.unknowndomain.alea.dice.GenericDice;

/**
 *
 * @author journeyman
 * @param <T>
 */
public class DicePool<T extends GenericDice>
{
    private final T diceClass;
    private final Integer numberOfDice;
    private final Set<Integer> explodingValues;
    
    public DicePool(T diceClass, int numberOfDice, Integer ... explodingValues)
    {
        this.diceClass = diceClass;
        this.numberOfDice = numberOfDice;
        Set<Integer> tmpSet = new HashSet<>();
        if (explodingValues != null)
        {
            List<Integer> tmpEX = Arrays.asList(explodingValues);
            if (!tmpEX.isEmpty())
            {
                tmpSet.addAll(tmpEX);
            }
        }
        this.explodingValues = Collections.unmodifiableSet(tmpSet);
    }
    
    public List<Integer> getResults()
    {
        List<Integer> results = new ArrayList<>(numberOfDice);
        int idx = 0;
        while(idx < numberOfDice)
        {
            idx++;
            int result = diceClass.roll();
            if (explodingValues.contains(result))
            {
                idx--;
            }
            results.add(result);
        }
        return results;
    }
}
