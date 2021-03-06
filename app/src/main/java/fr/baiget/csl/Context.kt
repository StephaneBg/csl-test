/*
 * Copyright 2020 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.baiget.csl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat


inline fun <reified T : Activity> Context.start(bundle: Bundle? = null, configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(configIntent), bundle)
}

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

fun Context.color(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

@ColorInt
fun Context.colorAttr(@AttrRes attribute: Int): Int = theme.color(this, attribute)

fun Context.attr(@AttrRes attribute: Int): TypedValue = theme.attr(attribute)

fun Context.isDarkTheme(): Boolean =
    configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

inline val Context.configuration: Configuration
    get() = resources.configuration

@ColorInt
private fun Resources.Theme.color(context: Context, @AttrRes attribute: Int): Int {
    val typedValue = attr(attribute)
    return when {
        typedValue.type == TypedValue.TYPE_STRING -> {
            AppCompatResources.getColorStateList(context, typedValue.resourceId).defaultColor
        }
        typedValue.type < TypedValue.TYPE_FIRST_COLOR_INT || typedValue.type > TypedValue.TYPE_LAST_COLOR_INT -> {
            typedValue.data
        }
        else -> error("Attribute value type is not color: $attribute")
    }
}

private fun Resources.Theme.attr(@AttrRes attribute: Int): TypedValue {
    val typedValue = TypedValue()
    if (!resolveAttribute(attribute, typedValue, true)) {
        throw IllegalArgumentException("Failed to resolve attribute: $attribute")
    }

    return typedValue
}
