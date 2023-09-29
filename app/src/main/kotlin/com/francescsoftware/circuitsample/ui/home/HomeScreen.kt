package com.francescsoftware.circuitsample.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.francescsoftware.circuitsample.di.AppScope
import com.francescsoftware.circuitsample.di.NumberGenerator
import com.francescsoftware.circuitsample.ui.theme.CircuitSampleTheme
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import javax.inject.Provider

@Parcelize
object HomeScreen : Screen {
    data class State(
        val label: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object Generate : Event
    }
}

@CircuitInject(HomeScreen::class, AppScope::class)
class HomePresenter @Inject constructor(
    private val numberGenerator: NumberGenerator,
) : Presenter<HomeScreen.State> {
    @Composable
    override fun present(): HomeScreen.State {
        var label by remember { mutableStateOf("") }
        return HomeScreen.State(
            label = label
        ) { event ->
            when (event) {
                HomeScreen.Event.Generate -> label = numberGenerator.generate().toString()
            }
        }
    }
}

@CircuitInject(HomeScreen::class, AppScope::class)
@Composable
fun Home(state: HomeScreen.State, modifier: Modifier = Modifier) {
    CircuitSampleTheme {
        Surface {
            Box(
                modifier = modifier,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = state.label,
                        color = Color.Red,
                    )
                    Button(
                        onClick = { state.eventSink(HomeScreen.Event.Generate) }
                    ) {
                        Text(
                            text = "Generate"
                        )
                    }
                }
            }
        }
    }
}

@ContributesMultibinding(AppScope::class)
class HomePresenterFactory2 @Inject constructor(
    private val provider: Provider<HomePresenter>,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? = when (screen) {
        HomeScreen -> provider.get()
        else -> null
    }
}

@ContributesMultibinding(AppScope::class)
class HomeFactory2 @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        HomeScreen -> ui<HomeScreen.State> { state, modifier ->
            Home(state = state, modifier = modifier)
        }

        else -> null
    }
}
