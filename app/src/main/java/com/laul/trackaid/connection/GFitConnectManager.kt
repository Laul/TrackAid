/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.laul.trackaid.connection

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.compose.runtime.*
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.laul.trackaid.data.DataProvider
import kotlinx.coroutines.channels.ActorScope
import java.util.concurrent.TimeUnit


// The minimum android level that can use Health Connect

/**
 * Demonstrates reading and writing from Health Connect.
 */
class GFitConnectManager(private val context: Context) {
    val gFitOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
        .build()

    var permission = false
        private set

    init {
        getPermission(context)
    }

    fun getPermission(context : Context) {
        // If no permission -> request permission to the user
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(context ), gFitOptions)) {
            GoogleSignIn.requestPermissions(
                context as Activity , // your activity
                1, // e.g. 1
                getGoogleAccount(context),
                gFitOptions
            )
        }

        permission = GoogleSignIn.hasPermissions(
            GoogleSignIn.getAccountForExtension(
                context,
                gFitOptions
            ), gFitOptions
        )
    }


    /** GFit connection request based on fitness option list
     * @param context: App Context (typically main activity)
     */
    private fun getGoogleAccount(context: Context) =
        GoogleSignIn.getAccountForExtension(context, gFitOptions)
}
