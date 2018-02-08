package com.cryosleeper.rxmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 *
 */

public final class RxModel<Item extends IRxModelItem, Category> implements IRxModel<Item, Category> {

    private Map<String, BehaviorSubject<Item>> itemObservers = new HashMap<>();
    private Map<Category, BehaviorSubject<List<Item>>> categoryObservers = new HashMap<>();
    private Map<String, Item> items = new HashMap<>();

    private ICategorizator<Item, Category> categorizator;

    public RxModel(ICategorizator<Item, Category> itemCategorizator) {
        categorizator = itemCategorizator;
    }

    @Override
    public void submitItem(@NonNull Item item) {
        Item oldItem = items.get(item.getId());
        items.put(item.getId(), item);
        if (itemObservers.containsKey(item.getId())) {
            itemObservers.get(item.getId()).onNext(item);
        }
        notifyCategories(categorizator.categorize(item));
        if (oldItem != null && categorizator.categorize(item) != categorizator.categorize(oldItem)) {
            notifyCategories(categorizator.categorize(oldItem));
        }
    }

    @Override
    public void submitItems(@NonNull List<? extends Item> items) {
        for (Item item : items) {
            submitItem(item);
        }
    }

    @Override
    public void delete(String id) {
        if (!items.containsKey(id)) {
            return;
        }

        Set<Category> categories = categorizator.categorize(items.get(id));
        items.remove(id);
        if (itemObservers.containsKey(id)) {
            itemObservers.get(id).onComplete();
            itemObservers.remove(id);
        }
        notifyCategories(categories);
    }

    private void notifyCategories(Set<Category> categories) {
        for (Category category : categories) {
            if (categoryObservers.containsKey(category)) {
                notifyCategory(category);
            }
        }
    }

    @Override
    public void clear() {
        items.clear();
        for (BehaviorSubject subject : itemObservers.values()) {
            subject.onComplete();
        }
        itemObservers.clear();
        for (BehaviorSubject subject : categoryObservers.values()) {
            subject.onComplete();
        }
        categoryObservers.clear();
    }

    @Nullable
    @Override
    public Item getItem(String id) {
        return items.get(id);
    }

    @NonNull
    @Override
    public Observable<Item> subscribeItem(String id) {
        if (!itemObservers.containsKey(id)) {
            itemObservers.put(id, BehaviorSubject.<Item>create());
            if (items.containsKey(id)) {
                itemObservers.get(id).onNext(items.get(id));
            }
        }
        return itemObservers.get(id);
    }

    @NonNull
    @Override
    public Observable<List<Item>> subscribeCategory(Category category) {
        if (!categoryObservers.containsKey(category)) {
            categoryObservers.put(category, BehaviorSubject.<List<Item>>create());
            notifyCategory(category);
        }
        return categoryObservers.get(category);
    }

    private void notifyCategory(Category category) {
        List<Item> result = getItemsForCategory(category);
        categoryObservers.get(category).onNext(result);
    }

    private List<Item> getItemsForCategory(Category category) {
        List<Item> result = new ArrayList<>();
        for (String key : items.keySet()) {
            Item item = items.get(key);
            if (categorizator.categorize(item).contains(category)) {
                result.add(item);
            }
        }
        return result;
    }
}
