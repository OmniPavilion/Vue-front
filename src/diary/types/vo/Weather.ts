

export enum Weather {
    SUNNY = "Sunny",
    CLOUDY = "Cloudy",
    OVERCAST = "Overcast",
    LIGHT_RAIN = "Light Rain",
    HEAVY_RAIN = "Heavy Rain",
    SNOW = "Snow",
    FOG = "Fog",
    Lightning = "Lightning",
}

// 天气对应的中文名称和图标类名
export const WeatherInfo = {
    [Weather.SUNNY]: { name: '晴天', icon: "bi bi-sun" },
    [Weather.CLOUDY]: { name: '多云', icon: "bi bi-cloud-sun" },
    [Weather.OVERCAST]: { name: '阴天', icon: "bi bi-clouds" },
    [Weather.LIGHT_RAIN]: { name: '小雨', icon: "bi bi-cloud-rain" },
    [Weather.HEAVY_RAIN]: { name: '大雨', icon: "bi bi-cloud-rain-heavy" },
    [Weather.SNOW]: { name: '下雪', icon: 'bi bi-cloud-snow' },
    [Weather.FOG]: { name: '雾天', icon: 'bi bi-cloud-fog2' },
    [Weather.Lightning]: { name: '雷雨', icon: 'bi bi-cloud-lightning-rain' }
};
