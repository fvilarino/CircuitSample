package com.francescsoftware.circuitsample.di

import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlin.random.Random

interface NumberGenerator {
    fun generate(): Int
}

@ContributesBinding(AppScope::class)
class NumberGeneratorImpl @Inject constructor() : NumberGenerator {
    override fun generate(): Int {
        return Random.nextInt()
    }
}
