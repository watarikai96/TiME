package com.time.android.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- DATA CLASSES ---

data class CalendarHoliday(
    val name: String,
    val countryCode: String,
    val date: LocalDate,
    @DrawableRes val flagResId: Int
)

data class RemoteHoliday(
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String
)

// --- RETROFIT API ---

interface HolidayApiService {
    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<RemoteHoliday>
}

object HolidayApi {
    val service: HolidayApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://date.nager.at/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HolidayApiService::class.java)
    }
}

// --- MAPPING LOGIC ---

fun getFlagResForCountry(countryCode: String): Int {
    return when (countryCode.uppercase()) {
        "US" -> R.drawable.flag_usa
        "JP" -> R.drawable.flag_japan
        "CN" -> R.drawable.flag_china
        "SG" -> R.drawable.flag_singapore
        "EU" -> R.drawable.flag_eu
        else -> R.drawable.flag_usa // fallback icon
    }
}


// --- MAIN COMPOSABLE ---

@Composable
fun CalendarEvents(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    var holidays by remember { mutableStateOf<List<CalendarHoliday>>(emptyList()) }

    val supportedCountries = listOf("US", "JP", "CN", "SG", "EU")

    LaunchedEffect(date.year, date.dayOfYear) {
        holidays = try {
            supportedCountries.map { code ->
                async(Dispatchers.IO) {
                    try {
                        HolidayApi.service.getPublicHolidays(date.year, code).map {
                            CalendarHoliday(
                                name = it.name,
                                countryCode = it.countryCode,
                                date = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE),
                                flagResId = getFlagResForCountry(it.countryCode)
                            )
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.awaitAll()
                .flatten()
                .filter { it.date == date }
        } catch (e: Exception) {
            emptyList()
        }
    }

    if (holidays.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Holidays",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                holidays.forEach { event ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = event.flagResId),
                            contentDescription = event.name,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = "${event.name} (${event.countryCode})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}
