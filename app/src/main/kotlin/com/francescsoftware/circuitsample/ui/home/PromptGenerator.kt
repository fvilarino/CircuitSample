package com.francescsoftware.circuitsample.ui.home

import com.francescsoftware.circuitsample.di.AppScope
import com.francescsoftware.circuitsample.di.SingleIn
import javax.inject.Inject
import kotlin.random.Random

private val Prompts = listOf(
    "A rose by any other name would smell as sweet.",
    "All that glitters is not gold.",
    "All the world’s a stage, and all the men and women merely players.",
    "Ask not what your country can do for you; ask what you can do for your country.",
    "Ask, and it shall be given you; seek, and you shall find.",
    "Eighty percent of success is showing up.",
    "Elementary, my dear Watson.",
    "For those to whom much is given, much is required.",
    "Frankly, my dear, I don't give a damn.",
    "Genius is one percent inspiration and ninety-nine percent perspiration.",
    "Go ahead, make my day.",
    "He travels the fastest who travels alone.",
    "Hell has no fury like a woman scorned.",
    "Hell is other people.",
    "Here's looking at you, kid.",
    "Houston, we have a problem."
)

@SingleIn(AppScope::class)
class PromptGenerator @Inject constructor() {

    fun generate(): String = Prompts[Random.nextInt(Prompts.size)]
}
