package com.francescsoftware.circuitsample.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.francescsoftware.circuitsample.di.AppScope
import com.francescsoftware.circuitsample.ui.details.DetailsScreen
import com.francescsoftware.circuitsample.ui.details2.Details2Screen
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
object HomeScreen : Screen {

    data class State(
        val quote: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object DetailsClick : Event
        data class Details2Click(
            val firstWord: String,
            val secondWord: String,
        ) : Event

        data object GeneratePrompt : Event
    }
}

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val promptGenerator: PromptGenerator,
) : Presenter<HomeScreen.State> {

    @CircuitInject(HomeScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            navigator: Navigator,
        ): HomePresenter
    }

    @Composable
    override fun present(): HomeScreen.State {
        var prompt by rememberRetained {
            mutableStateOf("")
        }
        return HomeScreen.State(
            quote = prompt,
        ) { event ->
            when (event) {
                HomeScreen.Event.GeneratePrompt -> prompt = promptGenerator.generate()
                HomeScreen.Event.DetailsClick -> navigator.goTo(DetailsScreen)
                is HomeScreen.Event.Details2Click -> navigator.goTo(
                    Details2Screen(
                        event.firstWord,
                        event.secondWord
                    )
                )
            }
        }
    }
}

@CircuitInject(HomeScreen::class, AppScope::class)
@Composable
fun Home(state: HomeScreen.State, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink
    Home(
        quote = state.quote,
        onGenerateClick = { eventSink(HomeScreen.Event.GeneratePrompt) },
        onDetailsClick = { eventSink(HomeScreen.Event.DetailsClick) },
        onDetails2Click = { foo, bar -> eventSink(HomeScreen.Event.Details2Click(foo, bar)) },
        modifier = modifier,
    )
}

@Composable
private fun Home(
    quote: String,
    onGenerateClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onDetails2Click: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Button(onClick = onGenerateClick) {
                Text(
                    text = "Get quote"
                )
            }
            Text(
                text = quote,
                textAlign = TextAlign.Center,
                minLines = 2,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = onDetailsClick
            ) {
                Text(
                    text = "To Details1"
                )
            }
            Button(
                onClick = {
                    val words = quote.split(" ")
                    onDetails2Click(
                        words[0],
                        words[1],
                    )
                },
                enabled = quote.isNotEmpty(),
            ) {
                Text(
                    text = "To Details2"
                )
            }
        }
    }
}

@ContributesMultibinding(AppScope::class)
class HomePresenterFactory2 @Inject constructor(
    private val factory: HomePresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? = when (screen) {
        HomeScreen -> factory.create(navigator = navigator)
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
