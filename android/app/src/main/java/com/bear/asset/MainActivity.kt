package com.bear.asset

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.bear.asset.data.local.TokenManager
import com.bear.asset.ui.navigation.AppNavigation
import com.bear.asset.ui.theme.BearAssetTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BearAssetTheme {
                AppNavigation(tokenManager = tokenManager)
            }
        }
    }
}
