package com.cryosleeper.rxmodeltestapplication;

import com.cryosleeper.rxmodel.ICategorizator;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */

public class IntegerCategorizator implements ICategorizator<IntegerItem, IntegerItem.IntegerCategory> {
    @Override
    public Set<IntegerItem.IntegerCategory> categorize(IntegerItem o) {
        Set<IntegerItem.IntegerCategory> result = new HashSet<>();
        result.add(o.getValue() % 2 == 0 ? IntegerItem.IntegerCategory.Even : IntegerItem.IntegerCategory.Odd);
        return result;
    }
}
