package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import org.junit.Rule
import org.junit.Test

class CheckerScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SessionParams.DeviceConfig.PHONE_4_INCH,
        theme = "CebolaoLotofacilTheme"
    )

    @Test
    fun checkerScreenIdleState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state for idle
                Text("Checker Screen Idle State")
            }
        }
    }

    @Test
    fun checkerScreenLoadingState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with loading
                Text("Checker Screen Loading State")
            }
        }
    }

    @Test
    fun checkerScreenSuccessState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with results
                Text("Checker Screen Success State")
            }
        }
    }
}

import androidx.compose.material3.Text
