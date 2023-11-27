package com.francescsoftware.circuitsample.ui.details2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.francescsoftware.circuitsample.di.AppScope
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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class Details2Screen(
    val firstWord: String,
    val secondWord: String,
) : Screen {
    data class State(
        val firstWord: String,
        val secondWord: String,
        val label: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object Back : Event
    }
}

class Details2Presenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: Details2Screen,
) : Presenter<Details2Screen.State> {

    @CircuitInject(Details2Screen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            navigator: Navigator,
            screen: Details2Screen
        ): Details2Presenter
    }

    @Composable
    override fun present(): Details2Screen.State {
        val label by remember { mutableStateOf("Go Back") }
        val firstWord = screen.firstWord
        val secondWord = screen.secondWord
        return Details2Screen.State(
            firstWord = firstWord,
            secondWord = secondWord,
            label = label
        ) { event ->
            when (event) {
                Details2Screen.Event.Back -> navigator.pop()
            }
        }
    }
}

@CircuitInject(Details2Screen::class, AppScope::class)
@Composable
fun Details(state: Details2Screen.State, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "First word: ${state.firstWord}"
            )
            Text(
                text = "Second word: ${state.secondWord}"
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { state.eventSink(Details2Screen.Event.Back) }
            ) {
                Text(
                    text = "Go Back"
                )
            }
        }
    }
}

@ContributesMultibinding(AppScope::class)
class DetailsPresenterFactory2 @Inject constructor(
    private val factory: Details2Presenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? = when (screen) {
        is Details2Screen -> factory.create(navigator = navigator, screen = screen)
        else -> null
    }
}

@ContributesMultibinding(AppScope::class)
class DetailsFactory2 @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is Details2Screen -> ui<Details2Screen.State> { state, modifier ->
            Details(state = state, modifier = modifier)
        }

        else -> null
    }
}
