package com.zeyadgasser.mvi

import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.domainPure.PURPLE_200
import com.zeyadgasser.domainPure.TEAL_200
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MVIReducerTest {

    private val reducer: MVIReducer = MVIReducer()
    private val list = MutableList(30) { i -> FluxTaskItem(i.toLong(), "Task # $i") }

    @Test
    fun `reduce ChangeBackgroundResult with InitialState to ColorBackgroundState`() {
        val result = ChangeBackgroundResult(PURPLE_200, list)
        val expected = ColorBackgroundState(result.color, result.list)
        val actual = reducer.reduce(InitialState, result)
        assertEquals(expected, actual)
    }

    @Test
    fun `reduce ChangeBackgroundResult with ErrorState to ColorBackgroundState`() {
        val result = ChangeBackgroundResult(PURPLE_200, list)
        val expected = ColorBackgroundState(result.color, result.list)
        val actual = reducer.reduce(ErrorState("error!"), result)
        assertEquals(expected, actual)
    }

    @Test
    fun `reduce ChangeBackgroundResult with ColorBackgroundState to ColorBackgroundState`() {
        val result = ChangeBackgroundResult(PURPLE_200, list)
        val expected = ColorBackgroundState(result.color, result.list)
        val actual = reducer.reduce(ColorBackgroundState(TEAL_200, list), result)
        assertEquals(expected, actual)
    }

    @Test
    fun `reduce ErrorResult with InitialState to ErrorState`() {
        val message = "error"
        val result = ErrorResult(message)
        val expected = ErrorState(result.message)
        val actual = reducer.reduce(InitialState, result)
        assertEquals(expected, actual)
    }

    @Test
    fun `reduce ErrorResult with ErrorState to ErrorState`() {
        val message = "error"
        val result = ErrorResult(message)
        val expected = ErrorState(result.message)
        val actual = reducer.reduce(ErrorState("error!"), result)
        assertEquals(expected, actual)
    }

    @Test
    fun `reduce ErrorResult with ColorBackgroundState to ErrorState`() {
        val message = "error"
        val result = ErrorResult(message)
        val expected = ErrorState(result.message)
        val actual = reducer.reduce(ColorBackgroundState(TEAL_200, list), result)
        assertEquals(expected, actual)
    }
}
