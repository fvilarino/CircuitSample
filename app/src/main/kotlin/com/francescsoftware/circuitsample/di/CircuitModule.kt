package com.francescsoftware.circuitsample.di

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.slack.circuit.backstack.NavDecoration
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.LocalCircuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds

@ContributesTo(AppScope::class)
@Module
interface CircuitModule {
    @Multibinds
    fun presenterFactories(): Set<Presenter.Factory>

    @Multibinds
    fun uiFactories(): Set<Ui.Factory>

    companion object {
        @Provides
        fun provideCircuit(
            presenterFactories: @JvmSuppressWildcards Set<Presenter.Factory>,
            uiFactories: @JvmSuppressWildcards Set<Ui.Factory>,
        ): Circuit = Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .setDefaultNavDecoration(NavDecoration)
            .setOnUnavailableContent { screen, modifier ->
                val circuit = LocalCircuit.current
                Column(
                    modifier = modifier.background(Color.Red).padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Can't locate route [${screen.javaClass.name}]",
                        color = Color.White,
                    )
                    val presenters = circuit?.newBuilder()?.presenterFactories.orEmpty()
                    val views = circuit?.newBuilder()?.uiFactories.orEmpty()
                    Text(
                        text = "Presenters factories [${presenters.size}]",
                        color = Color.White,
                    )
                    presenters.forEach { factory ->
                        Text(
                            text = "Factory $factory",
                            color = Color.White,
                        )
                    }
                    Text(
                        text = "UI factories [${views.size}]",
                        color = Color.White,
                    )
                    views.forEach { factory ->
                        Text(
                            text = "Factory $factory",
                            color = Color.White,
                        )
                    }
                }
            }
            .build()
    }
}

object NavDecoration : NavDecoration {

    @Composable
    override fun <T> DecoratedContent(
        arg: T,
        backStackDepth: Int,
        modifier: Modifier,
        content: @Composable (T) -> Unit,
    ) {
        AnimatedContent(
            targetState = arg,
            modifier = modifier,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "nav_transition",
        ) {
            content(it)
        }
    }
}
