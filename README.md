# RxModel

A simple reactive data model that supports both ID and category data access. 

Add items:
```java
model.submitItem(item);
model.submitItems(new List<Item>());
```

Get them back:
```java
model.getItem("item ID")
```

Subscribe for item changes by ID or a custom item category:
```java
model.subscribeItem("1").subscribe(new Observer() {/*...*/});
model.subscribeCategory(Category.Category1).subscribe(new Observer(){/*...*/});
```

You can read more here https://medium.com/@ikkondratov/2-minutes-reactive-model-4135089bebff or check https://github.com/Cryosleeper/RxModel/blob/master/rx-model/src/main/java/com/cryosleeper/rxmodel/IRxModel.java to get the idea. Repository includes a simple test app and a test coverage https://github.com/Cryosleeper/RxModel/blob/master/rx-model/src/test/java/com/cryosleeper/rxmodel/IRxModelTest.java

It is also available via jcenter:
```gradle
implementation 'com.cryosleeper.android:rx-model:1.0.1'
```
