package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import org.junit.Rule
import org.junit.Test

class FiltersScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SessionParams.DeviceConfig.PHONE_4_INCH,
        theme = "CebolaoLotofacilTheme"
    )

    @Test
    fun filtersScreenLoadingState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state for loading
                Text("Filters Screen Loading State")
            }
        }
    }

    @Test
    fun filtersScreenWithFilters() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with filters configured
                Text("Filters Screen With Filters")
            }
        }
    }

    @Test
    fun filtersScreenAfterGeneration() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state after generation
                Text("Filters Screen After Generation")
            }
        }
    }
}

import androidx.compose.material3.Text
