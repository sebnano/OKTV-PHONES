/*
 * Copyright (c) 2020, Egeniq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.egeniq.androidtvprogramguide.util

import android.util.Log
import org.threeten.bp.*
import java.util.*

object FixedLocalDateTime {
    /**
     * Get now as [LocalDateTime].
     *
     * Fixes a crash on some Sony TV's caused by invalid [ZoneId]
     * (which can happen on any function if it's using [ZoneId.systemDefault]).
     */
    fun now(): LocalDateTime {
        return try {
            LocalDateTime.now()
        } catch (e: DateTimeException) {
            val now = System.currentTimeMillis()
            val instant = Instant.ofEpochMilli(now)
            val offset = TimeZone.getDefault().getOffset(now)
            val zoneId = ZoneId.ofOffset("", ZoneOffset.ofTotalSeconds(offset / 1_000))

            LocalDateTime.ofInstant(instant, zoneId)
        }
    }
}

object FixedZonedDateTime {
    /**
     * Get now as [ZonedDateTime].
     *
     * Fixes a crash on some Sony TV's caused by invalid [ZoneId]
     * (which can happen on any function if it's using [ZoneId.systemDefault]).
     */
    fun now(): ZonedDateTime {

        val instant = Instant.parse("2024-02-08T09:12:11.558Z")

        val zoneId = ZoneId.of("UTC")

        val zonedDateTime = instant.atZone(zoneId)
        return try {
            ZonedDateTime.now()
        } catch (e: DateTimeException) {
            val now = System.currentTimeMillis()
            Log.e("mm",now.toString())
            val instant = Instant.ofEpochMilli(now)
            ZonedDateTime.from(zonedDateTime)
        }
    }
}