package com.cebolao.lotofacil

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CheckerScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        // Navega para a tela do Conferidor antes de cada teste
        composeTestRule.onNodeWithText("Conferidor").performClick()
    }

    @Test
    fun checkerScreen_when15NumbersAreSelected_checkButtonIsEnabled() {
        // Verifica o estado inicial do botão
        composeTestRule.onNodeWithText("Conferir jogo").assertIsNotEnabled()

        // Seleciona 15 números
        for (i in 1..LotofacilConstants.GAME_SIZE) {
            composeTestRule.onNodeWithText("%02d".format(i)).performClick()
        }

        // Verifica se o progresso foi atualizado
        composeTestRule.onNodeWithText("15 de 15 números").assertIsDisplayed()

        // Verifica se o botão "Conferir jogo" agora está habilitado
        composeTestRule.onNodeWithText("Conferir jogo").performScrollTo().assertIsEnabled()
    }

    @Test
    fun checkerScreen_whenCheckButtonIsClicked_resultIsShown() {
        // Seleciona 15 números
        for (i in 1..LotofacilConstants.GAME_SIZE) {
            composeTestRule.onNodeWithText("%02d".format(i)).performClick()
        }

        // Clica para conferir
        composeTestRule.onNodeWithText("Conferir jogo").performScrollTo().performClick()

        // Aguarda e verifica se o resultado (ou o cabeçalho do resultado) é exibido.
        // Verifica múltiplos indicadores possíveis de sucesso
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            val nodes = composeTestRule.onAllNodesWithText("Análise de Desempenho").fetchSemanticsNodes()
            nodes.isNotEmpty() || 
            composeTestRule.onAllNodesWithText("Estatísticas do jogo").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Prêmios").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Pelo menos um dos indicadores deve estar presente
        try {
            composeTestRule.onNodeWithText("Análise de Desempenho").assertIsDisplayed()
        } catch (e: AssertionError) {
            try {
                composeTestRule.onNodeWithText("Estatísticas do jogo").assertIsDisplayed()
            } catch (e2: AssertionError) {
                composeTestRule.onNodeWithText("Prêmios").assertIsDisplayed()
            }
        }
    }

    @Test
    fun checkerScreen_clearButtonResetsSelection() {
        // Seleciona alguns números
        composeTestRule.onNodeWithText("01").performClick()
        composeTestRule.onNodeWithText("02").performClick()
        composeTestRule.onNodeWithText("03").performClick()

        // Verifica se o botão de limpar está visível
        composeTestRule.onNodeWithText("Limpar seleção").performScrollTo().assertIsDisplayed()

        // Clica no botão de limpar
        composeTestRule.onNodeWithText("Limpar seleção").performScrollTo().performClick()

        // Confirma a limpeza no diálogo
        composeTestRule.onNodeWithText("Limpar").performClick()

        // Aguarda um pouco para a limpeza ser processada
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("0 de 15 números").fetchSemanticsNodes().isNotEmpty()
        }

        // Verifica se o progresso foi resetado
        composeTestRule.onNodeWithText("0 de 15 números").assertIsDisplayed()

        // Verifica se o botão de conferir está desabilitado
        composeTestRule.onNodeWithText("Conferir jogo").assertIsNotEnabled()
    }
}
