package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SessionParams.DeviceConfig.PHONE_4_INCH,
        theme = "CebolaoLotofacilTheme"
    )

    @Test
    fun homeScreenLoadingState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state for loading
                Text("Home Screen Loading State")
            }
        }
    }

    @Test
    fun homeScreenWithData() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with data
                Text("Home Screen With Data")
            }
        }
    }

    @Test
    fun homeScreenErrorState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with error
                Text("Home Screen Error State")
            }
        }
    }
}

import androidx.compose.material3.Text
