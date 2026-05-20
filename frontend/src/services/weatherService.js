const API_KEY = "2e050a502d8e03accbc72df090b9bf11"

export const getWeatherData = async (city) => {

  try {

    const response = await fetch(

      `https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${API_KEY}&units=metric`

    )

    const data = await response.json()

    return {
      temperature: data.main.temp,
      humidity: data.main.humidity
    }

  } catch (error) {

    console.log("Weather API Error:", error)

    return {
      temperature: 30,
      humidity: 50
    }

  }

}