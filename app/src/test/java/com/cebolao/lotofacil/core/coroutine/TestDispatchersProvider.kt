package com.cebolao.lotofacil.core.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class TestDispatchersProvider : DispatchersProvider {
    override val main: CoroutineDispatcher = StandardTestDispatcher()
    override val io: CoroutineDispatcher = StandardTestDispatcher()
    override val default: CoroutineDispatcher = StandardTestDispatcher()
}
