package com.example.administrator.slidertest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class NavBaseActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    protected RelativeLayout _completeLayout, _activityLayout;
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;


    final int REQ_CODE_SELECT_IMAGE=100;
<<<<<<< HEAD
    private ParseFile ImageFile;
    Bitmap bmp;
    ParseFile fileObject;
=======
    public ParseFile ImageFile;
    ParseObject ImageValues;//파스 오브젝트 생성
    ImageView image;
    ParseFile fileObject;
    Bitmap bmp;
    Bitmap image_bitmap;
    Intent intent = new Intent(Intent.ACTION_PICK);
    private ProgressDialog progressDialog;
    ParseUser pUser= ParseUser.getCurrentUser();
    String objectId= "O4e8Vn2FCK";
>>>>>>> 821bdee5080b88b6a214038ec783bfaa0b4b49bf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
<<<<<<< HEAD
    }
=======

        // if (savedInstanceState == null) {
        // // on first time display view for first nav item
        // // displayView(0);
        // }
       // progressDialog = ProgressDialog.show(this, "", "Downloading Image...", true);

        // Locate the class table named "ImageUpload" in Parse.com
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("profile_pic");
        // Locate the objectId from the class

        query.getInBackground(objectId, new GetCallback<ParseObject>() {

                    public void done(ParseObject object,
                                     ParseException e) {
                        // TODO Auto-generated method stub

                        // Locate the column named "ImageName" and set
                        // the string
                        ParseFile fileObject = (ParseFile) object.get("profileimage");
                      //  ParseFile fileUser = (ParseFile) object.get("User");
                        //ParseFile fileName = (ParseFile) object.get("Name");


                        fileObject.getDataInBackground(new GetDataCallback() {

                                     public void done(byte[] data, ParseException e) {
                                         if (e == null) {
                                        Log.d("test", "We've got data in data.");
                                        // Decode the Byte[] into
                                        // Bitmap
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        // Get the ImageView from
                                        // main.xml
                                        ImageButton image = (ImageButton) findViewById(R.id.profileimage);

                                        // Set the Bitmap into the
                                        // ImageView
                                        image.setImageBitmap(bmp);

                                        // Close progress dialog
                                        //progressDialog.dismiss();

                                    } else {
                                        Log.d("test",
                                                "There was a problem downloading the data.");
                                    }
                                    }
                                });
                    }
                });





}
>>>>>>> 821bdee5080b88b6a214038ec783bfaa0b4b49bf

    public void profilebutton(View v){
//버튼 클릭시 처리로직
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();

        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
<<<<<<< HEAD
                    //ImageView image = (ImageView) findViewById(R.id.profileimage);

                    //배치해놓은 ImageView에 set
                    //image.setImageBitmap(image_bitmap);
=======

>>>>>>> 821bdee5080b88b6a214038ec783bfaa0b4b49bf

                    ParseObject ImageValues = new ParseObject("profile_pic");//파스 오브젝트 생성
                    ParseUser user = ParseUser.getCurrentUser();
                    ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();
                    image_bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArray2);
                    byte[] image_to_byte = byteArray2.toByteArray();
                    ImageFile = new ParseFile(name_Str, image_to_byte);

                    ImageValues.put("profile" + "image", ImageFile);
                    ImageValues.put("User", user);
                    ImageValues.put("Name", "profile");
                    ImageValues.saveInBackground();
                    objectId = ImageValues.getObjectId();
                    Toast.makeText(this, "텍스트 업로드 완료",Toast.LENGTH_SHORT).show();
                    //finish();
<<<<<<< HEAD
                    fileObject = (ParseFile) ImageValues.get("profileimage");
=======
                    ParseFile fileObject = (ParseFile) ImageValues.get("profileimage");

>>>>>>> 821bdee5080b88b6a214038ec783bfaa0b4b49bf
                    fileObject.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e == null) {
                                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                ImageView image = (ImageView) findViewById(R.id.profileimage);
                                image.setImageBitmap(bmp);
                            } else {
                                Log.d("test", "문제발생");
                            }
                        }

                    });

                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    public void set(String[] navMenuTitles, TypedArray navMenuIcons) {
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        // 네비게이션 헤더 추가.
        View header = getLayoutInflater().inflate(R.layout.nav_header, null);
        mDrawerList.addHeaderView(header, null, false);
        // mDrawerList.setAdapter(adapter);

        // adding nav drawer items
        if (navMenuIcons == null) {
            for (int i = 0; i < navMenuTitles.length; i++) {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i]));
            }
        } else {
            for (int i = 0; i < navMenuTitles.length; i++) {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
            }
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);


        /////////////////*****************************************************************
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setStackedBackgroundDrawable(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setIcon(R.drawable.ic_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.main_menubutton, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:

                break;
            case 1:
                Intent intent1 = new Intent(this, NavActivity1.class);
                startActivity(intent1);
                finish();
                break;
            case 2:
                Intent intent2 = new Intent(this, ChartActivity.class);
                startActivity(intent2);
                finish();
                break;
            case 3:
                Intent intent3 = new Intent(this, NavActivity3.class);
                startActivity(intent3);
                finish();
                break;
            case 4:
                Intent intent4 = new Intent(this, NavActivity3.class);
                startActivity(intent4);
                finish();
                break;
            case 5:
                Intent intent5 = new Intent(this, NavActivity5.class);
                startActivity(intent5);
                finish();
                break;

            default:
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}



