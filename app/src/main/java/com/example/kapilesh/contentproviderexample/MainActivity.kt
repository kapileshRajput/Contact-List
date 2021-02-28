package com.example.kapilesh.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {
    private var readGranted = false
    private val contactNames: ListView by lazy { findViewById<ListView>(R.id.contact_names) }
    private val fab: FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.fab) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val hasReadContactPermission =
            ContextCompat.checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, "onCreate: Has Permission to read contacts ==>> $hasReadContactPermission")

        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission Granted")
            readGranted = true // ToDo: don't do this
        } else {
            Log.d(TAG, "onCreate: Permission Denied")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }

        fab.setOnClickListener { view ->
            if (readGranted) {
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

                    val adapter =
                        ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
                    contactNames.adapter = adapter
                }
            } else {
                Snackbar.make(view, "Please grant access to your Contacts", Snackbar.LENGTH_LONG).setAction("Action", null)
                    .show()
            }


            Log.d(TAG, "onCreate: onclick => ends")
        }

        Log.d(TAG, "onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: starts")
        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                readGranted =
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted.
                        // Do the contacts related tast we need to do.
                        Log.d(TAG, "onRequestPermissionsResult: permission granted")
                        true
                    } else {
                        // permission denied
                        // disable the functionallity that depends on this permission
                        Log.d(TAG, "onRequestPermissionsResult: permission refused")
                        false
                    }
//                fab.isEnabled = readGranted
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: ends")
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