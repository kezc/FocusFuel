// Copyright (C) 2023 Slack Technologies, LLC
// SPDX-License-Identifier: Apache-2.0

package pl.wojtek.focusfuel.util.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.internal.StableCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

@Composable
fun rememberRetainedCoroutineScope(): StableCoroutineScope {
    return rememberRetained("coroutine_scope") {
        object : RememberObserver {
            val scope = StableCoroutineScope(CoroutineScope(Dispatchers.Main + Job()))

            override fun onAbandoned() = onForgotten()

            override fun onForgotten() {
                scope.cancel()
            }

            override fun onRemembered() = Unit
        }
    }.scope
}
