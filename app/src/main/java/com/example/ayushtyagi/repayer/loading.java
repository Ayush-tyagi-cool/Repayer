package com.example.ayushtyagi.repayer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;


import static android.os.Build.VERSION_CODES.M;


public class loading extends Activity
{
    //creates a ViewSwitcher object, to switch between Views
    private ViewSwitcher viewSwitcher;

    /** Called when the studios.codelight.smartlogin.activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();

    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        //A TextView object and a ProgressBar object
        private TextView tv_progress;
        private ProgressBar pb_progressBar;

        //Before running code in the separate thread
        @Override
        protected void onPreExecute()
        {
            //Initialize the ViewSwitcher object
            viewSwitcher = new ViewSwitcher(loading.this);
	        /* Initialize the loading screen with data from the 'loadingscreen.xml' layout xml file.
	         * Add the initialized View to the viewSwitcher.*/
            viewSwitcher.addView(ViewSwitcher.inflate(loading.this, R.layout.activity_loading, null));

            //Initialize the TextView and ProgressBar instances - IMPORTANT: call findViewById() from viewSwitcher.
            tv_progress = (TextView) viewSwitcher.findViewById(R.id.tv_progress);
            pb_progressBar = (ProgressBar) viewSwitcher.findViewById(R.id.pb_progressbar);
            //Sets the maximum value of the progress bar to 100
            pb_progressBar.setMax(100);

            //Set ViewSwitcher instance as the current View.
            setContentView(viewSwitcher);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
			/* This is just a code that delays the thread execution 4 times,
			 * during 850 milliseconds and updates the current progress. This
			 * is where the code that is going to be executed on a background
			 * thread must be placed.
			 */
            try
            {
                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than four
                    while(counter <= 100)
                    {
                        //Wait 850 milliseconds
                        this.wait(70);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter*1);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //Update the TextView and the progress at progress bar
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //Update the progress at the UI if progress value is smaller than 100
            if(values[0] <= 100)
            {
                tv_progress.setText("Progress: " + Integer.toString(values[0]) + "%");
                pb_progressBar.setProgress(values[0]);
            }
        }

        //After executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            PrefManager prefManager = new PrefManager(getApplicationContext());

            // make first time launch TRUE
            prefManager.setFirstTimeLaunch(true);

            startActivity(new Intent(loading.this,WelcomeActivity.class));
            finish();
        }
    }

    //Override the default back key behavior
    @Override
    public void onBackPressed()
    {
        //Emulate the progressDialog.setCancelable(false) behavior
        //If the first view is being shown
        if(viewSwitcher.getDisplayedChild() == 0)
        {
            //Do nothing
            return;
        }
        else
        {
            //Finishes the current Activity
            super.onBackPressed();
        }
    }
}
