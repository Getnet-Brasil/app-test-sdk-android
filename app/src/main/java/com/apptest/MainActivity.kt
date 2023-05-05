package com.apptest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.getnetpd.interfaces.GPDCallback
import com.getnetpd.ui.GPDCheckoutActivity
import com.getnetpd.ui.GPDVaultActivity
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject

class MainActivity : AppCompatActivity(), GPDCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_teste)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_subscription, R.id.nav_recurrence_customer, R.id.nav_recurrence_plan
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_teste)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onUnauthorizedResponse(activity: AppCompatActivity) {
        if (activity is GPDCheckoutActivity) {
            GPDCheckoutActivity.requestFinish.call()
        }
        if (activity is GPDVaultActivity) {
            GPDVaultActivity.requestFinish.call()
        }
    }

    override fun closedWithSuccess(details: JsonObject?) {
        Log.d("=====", "Closed with success")
    }

    override fun closedWithError(details: JsonObject?) {
        Log.d("=====", "Closed with error [$details]")
    }

    override fun closedByUser() {
        Log.d("=====", "Closed by user")
    }
}
