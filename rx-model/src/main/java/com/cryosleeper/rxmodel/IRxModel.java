package com.cryosleeper.rxmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;

/**
 *
 */

public interface IRxModel<Item extends IRxModelItem, Category> {

    /**
     * submitting one item to add/replace
     * @param item
     */
    void submitItem(@NonNull Item item);

    /**
     * submitting a few items to add/replace
     * @param items
     */
    void submitItems(@NonNull List<? extends Item> items);

    /**
     * delete an item
     * @param id
     */
    void delete(String id);

    /**
     * delete all items
     */
    void clear();

    /**
     * get item as a sync operation
     * @param id
     * @return item or null
     */
    @Nullable
    Item getItem(String id);

    /**
     * get Observable for item
     * @param id
     * @return observable to emit item updates until item is deleted
     */
    @NonNull
    Observable<Item> subscribeItem(String id);

    /**
     * get Observable to emit category items updates until cleared
     * @param category
     * @return
     */
    @NonNull
    Observable<List<Item>> subscribeCategory(Category category);
}
