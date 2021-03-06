package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.SpeakersOfEventListAdapter;
import com.congresy.congresy.adapters.SpeakersOfEventListUserAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowSpeakersOfEventActivity extends BaseActivity {

    UserService userService;

    Button btnAddSpeakers;

    ListView listView;

    public static String event_;

    public static List<Actor> speakers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Event speakers");

        Intent myIntent = getIntent();
        String comeFrom = myIntent.getExtras().get("comeFrom").toString();

        userService = ApiUtils.getUserService();

        if (comeFrom.equals("user")) {
            loadDrawer(R.layout.activity_show_speakers_of_event_user);

            listView = findViewById(R.id.listView);

            loadSpeakers();
        } else if (comeFrom.equals("organizator")){
            loadDrawer(R.layout.activity_show_speakers_of_event);

            btnAddSpeakers = findViewById(R.id.addSpeakers);
            listView = findViewById(R.id.listView);

            btnAddSpeakers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowSpeakersOfEventActivity.this, SearchSpeakersActivity.class);
                    intent.putExtra("idEvent", event_);
                    startActivity(intent);
                }
            });

            loadSpeakers();
        }

    }

    private void loadSpeakers(){
        Intent myIntent = getIntent();
        final String id = myIntent.getExtras().get("idEvent").toString();

        event_ = id;

        Call<List<Actor>> call = userService.getSpeakersOfEvent(id);
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if(response.isSuccessful()){

                    Intent myIntent = getIntent();
                    String comeFrom = myIntent.getExtras().get("comeFrom").toString();

                    final List<Actor> speakers = response.body();

                    ListView lv = findViewById(R.id.listView);

                    if (comeFrom.equals("user")){
                        SpeakersOfEventListUserAdapter adapter = new SpeakersOfEventListUserAdapter(getApplicationContext(), speakers);
                        lv.setAdapter(adapter);
                    } else if (comeFrom.equals("organizator")) {
                        SpeakersOfEventListAdapter adapter = new SpeakersOfEventListAdapter(getApplicationContext(), speakers);
                        lv.setAdapter(adapter);
                    }

                   lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                           myIntent.putExtra("goingTo", "Speaker");
                           myIntent.putExtra("idSpeaker", speakers.get(position).getId());
                           getApplication().startActivity(myIntent);
                       }
                   });


                } else {
                    Toast.makeText(ShowSpeakersOfEventActivity.this, "This event has no speakers!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ShowSpeakersOfEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
