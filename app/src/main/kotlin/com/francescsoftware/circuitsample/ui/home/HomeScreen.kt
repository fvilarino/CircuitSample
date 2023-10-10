package com.francescsoftware.circuitsample.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.francescsoftware.circuitsample.di.AppScope
import com.francescsoftware.circuitsample.ui.details.DetailsScreen
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.random.Random

private val Prompts = listOf(
    "A rose by any other name would smell as sweet",
    "All that glitters is not gold",
    "All the world’s a stage, and all the men and women merely players",
    "Ask not what your country can do for you; ask what you can do for your country",
    "Ask, and it shall be given you; seek, and you shall find",
    "Eighty percent of success is showing up",
    "Elementary, my dear Watson",
    "For those to whom much is given, much is required",
    "Frankly, my dear, I don't give a damn",
    "Genius is one percent inspiration and ninety-nine percent perspiration",
    "Go ahead, make my day",
    "He travels the fastest who travels alone",
    "Hell has no fury like a woman scorned",
    "Hell is other people",
    "Here's looking at you, kid",
    "Houston, we have a problem."
)

@Parcelize
object HomeScreen : Screen {

    sealed interface Prompt {
        data object Idle : Prompt
        data object Loading : Prompt
        data class Loaded(val prompt: String) : Prompt
    }

    data class State(
        val prompt: Prompt,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object DetailsClick : Event
        data class QueryUpdated(val query: String) : Event
    }
}

class PromptGenerator {

    private val query = MutableStateFlow("")

    val prompt = query
        .debounce(400L)
        .distinctUntilChanged()
        .flatMapLatest {
            flow {
                if (it.length < 3) {
                    emit(HomeScreen.Prompt.Idle)
                } else {
                    emit(HomeScreen.Prompt.Loading)
                    delay(1000L)
                    emit(
                        HomeScreen.Prompt.Loaded(
                            Prompts[Random.nextInt(Prompts.size)]
                        )
                    )
                }
            }
        }

    fun setQuery(query: String) {
        this.query.value = query
    }
}

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
) : Presenter<HomeScreen.State> {

    private val promptGenerator = PromptGenerator()

    @CircuitInject(HomeScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            navigator: Navigator,
        ): HomePresenter
    }

    @Composable
    override fun present(): HomeScreen.State {
        val prompt by promptGenerator.prompt.collectAsRetainedState(initial = HomeScreen.Prompt.Idle)
        return HomeScreen.State(
            prompt = prompt,
        ) { event ->
            when (event) {
                HomeScreen.Event.DetailsClick -> navigator.goTo(DetailsScreen)
                is HomeScreen.Event.QueryUpdated -> promptGenerator.setQuery(event.query)
            }
        }
    }
}

@CircuitInject(HomeScreen::class, AppScope::class)
@Composable
fun Home(state: HomeScreen.State, modifier: Modifier = Modifier) {
    var query by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { query }
            .collectLatest { state.eventSink(HomeScreen.Event.QueryUpdated(it)) }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val text = when (state.prompt) {
            HomeScreen.Prompt.Idle,
            HomeScreen.Prompt.Loading -> ""

            is HomeScreen.Prompt.Loaded -> state.prompt.prompt
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                label = {
                    Text(text = "Search")
                }
            )
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(
                onClick = { state.eventSink(HomeScreen.Event.DetailsClick) }
            ) {
                Text(
                    text = "To Details"
                )
            }
        }
        if (state.prompt is HomeScreen.Prompt.Loading) {
            CircularProgressIndicator()
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
