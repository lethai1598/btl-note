package com.example.btl_note;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_note.adapters.NotesAdapter;
import com.example.btl_note.callbacks.MainActionModelCallback;
import com.example.btl_note.callbacks.NoteEventListener;
import com.example.btl_note.db.NotesDB;
import com.example.btl_note.db.NotesDao;
import com.example.btl_note.model.Note;
import com.example.btl_note.utils.NoteUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.btl_note.EditNoteActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NoteEventListener {
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    ArrayList<Note> notes;
    NotesAdapter adapter;
    NotesDao dao;
    MainActionModelCallback actionModelCallback;
    int checkedCount=0;
    FloatingActionButton fab;
    Note temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // init recycleview
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //init fab
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 8/4/2020 add new note
                onAddNewNote();
            }
        });
        dao = NotesDB.getInstance(this).notesDao();
    }
    public void postRequest(final String id, final String text, final String dateNS){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.7:8080/android/insert.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//                System.out.println("ok post"+response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                System.out.println("toang");
//                System.out.println(error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("id",id);
                params.put("noteText",text);
                params.put("noteDate",dateNS);
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void ReadJson(){
        String url = "http://192.168.1.7:8080/android/getnote.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<=response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        int id = object.getInt("id");
                        String notetext = object.getString("noteText");
                        long notedate = object.getLong("noteDate");
                        temp = new Note(id, notetext, notedate);
                        dao.insertNote(temp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                System.out.println("Xảy ra lỗi");
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    public void postDelete(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.7:8080/android/deleteallnote.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                System.out.println("ok post"+response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                System.out.println("toang");
                System.out.println(error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("check","ok");
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list =  dao.getNotes();
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(this, notes);
        //set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
    }

    private void onAddNewNote() {
       startActivity(new Intent(this, EditNoteActivity.class));
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
        if (id == R.id.action_backup) {
            postDelete();
            for (int i=0 ; i < notes.size(); i++){
                String idN = String.valueOf(notes.get(i).getId());
                String text = notes.get(i).getNoteText();
                String dateNS = String.valueOf(notes.get(i).getNoteDate());
                postRequest(idN, text, dateNS);
            }
        }
        if (id == R.id.action_restore){
            Toast.makeText(MainActivity.this, "restore", Toast.LENGTH_SHORT).show();
            dao.deleteAll();
            ReadJson();
            startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    public void onNoteClick(Note note) {
        Intent edit = new Intent(this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNoteLongClick(Note note) {
        note.setChecked(true);
        checkedCount = 1;
        adapter.setMultiCheckMode(true);

        adapter.setListener(new NoteEventListener() {
                    @Override
                    public void onNoteClick(Note note) {
                        note.setChecked(!note.isChecked()); // inverse selected
                        if (note.isChecked())
                            checkedCount++;
                        else checkedCount--;

                        if (checkedCount > 1) {
                            actionModelCallback.changeShareItemVisible(false);
                        } else actionModelCallback.changeShareItemVisible(true);

                        if (checkedCount == 0) {
                            //  finish multi select mode wen checked count =0
                            actionModelCallback.getAction().finish();
                        }

                        actionModelCallback.setCount(checkedCount + "/" + notes.size());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNoteLongClick(Note note) {

                    }
                });
                actionModelCallback = new MainActionModelCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if(menuItem.getItemId()==R.id.action_share_note)
                    onShareNote();
                actionMode.finish();
                return false;
            }
        };


        startActionMode(actionModelCallback);
        fab.setVisibility(View.GONE);
        actionModelCallback.setCount(checkedCount+"/"+notes.size());
    }

    private void onShareNote() {
        // TODO: 9/4/2020 share
        Note note = adapter.getCheckedNotes().get(0);
        Intent share= new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteText()+"\n\n create on: "+NoteUtils.dateFromLong(note.getNoteDate())+"by"+getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);
    }

    private void onDeleteMultiNotes() {
        // TODO: 9/4/2020 delete mutinotes
        List<Note> checkedNotes = adapter.getCheckedNotes();
        if (checkedNotes.size()!=0){
            for (Note note : checkedNotes) {
                dao.deleteNote(note);
            }
            loadNotes();
            Toast.makeText(this, checkedNotes.size()+"Note xoa thanh cong", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this, "Xoa khong thanh cong", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        adapter.setMultiCheckMode(false);
        adapter.setListener(this);
        fab.setVisibility(View.VISIBLE);
    }


}
