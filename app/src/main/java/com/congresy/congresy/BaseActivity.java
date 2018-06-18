package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    protected void loadDrawer(int layout) {

        setContentView(layout);

        ListView mDrawerList = findViewById(R.id.navList);

        List<String> osArray = new ArrayList<>();

        //if(HomeActivity.actor_.getRole().equals("User")){
            osArray.add("Profile");
            osArray.add("My social networks");
            osArray.add("All conferences");
            osArray.add("My conferences");
            osArray.add("My events");
        //}

        /*if(HomeActivity.actor_.getRole().equals("Organizator")){
            osArray.add("Profile");
            osArray.add("My social networks");
            osArray.add("My conferences");
            osArray.add("My events");
            osArray.add("Create conference");

        }*/

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(BaseActivity.this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.bringToFront();

        //if(HomeActivity.actor_.getRole().equals("User")) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(BaseActivity.this, ShowMySocialNetworksActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(BaseActivity.this, ShowAllConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 4) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyEventsActivity.class);
                        startActivity(intent);
                    }
                }
            });
        //}

        /*if(HomeActivity.actor_.getRole().equals("Organizator")) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(BaseActivity.this, ShowMySocialNetworksActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyConferencesActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(BaseActivity.this, ShowMyEventsActivity.class);
                        startActivity(intent);
                    } else if (position == 4) {
                        Intent intent = new Intent(BaseActivity.this, CreateConferenceActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index_menu_options_logged, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        Call call = ApiUtils.getUserService().logout();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("Username");
                editor.remove("Password");
                editor.putInt("logged", 0);
                editor.apply();

                startActivity(new Intent(BaseActivity.this, IndexActivity.class));
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BaseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}