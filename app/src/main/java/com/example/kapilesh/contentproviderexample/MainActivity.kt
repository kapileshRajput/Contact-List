package com.example.kapilesh.contentproviderexample

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    val contact_names by lazy { findViewById<ListView>(R.id.contact_names) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Log.d(TAG, "onCreate: onclick => starts")
            val projection =
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY) // this is just the name of the column in the contacts table.

            // we get contentResolver from the activity using getContentResolver() but
            // as this is kotlin we could use property directly without using getter.
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            )
            // params: 1 => URI = Identifies the source of our data
            // 2 => stringArray holding the names of columns we want to retrive
            // 3 => selection = filter to determine which rows to return (like a where clause in SQL statement).
            // setting it to null means return all rows.
            // 4 =>selectionArgs = array of values that will be used to replace placeholders in the selection string.
            // ex: selection = "name = ?" & selectionArgs could contain single value "Frodo"
            // 5 => sortOrder = string containing names of the fields you want the data sorted by. (ORDER BY clause)

            val contacts = ArrayList<String>() // create a list to hold our contacts.
            cursor?.use {
                // loop through the cursor
                while (it.moveToNext()) {
                    contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                }
            }

            val adapter = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
            contact_names.adapter = adapter

            Log.d(TAG, "onCreate: onclick => ends")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}