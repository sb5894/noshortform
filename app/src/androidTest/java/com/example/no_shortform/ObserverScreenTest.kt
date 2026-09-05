package com.example.no_shortform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.no_shortform.ui.theme.NoshortformTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ObserverScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun selectingModeWhileBlockingIsOffDoesNotToggleBlocking() {
        var mode by mutableStateOf(BlockingMode.ENTRY)
        var blockingChanges = 0
        compose.setContent {
            NoshortformTheme {
                ObserverScreen({}, false, mode, { mode = it }, false, { blockingChanges++ })
            }
        }
        compose.onNodeWithText("스크롤 차단").performScrollTo().performClick().assertIsSelected()
        compose.runOnIdle {
            assertEquals(BlockingMode.LEGACY, mode)
            assertEquals(0, blockingChanges)
        }
        compose.onNodeWithContentDescription("Shorts 차단").performScrollTo().assertIsOff()
    }

    @Test
    fun enabledSettingWithoutServiceShowsSetupNoticeAndOpensSettings() {
        var settingsRequests = 0
        compose.setContent {
            NoshortformTheme {
                ObserverScreen({ settingsRequests++ }, true, BlockingMode.ENTRY, {}, false, {})
            }
        }
        compose.onNodeWithContentDescription("Shorts 차단").assertIsOn()
        compose.onNodeWithText("차단 설정은 켜져 있어요.", substring = true)
            .assertTextContains("사용하려면 접근성 서비스를 켜 주세요.", substring = true)
        compose.onNodeWithText("접근성 설정 열기").performClick()
        compose.runOnIdle { assertEquals(1, settingsRequests) }
    }

    @Test
    fun expandedInformationSurvivesSavedStateRestoration() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            NoshortformTheme {
                ObserverScreen({}, false, BlockingMode.ENTRY, {}, false, {})
            }
        }
        compose.onNodeWithText("정보 및 사용법 펼치기").performScrollTo().performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("데이터 처리").performScrollTo().assertExists()
        compose.onNodeWithText("정보 및 사용법 접기").performScrollTo().performClick()
        compose.onNodeWithText("데이터 처리").assertDoesNotExist()
    }
}
