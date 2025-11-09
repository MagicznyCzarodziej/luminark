package pl.przemyslawpitus.luminark

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import dagger.hilt.android.AndroidEntryPoint
import pl.przemyslawpitus.luminark.ui.navigation.AppNavigation
import pl.przemyslawpitus.luminark.ui.theme.LuminarkTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionManager by lazy { PermissionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager.ensurePermissions(
            listOf(
                READ_EXTERNAL_STORAGE,
                INTERNET
            )
        )

        setContent {
            LuminarkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape, // Use RectangleShape to fill the whole screen
                    colors = SurfaceDefaults.colors(
                        containerColor = Color(0xFF090A1A)
                    )
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

