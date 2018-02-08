package com.cryosleeper.rxmodel;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.observers.TestObserver;

import static org.junit.Assert.*;

/**
 *
 */
public class IRxModelTest {

    private ItemCategorizator categorizator;

    @Test
    public void testAddingItem() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        //PERFORM
        Item item = new Item("1");
        model.submitItem(item);

        //CHECK
        assertEquals(item, model.getItem("1"));
    }

    @Test
    public void testAddMultipleItems() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        //PERFORM
        Item item1 = new Item("1");
        Item item2 = new Item("2");
        Item item3 = new Item("3");
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        model.submitItems(items);

        //CHECK
        assertEquals(item1, model.getItem("1"));
        assertEquals(item2, model.getItem("2"));
        assertEquals(item3, model.getItem("3"));
    }

    @Test
    public void testDeleteItem() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        model.submitItem(item);

        //PERFORM
        model.delete("1");

        //CHECK
        assertNull(model.getItem("1"));
    }

    @Test
    public void testDeleteNonExistentItem() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        //PERFORM
        model.delete("1");

        //CHECK
        assertNull(model.getItem("1"));
    }

    @Test
    public void testSubscribeNonExistentItem() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        //PERFORM
        TestObserver testObserver = model.subscribeItem("1").test();

        //CHECK
        testObserver.assertEmpty();
    }

    @Test
    public void testSubscribeItem() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        //PERFORM
        TestObserver testObserver = model.subscribeItem("1").test();
        Item item = new Item("1");
        model.submitItem(item);

        //CHECK
        testObserver.assertValue(item);
    }

    @Test
    public void testSubscribedItemDelete() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        model.submitItem(item);
        TestObserver testObserver = model.subscribeItem("1").test();

        //PERFORM
        model.delete("1");

        //CHECK
        testObserver.assertResult(item);
    }

    @Test
    public void testSubscribeEmptyCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        //PERFORM
        TestObserver testObserver = model.subscribeCategory(Category.Category1).test();

        //CHECK
        testObserver.assertValue(new ArrayList<>());
    }

    @Test
    public void testSubscribeNonEmptyCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item1 = new Item("1");
        item1.addCategory(Category.Category1);
        Item item2 = new Item("2");
        item2.addCategory(Category.Category1);
        Item item3 = new Item("3");
        item3.addCategory(Category.Category3);
        model.submitItem(item1);
        model.submitItem(item2);
        model.submitItem(item3);

        //PERFORM
        TestObserver testObserver = model.subscribeCategory(Category.Category1).test();

        //CHECK
        ArrayList list = new ArrayList();
        list.add(item1);
        list.add(item2);
        testObserver.assertValues(list);
    }

    @Test
    public void testAddNewItemToSubscribedCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        item.addCategory(Category.Category1);
        TestObserver testObserver = model.subscribeCategory(Category.Category1).test();

        //PERFORM
        model.submitItem(item);

        //CHECK
        ArrayList list = new ArrayList();
        list.add(item);
        testObserver.assertValues(new ArrayList<>(), list);
    }

    @Test
    public void testDeleteItemFromSubscribedCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        item.addCategory(Category.Category1);
        model.submitItem(item);
        TestObserver testObserver = model.subscribeCategory(Category.Category1).test();

        //PERFORM
        model.delete("1");

        //CHECK
        ArrayList list = new ArrayList();
        list.add(item);
        testObserver.assertValues(list, new ArrayList<>());
    }

    @Test
    public void testAddItemWhileSubscribingAnotherCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        item.addCategory(Category.Category2);
        TestObserver testObserver = model.subscribeCategory(Category.Category1).test();

        //PERFORM
        model.submitItem(item);

        //CHECK
        testObserver.assertValues(new ArrayList<>());
    }

    @Test
    public void testDeleteItemWhileSubscribingAnotherCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        item.addCategory(Category.Category2);
        model.submitItem(item);
        TestObserver testObserver = model.subscribeCategory(Category.Category1).test();

        //PERFORM
        model.delete("1");

        //CHECK
        testObserver.assertValues(new ArrayList<>());
    }

    @Test
    public void testChangingCategory() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();

        Item item1 = new Item("1");
        item1.addCategory(Category.Category1);
        Item item2 = new Item("2");
        item2.addCategory(Category.Category1);

        model.submitItem(item1);
        model.submitItem(item2);

        TestObserver testObserver1 = model.subscribeCategory(Category.Category1).test();
        TestObserver testObserver2 = model.subscribeCategory(Category.Category2).test();

        //PERFORM
        Item testItem = new Item("2");
        testItem.addCategory(Category.Category2);
        model.submitItem(testItem);

        //CHECK
        List<Item> categoryOneStart = new ArrayList<>();
        categoryOneStart.add(item1);
        categoryOneStart.add(item2);
        List<Item> categoryOneFinish = new ArrayList<>();
        categoryOneFinish.add(item1);
        testObserver1.assertValues(categoryOneStart, categoryOneFinish);

        List<Item> categoryTwoStart = new ArrayList<>();
        List<Item> categoryTwoFinish = new ArrayList<>();
        categoryTwoFinish.add(testItem);
        testObserver2.assertValues(categoryTwoStart, categoryTwoFinish);
    }

    @Test
    public void testMulticategorialItem() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        Item item = new Item("1");
        item.addCategory(Category.Category1);
        item.addCategory(Category.Category2);
        TestObserver testObserver1 = model.subscribeCategory(Category.Category1).test();
        TestObserver testObserver2 = model.subscribeCategory(Category.Category2).test();

        //PERFORM
        model.submitItem(item);

        //CHECK
        ArrayList list = new ArrayList();
        list.add(item);
        testObserver1.assertValues(new ArrayList<>(), list);
        testObserver2.assertValues(new ArrayList<>(), list);
    }

    @Test
    public void testClear() throws Exception {
        //PREPARE
        RxModel<Item, Category> model = getRxModel();
        TestObserver testObserverItem = model.subscribeItem("1").test();
        TestObserver testObserverCategory = model.subscribeCategory(Category.Category1).test();

        //PERFORM
        model.clear();

        //CHECK
        testObserverItem.assertResult();
        testObserverCategory.assertResult(new ArrayList<>());
    }

    @NonNull
    private RxModel<Item, Category> getRxModel() {
        return new RxModel<>(getItemCategorizator());
    }

    @NonNull
    private ItemCategorizator getItemCategorizator() {
        if (categorizator == null) {
            categorizator = new ItemCategorizator();
        }
        return categorizator;
    }

    private class Item implements IRxModelItem {

        private String id;
        private Set<Category> categories;

        public Item(String id) {
            this.id = id;
            this.categories = new HashSet<>();
        }

        @Override
        public String getId() {
            return id;
        }

        public void addCategory(Category category) {
            categories.add(category);
        }

        public Set<Category> getCategories() {
            return categories;
        }
    }

    private class ItemCategorizator implements ICategorizator<Item, Category> {
        @Override
        public Set<Category> categorize(Item item) {
            return item.getCategories();
        }
    }

    private enum Category {
        Category1,
        Category2,
        Category3
    }
}