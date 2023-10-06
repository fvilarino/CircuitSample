package com.francescsoftware.circuitsample.ui.home

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.francescsoftware.circuitsample.di.NumberGenerator
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomePresenterTest {

    @Test
    fun `sample test`() = runTest {
        val numberGenerator = object : NumberGenerator {
            override fun generate(): Int {
                return 42
            }
        }
        val presenter = HomePresenter(
            navigator = FakeNavigator(),
            numberGenerator = numberGenerator
        )
        presenter.test {
            var item = awaitItem()
            assertThat(item.label).isEqualTo("")
            item.eventSink(HomeScreen.Event.Generate)
            item = awaitItem()
            assertThat(item.label).isEqualTo("42")
        }
    }
}
