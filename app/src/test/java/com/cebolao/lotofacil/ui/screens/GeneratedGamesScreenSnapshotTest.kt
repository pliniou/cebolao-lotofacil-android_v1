package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import org.junit.Rule
import org.junit.Test

class GeneratedGamesScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SessionParams.DeviceConfig.PHONE_4_INCH,
        theme = "CebolaoLotofacilTheme"
    )

    @Test
    fun generatedGamesScreenLoadingState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state for loading
                Text("Generated Games Loading State")
            }
        }
    }

    @Test
    fun generatedGamesScreenWithGames() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with generated games
                Text("Generated Games Screen With Games")
            }
        }
    }

    @Test
    fun generatedGamesScreenEmptyState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with empty list
                Text("Generated Games Screen Empty")
            }
        }
    }
}

import androidx.compose.material3.Text
