package com.francescsoftware.circuitsample.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
object DetailsScreen : Screen {
    data class State(
        val label: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object Back : Event
    }
}

class DetailsPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
) : Presenter<DetailsScreen.State> {

    @CircuitInject(DetailsScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            navigator: Navigator,
        ): DetailsPresenter
    }

    @Composable
    override fun present(): DetailsScreen.State {
        val label by remember { mutableStateOf("Go Back") }
        return DetailsScreen.State(
            label = label
        ) { event ->
            when (event) {
                DetailsScreen.Event.Back -> navigator.pop()
            }
        }
    }
}

@CircuitInject(DetailsScreen::class, AppScope::class)
@Composable
fun Details(state: DetailsScreen.State, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = { state.eventSink(DetailsScreen.Event.Back) }
        ) {
            Text(
                text = "Go Back"
            )
        }
    }
}

@ContributesMultibinding(AppScope::class)
class DetailsPresenterFactory2 @Inject constructor(
    private val factory: DetailsPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? = when (screen) {
        DetailsScreen -> factory.create(navigator = navigator)
        else -> null
    }
}

@ContributesMultibinding(AppScope::class)
class DetailsFactory2 @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        DetailsScreen -> ui<DetailsScreen.State> { state, modifier ->
            Details(state = state, modifier = modifier)
        }

        else -> null
    }
}
