package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.adapters.EventListOrganizatorAdapter;
import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyEventsActivity extends BaseActivity {

    UserService userService;
    private static List<Event> eventsList;

    private String username;
    private String role;

    LinearLayout ll;

    Button btnEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My events");
        loadDrawer(R.layout.activity_show_events_of_conference);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");
        role = sp.getString("Role", "not found");

        userService = ApiUtils.getUserService();

        btnEvents = findViewById(R.id.btnCreateEvent);
        ll = findViewById(R.id.header);
        ll.setVisibility(View.GONE);

        loadMyEvents();
    }

    private void loadMyEvents(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Event>> call = userService.getOwnEvents(idActor);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    if(role.equals("Organizator")){
                        ll.setVisibility(View.VISIBLE);
                    }

                    EventListOrganizatorAdapter adapter = null;
                    ArrayAdapter<Event> adapter1 = null;
                    eventsList = response.body();

                    if(role.equals("Organizator")) {
                        adapter = new EventListOrganizatorAdapter(getApplicationContext(), eventsList);
                    } else {
                        adapter1 = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, eventsList);
                    }

                    final ListView lv = findViewById(R.id.listView);

                    if(role.equals("Organizator")) {
                        lv.setAdapter(adapter);
                    } else {
                        lv.setAdapter(adapter1);
                    }

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ShowMyEventsActivity.this, ShowEventActivity.class);
                            intent.putExtra("idEvent", eventsList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                    btnEvents.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = getIntent();
                            String idConference = myIntent.getExtras().get("idConference").toString();

                            Intent intent = new Intent(ShowMyEventsActivity.this, CreateEventActivity.class);
                            intent.putExtra("idConference", idConference);
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(ShowMyEventsActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                    if(role.equals("Organizator")) {
                        ll.setVisibility(View.VISIBLE);

                        btnEvents.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = getIntent();
                                String idConference = myIntent.getExtras().get("idConference").toString();

                                Intent intent = new Intent(ShowMyEventsActivity.this, CreateEventActivity.class);
                                intent.putExtra("idConference", idConference);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(ShowMyEventsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
}
