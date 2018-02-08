package com.cryosleeper.rxmodeltestapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cryosleeper.rxmodel.RxModel;

import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private RxModel<IntegerItem, IntegerItem.IntegerCategory> model;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int value = counter;
                Snackbar.make(view, "Added: " + counter, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                model.delete(String.valueOf(value));
                                counter = value;
                            }
                        }).show();
                model.submitItem(new IntegerItem(counter++));
            }
        });

        model = new RxModel<>(new IntegerCategorizator());
        model.subscribeCategory(IntegerItem.IntegerCategory.Odd).subscribe(new Consumer<List<IntegerItem>>() {
            @Override
            public void accept(List<IntegerItem> integerItems) throws Exception {
                Toast.makeText(MainActivity.this, "Odds are now " + integerItems, Toast.LENGTH_LONG).show();
            }
        });

        model.subscribeCategory(IntegerItem.IntegerCategory.Even).subscribe(new Consumer<List<IntegerItem>>() {
            @Override
            public void accept(List<IntegerItem> integerItems) throws Exception {
                Toast.makeText(MainActivity.this, "Evens are now " + integerItems, Toast.LENGTH_LONG).show();
            }
        });
        model.subscribeItem("10").subscribe(new Consumer<IntegerItem>() {
            @Override
            public void accept(IntegerItem integerItem) throws Exception {
                TextView text = findViewById(R.id.text_view);
                text.setText("Wow, ten is finally here!");
                text.setTextColor(Color.RED);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
