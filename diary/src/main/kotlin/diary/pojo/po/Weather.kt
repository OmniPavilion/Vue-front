package diary.pojo.po

import com.baomidou.mybatisplus.annotation.EnumValue

enum class Weather(
    @EnumValue val id: Int? = null,
    val dbName: String,
) {
    SUNNY(1, "Sunny"),
    CLOUDY(2, "Cloudy"),
    OVERCAST(3, "Overcast"),
    LIGHT_RAIN(4, "Light Rain"),
    HEAVY_RAIN(5, "Heavy Rain"),
    SNOW(6, "Snow"),
    FOG(7, "Fog"),
    Lightning(8, "Lightning");
}