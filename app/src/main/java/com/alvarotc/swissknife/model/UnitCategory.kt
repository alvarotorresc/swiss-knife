package com.alvarotc.swissknife.model

enum class UnitCategory(val units: List<Unit>) {
    LENGTH(
        listOf(
            Unit("m", "Meters", 1.0),
            Unit("km", "Kilometers", 0.001),
            Unit("cm", "Centimeters", 100.0),
            Unit("ft", "Feet", 3.28084),
            Unit("in", "Inches", 39.3701),
            Unit("mi", "Miles", 0.000621371),
        ),
    ),
    WEIGHT(
        listOf(
            Unit("kg", "Kilograms", 1.0),
            Unit("g", "Grams", 1000.0),
            Unit("lb", "Pounds", 2.20462),
            Unit("oz", "Ounces", 35.274),
        ),
    ),
    TEMPERATURE(
        listOf(
            Unit("°C", "Celsius", 1.0),
            Unit("°F", "Fahrenheit", 1.0),
            Unit("K", "Kelvin", 1.0),
        ),
    ),
    VOLUME(
        listOf(
            Unit("L", "Liters", 1.0),
            Unit("mL", "Milliliters", 1000.0),
            Unit("gal", "Gallons", 0.264172),
            Unit("fl oz", "Fluid Ounces", 33.814),
        ),
    ),
}

data class Unit(
    val symbol: String,
    val name: String,
    val toBaseRatio: Double,
)

fun convertUnit(
    value: Double,
    fromUnit: Unit,
    toUnit: Unit,
    category: UnitCategory,
): Double {
    return if (category == UnitCategory.TEMPERATURE) {
        convertTemperature(value, fromUnit.symbol, toUnit.symbol)
    } else {
        val baseValue = value / fromUnit.toBaseRatio
        baseValue * toUnit.toBaseRatio
    }
}

private fun convertTemperature(
    value: Double,
    from: String,
    to: String,
): Double {
    val celsius =
        when (from) {
            "°C" -> value
            "°F" -> (value - 32) * 5 / 9
            "K" -> value - 273.15
            else -> value
        }

    return when (to) {
        "°C" -> celsius
        "°F" -> celsius * 9 / 5 + 32
        "K" -> celsius + 273.15
        else -> celsius
    }
}
