package net.bplaced.fabianmainz.easynotes;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int selectedEntry = 0;
    public static ArrayList<ArrayList<String>> todo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        final ListView listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Dialog dialogNew = new Dialog(this);
        final Dialog dialogDone = new Dialog(this);
        dialogNew.setContentView(R.layout.dialog_add);
        dialogDone.setContentView(R.layout.dialog_done);

        final EditText newNameN = (EditText) dialogNew.findViewById(R.id.editViewName);
        final EditText newDescN = (EditText) dialogNew.findViewById(R.id.editViewDesc);

        final Button addButton = (Button) dialogNew.findViewById(R.id.buttonAdd);
        final Button doneButton = (Button) dialogDone.findViewById(R.id.buttonDone);
        final Button dismissButton = (Button) dialogDone.findViewById(R.id.buttonDismiss);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> a = new ArrayList<>();
                a.add(newNameN.getText().toString());
                a.add(newDescN.getText().toString());
                todo.add(a);
                listView.requestLayout();
                dialogNew.dismiss();
                saveData();
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDone.dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
                saveData();
            }
        });

        final TextView newNameD = (TextView) dialogDone.findViewById(R.id.textViewName);
        final TextView newDescD = (TextView) dialogDone.findViewById(R.id.textViewDesc);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogNew.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogNew.show();
                dialogNew.getWindow().setAttributes(lp);
            }
        });

        listView.setAdapter(new TextAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newDescD.setText(todo.get(position).get(1));
                newNameD.setText(todo.get(position).get(0));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogDone.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogDone.show();
                dialogDone.getWindow().setAttributes(lp);
                selectedEntry = position;
            }
        });


    }

    private void saveData() {
        Gson gson = new Gson();
        String save = gson.toJson(todo);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d("saved", save);
        editor.putString("todo",save);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String todoString = preferences.getString("todo", null);
        if (todoString != null) {
            Log.d("found", todoString);
            Gson gson = new Gson();
            ArrayList<ArrayList<String>> arraylist = gson.fromJson(todoString, ArrayList.class);
            todo = arraylist;
        }
    }

    private void deleteEntry() {
        todo.remove(selectedEntry);
    }


    private class TextAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;

        public TextAdapter(Context ctx) {
            this.context = ctx;
            layoutInflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return todo.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewGroup row = (ViewGroup) layoutInflater.inflate(R.layout.listview, null);

            TextView name = (TextView) row.findViewById(R.id.name);
            name.setText(todo.get(position).get(0));
            TextView desc = (TextView) row.findViewById(R.id.desc);
            desc.setText(todo.get(position).get(1));


            return row;
        }
    }


}
