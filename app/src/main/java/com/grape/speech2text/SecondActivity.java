package com.grape.speech2text;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity
{
    protected static final int RESULT_SPEECH = 1;
    FloatingActionButton fab_mic,fab_save,fab_export;
    public EditText output,fileNameEntry;
    public static String s1 = "";
    public String s2 = "";
    public boolean internal = false;
    Spinner spinner;
    String extension;

    InputMethodManager imm;

    CardView card_rate_app, card_share_app, card_facebook;
    CardView card_twitter, card_visit_site, card_more_apps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        output = (EditText)findViewById(R.id.editTextContent);
        fileNameEntry = (EditText)findViewById(R.id.editTextFileName);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        spinner = (Spinner)findViewById(R.id.spinner);
        output.setBackgroundColor(0);

        fab_mic = (FloatingActionButton) findViewById(R.id.fab1);
        fab_export = (FloatingActionButton) findViewById(R.id.fab2);
        fab_save = (FloatingActionButton) findViewById(R.id.fab3);

        String fileExtensions[] = this.getResources().getStringArray(R.array.file_extensions);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fileExtensions);
        spinner.setAdapter(arrayAdapter);


        fab_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                tog();
            }
        });

        fab_mic.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                output.requestFocus();
                imm.showSoftInput(output, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });

        fab_export.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                export();
            }
        });

        fab_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                save();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch(position)
                {
                    case 0:
                        extension = ".txt";
                        break;
                    case 1:
                        extension = ".pdf";
                        break;
                    default:
                        extension = ".txt";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                extension = ".txt";
            }
        });
    }

    public void tog()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try{
            startActivityForResult(intent,RESULT_SPEECH);
            output.setText("");
        }
        catch(ActivityNotFoundException a)
        {
            Toast t = Toast.makeText(getApplicationContext(),
                    "OOPS!!! your device does not support speech to text",Toast.LENGTH_SHORT );
            t.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && null!=data)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    s1 = s1 + " "+ text.get(0).toString()+ " ";
                    output.setText(s1);
                    output.requestFocus();
                    imm.showSoftInput(output, InputMethodManager.SHOW_IMPLICIT);
                }

                break;
        }

    }

    public void save()
    {
        String s3 = output.getText().toString();
        s1 = s3;
        Toast.makeText(this, "Content Saved", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SdCardPath")
    public void export()
    {
        //Toast.makeText(this, "image clicked", Toast.LENGTH_LONG).show();

        try
        {
            //File myfile = new File("/sdcard/" + et1.getText().toString());

            if(Environment.isExternalStorageRemovable())
            {
                Log.e("SD card", "device has sd card");

                String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.e("SD card path", directory);

                if(fileNameEntry.getText().toString().trim().length() != 0)
                {


                    if(extension.equals(".txt"))
                    {
                        File mydirectory = new File(directory + "/GrapeSpeech2Text/Text/");
                        mydirectory.mkdirs();

                        File myfile = new File(mydirectory, "/" +fileNameEntry.getText().toString().trim() + extension);
                        myfile.createNewFile();

                        FileOutputStream fos = new FileOutputStream(myfile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
                        myOutWriter.append(s1);
                        myOutWriter.close();
                        fos.close();
                        Toast.makeText(this, "File write success", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, fileNameEntry.getText().toString() + extension +
                                " was saved at "+ directory + "/GrapeSpeech2Text/Text/",Toast.LENGTH_LONG).show();
                        //FileOutputStream fileOutputStream = this.openFileOutput(fileNameEntry.getText().toString(),Context.MODE_PRIVATE);
                        //ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        //objectOutputStream.writeObject(output.getText().toString());
                        //objectOutputStream.close();
                        //Toast.makeText(this,"File write success",Toast.LENGTH_SHORT);
                    }
                    else if(extension.equals(".pdf"))
                    {

                        File mydirectory = new File(directory + "/GrapeSpeech2Text/PDF/");
                        mydirectory.mkdirs();

                        File myfile = new File(mydirectory, "/" +fileNameEntry.getText().toString().trim() + extension);
                        myfile.createNewFile();

                        OutputStream outputStream = new FileOutputStream(myfile);

                        //Step 1
                        Document document = new Document(PageSize.A4);
                        // step 2
                        PdfWriter.getInstance(document, new FileOutputStream(myfile));
                        // step 3
                        document.open();
                        // step 4
                        document.add(new Paragraph(output.getText().toString()));
                        // step 5
                        document.close();

                        outputStream.close();

                        Toast.makeText(this, "File write success", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, fileNameEntry.getText().toString() + extension +
                                " was saved at "+ directory + "/GrapeSpeech2Text/PDF/" ,Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "Please enter a filename", Toast.LENGTH_LONG).show();
                }
            }

            else
            {
                Log.e("No SD card", "Device does not has an SD card");
                //File mydirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                //Environment.get
                //File mydirectory = Environment.getRootDirectory();
                //
                //mydirectory = new File(mydirectory, "/GrapeSpeech2Text/");
                ////mydirectory.mkdirs();
                //
                //if(fileNameEntry.getText().toString().trim().length() != 0)
                //{
                //
                //    File myfile = new File(mydirectory, fileNameEntry.getText().toString().trim() + extension);
                //    boolean didNewFileCreated = myfile.createNewFile();
                //
                //    //if(didNewFileCreated)
                //    //{
                //        FileOutputStream fos = new FileOutputStream(myfile, true);
                //        OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
                //        myOutWriter.append(s1);
                //        myOutWriter.close();
                //        Toast.makeText(this, "File write success", Toast.LENGTH_SHORT).show();
                //        Toast.makeText(this,fileNameEntry.getText().toString() + extension +
                //                " was saved at "+ "Downloads/GrapeSpeech2Text/",Toast.LENGTH_LONG).show();
                //    //}
                //    //else
                //    //{
                //        //Toast.makeText(this,"Could not create file",Toast.LENGTH_LONG).show();
                //    //}
                //
                //}
                //else
                //{
                //    Toast.makeText(this, "Please enter a filename", Toast.LENGTH_LONG).show();
                //}


                if(fileNameEntry.getText().toString().trim().length() != 0)
                {


                    if(extension.equals(".txt"))
                    {

                        File mydirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        Log.e("STORAGE PATH",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                        mydirectory = new File(mydirectory, "/GrapeSpeech2Text/Text/");
                        mydirectory.mkdirs();

                        File myfile = new File(mydirectory, fileNameEntry.getText().toString().trim() + extension);
                        try {
                            myfile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myfile,false);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
                        try {
                            myOutWriter.append(s1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            myOutWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(SecondActivity.this, "File write success", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SecondActivity.this, fileNameEntry.getText().toString() + extension +
                                " was saved at "+
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                                "/GrapeSpeech2Text/Text/", Toast.LENGTH_LONG).show();
                    }
                    else if(extension.equals(".pdf"))
                    {

                        File mydirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        Log.e("STORAGE PATH",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                        mydirectory = new File(mydirectory, "/GrapeSpeech2Text/PDF/");
                        mydirectory.mkdirs();

                        File myfile = new File(mydirectory, fileNameEntry.getText().toString().trim() + extension);
                        try {
                            myfile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //Step 1
                        Document document = new Document(PageSize.A4);
                        // step 2
                        try
                        {
                            PdfWriter.getInstance(document, new FileOutputStream(myfile));
                        }
                        catch (DocumentException e)
                        {
                            e.printStackTrace();
                        }
                        // step 3
                        document.open();
                        // step 4
                        try
                        {
                            document.add(new Paragraph(output.getText().toString()));
                        }
                        catch (DocumentException e)
                        {
                            e.printStackTrace();
                        }
                        // step 5
                        document.close();

                        Toast.makeText(SecondActivity.this, "File write success", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SecondActivity.this, fileNameEntry.getText().toString() + extension +
                                " was saved at " +
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                                "/GrapeSpeech2Text/PDF/" , Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(SecondActivity.this, "Please enter a filename", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this, "File write failure", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("File write failure", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.share_the_content)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, output.getText().toString());
            startActivity(Intent.createChooser(intent,"Share via"));
        }
        if(id == R.id.share_this_app)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello friends!!! Check out this application in play store. \"Material Speech to Text\" - https://play.google.com/store/apps/details?id=com.grape.speech2text");
            startActivity(Intent.createChooser(intent,"Share via"));
        }
        //if(id == R.id.rate_this_app)
        //{
        //    Intent intent = new Intent(Intent.ACTION_VIEW);
        //    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.grape.speech2text"));
        //    startActivity(intent);
        //}
        //if(id == R.id.like_us_on_facebook)
        //{
        //    Intent intent = new Intent(Intent.ACTION_VIEW);
        //    intent.setData(Uri.parse("http://www.facebook.com/GrapeEmbeddedSolutions"));
        //    startActivity(intent);
        //}
        //if(id == R.id.follow_us_on_twitter)
        //{
        //    Intent intent = new Intent(Intent.ACTION_VIEW);
        //    intent.setData(Uri.parse("https://twitter.com/Grape_Labs"));
        //    startActivity(intent);
        //}
        //if(id == R.id.visit_our_website)
        //{
        //    Intent intent = new Intent(Intent.ACTION_VIEW);
        //    intent.setData(Uri.parse("http://www.grape-labs.com"));
        //    startActivity(intent);
        //}
        if(id == R.id.more)
        {
            LayoutInflater inflater = LayoutInflater.from(this);
            View moreMenu = inflater.inflate(R.layout.alert_more_menu, null);

            card_rate_app = (CardView)moreMenu.findViewById(R.id.view1);
            card_share_app = (CardView)moreMenu.findViewById(R.id.view2);
            card_facebook = (CardView)moreMenu.findViewById(R.id.view3);
            card_twitter = (CardView)moreMenu.findViewById(R.id.view4);
            card_visit_site = (CardView)moreMenu.findViewById(R.id.view5);
            card_more_apps = (CardView)moreMenu.findViewById(R.id.view6);


            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(moreMenu);
            builder.setTitle("More...");

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void rate_app(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.grape.speech2text"));
        startActivity(intent);
    }

    public void share_app(View v)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello friends!!! Check out this application in play store. \"Material Speech to Text\" - https://play.google.com/store/apps/details?id=com.grape.speech2text");
        startActivity(Intent.createChooser(intent,"Share via"));
    }
    public void facebook(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.facebook.com/GrapeEmbeddedSolutions"));
        startActivity(intent);
    }

    public void twitter(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://twitter.com/Grape_Labs"));
        startActivity(intent);
    }
    public void visit_site(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.grape-labs.com"));
        startActivity(intent);
    }
    public void more_apps(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Grape+Labs"));
        startActivity(intent);
    }

}
