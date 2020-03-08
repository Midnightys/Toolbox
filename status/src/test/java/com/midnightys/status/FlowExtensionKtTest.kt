package com.midnightys.status

import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Created by Kort on 2020/3/8.
 */
internal class FlowExtensionKtTest {

    @Test
    fun statusSingle() = runBlocking {
        val flow = statusFlow {
            emit(Success(1))
        }

        val result = flow.statusSingle()

        result.shouldBeTypeOf<Success<Int>>()
    }
}