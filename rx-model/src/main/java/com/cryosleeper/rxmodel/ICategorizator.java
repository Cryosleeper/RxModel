package com.cryosleeper.rxmodel;

import java.util.Set;

/**
 *
 */

public interface ICategorizator<Item, Category> {
    Set<Category> categorize(Item item);
}
