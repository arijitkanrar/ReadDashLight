package com.maniacapps.readdashlight;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DashScanner extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    final int IMAGE_NOT_TAKEN  = 0;
    final int IMAGE_CAPTURED_CAMERA  = 1;
    final int IMAGE_CHOSEN_GALLERY  = 2;

    //picture request codes
    final int IMAGE_CAPTURE_REQUEST = 100;
    final int GALLERY_CHOOSE_REQUEST = 200;

    private Button btnTakePic;
    private Button btnChooseGallery;
    private int imgCaptResult;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_scanner);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        btnTakePic = (Button)findViewById(R.id.btnTakePicture);
        btnChooseGallery = (Button)findViewById(R.id.btnChooseGalleryPic);

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCameraImage();
            }
        });

        btnChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        imgCaptResult = IMAGE_NOT_TAKEN;
    }

    private Uri getImageFileDirectory() {
        File stoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DashScanner");

        if(!stoDir.exists()){
            if(!stoDir.mkdirs()){
                Log.d("DashScanner App Status", "Unable to create app dir");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(stoDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        Log.i("DashScanner App Status", "File: " + mediaFile.getAbsolutePath());
        return Uri.fromFile(mediaFile);
    }

    private void getCameraImage() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgUri = getImageFileDirectory();
        i.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);

        startActivityForResult(i, IMAGE_CAPTURE_REQUEST);
    }

    private void chooseImageFromGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i,GALLERY_CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("DashScanner App Status","Result code: " + resultCode );
            if (requestCode == IMAGE_CAPTURE_REQUEST) {
                Log.d("DashScanner App Status", "Result code is " + resultCode);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Image saved", Toast.LENGTH_LONG).show();
                    imgCaptResult = IMAGE_CAPTURED_CAMERA;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Image capture cancelled by user", Toast.LENGTH_SHORT).show();
                    Log.d("DashScanner App Status", "Image capture cancelled by user");
                    imgCaptResult = IMAGE_NOT_TAKEN;
                } else {
                    Toast.makeText(this, "Image capture failed for unknown reasons", Toast.LENGTH_SHORT).show();
                    Log.d("DashScanner App Status", "Image capture failed for unknown reasons");
                    imgCaptResult = IMAGE_NOT_TAKEN;
                }
            }else if (requestCode == GALLERY_CHOOSE_REQUEST){
                if (resultCode == RESULT_OK) {
                    Uri selImage = data.getData();

                    Toast.makeText(getApplicationContext(), "Gallery Image chosen: " + selImage.toString(), Toast.LENGTH_LONG).show();
                    Log.d("DashScanner App Status", "Gallery Image chosen: " + selImage.toString());
                    imgUri = selImage;
                    imgCaptResult = IMAGE_CHOSEN_GALLERY;
                }else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
                    Log.d("DashScanner App Status", "No Image chosen");
                    imgCaptResult = IMAGE_NOT_TAKEN;
                }else {
                    Toast.makeText(this, "Choosing image failed for unknown reasons", Toast.LENGTH_SHORT).show();
                    Log.d("DashScanner App Status", "Choosing image failed for unknown reasons");
                    imgCaptResult = IMAGE_NOT_TAKEN;
                }
            }

            if(imgCaptResult == IMAGE_CAPTURED_CAMERA || imgCaptResult == IMAGE_CHOSEN_GALLERY){
                handleSelectedImage(imgUri);
            }

        }
        catch(Exception e){
            Log.d("ReadLights exception", e.getMessage());
        }
    }

    private void handleSelectedImage(Uri imageUri){

            Intent i = new Intent(getApplicationContext(),HandleCapturedImage.class);
            i.putExtra("ImageURI", imageUri.toString());
            startActivity(i);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dash_scanner, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dash_scanner, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashScanner) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
