package com.adwaymalhotra.parkstash.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import com.adwaymalhotra.parkstash.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by adway on 27/03/18.
 */

public class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var t = toolbar

        setSupportActionBar(t)
        supportActionBar!!.title = "ParkStash"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)

        supportFragmentManager.beginTransaction().replace(R.id.container, MapFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            drawerLayout.openDrawer(Gravity.START)
        }
        return super.onOptionsItemSelected(item)
    }
}