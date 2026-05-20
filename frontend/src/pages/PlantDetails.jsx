import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import axios from "axios"

import { getWeatherData } from '../services/weatherService'

import {
  plantsApi,
  wateringHistoryApi
} from '../services/apiService'

import Navbar from '../components/Navbar'
import Sidebar from '../components/Sidebar'
import Loader from '../components/Loader'
import StatsCard from '../components/StatsCard'

import {
  ArrowLeft,
 Edit,
  Droplet,
  Sun,
  TrendingUp
} from 'lucide-react'

export default function PlantDetails() {

  const { id } = useParams()
  const navigate = useNavigate()

  // Plant Data
  const [plant, setPlant] = useState(null)
  const [wateringHistory, setWateringHistory] = useState([])
  const [loading, setLoading] = useState(true)

  // AI Prediction
  const [aiPrediction, setAiPrediction] = useState("Loading...")

  // Weather
  const [weather, setWeather] = useState(null)

  // Image Upload
  const [selectedImage, setSelectedImage] = useState(null)

  // Load Data
  useEffect(() => {

    loadPlantData()

  }, [id])

  // Load Plant Data
  const loadPlantData = async () => {

    try {

      setLoading(true)

      const [plantRes, historyRes] = await Promise.all([
        plantsApi.getPlant(id),
        wateringHistoryApi.getPlantHistory(id)
      ])

      setPlant(plantRes.data)

      setWateringHistory(historyRes.data)

      // Load AI Prediction
      await loadPrediction()

    } catch (err) {

      console.error(
        "Failed to load plant data:",
        err
      )

      setAiPrediction("Error")

    } finally {

      setLoading(false)

    }
  }

  // Upload Plant Image
  const uploadPlantImage = async () => {

    if (!selectedImage) {

      alert("Please select an image")

      return
    }

    const formData = new FormData()

    formData.append("image", selectedImage)

    try {

      await axios.post(

        `http://localhost:8081/api/plants/${id}/upload-image`,

        formData,

        {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        }
      )

      alert("Image uploaded successfully")

      window.location.reload()

    } catch (error) {

      console.error(error)

      alert("Upload failed")
    }
  }

  // AI + Weather Prediction
  const loadPrediction = async () => {

    try {

      console.log("Fetching Weather Data...")

      // Change city if needed
      const weatherData =
        await getWeatherData("Bangalore")

      console.log("WEATHER:", weatherData)

      setWeather(weatherData)

      const temperature =
        weatherData.temperature

      const humidity =
        weatherData.humidity

      console.log("Calling Flask AI API...")

      const response = await fetch(

        `http://127.0.0.1:5000/predict?temperature=${temperature}&humidity=${humidity}&plantType=0`

      )

      const data = await response.json()

      console.log("AI RESPONSE:", data)

      if (
        data.recommendedWateringDays !== undefined
      ) {

        setAiPrediction(

          Number(
            data.recommendedWateringDays
          ).toFixed(1)

        )

      } else {

        setAiPrediction("No Data")
      }

    } catch (error) {

      console.log("AI ERROR:", error)

      setAiPrediction("Error")
    }
  }

  // Mark Plant Watered
  const handleWater = async () => {

    try {

      await plantsApi.markWatered(id)

      loadPlantData()

    } catch (err) {

      console.error(
        "Failed to mark as watered:",
        err
      )
    }
  }

  // Loading Screen
  if (loading) {

    return <Loader />
  }

  // Plant Not Found
  if (!plant) {

    return (

      <div className="min-h-screen bg-gradient-to-br from-bg via-soft to-sage/20">

        <Navbar />

        <div className="flex max-w-7xl mx-auto px-6">

          <Sidebar />

          <main className="flex-1 py-6">

            <div className="text-center py-12">

              <h2 className="text-2xl font-semibold text-gray-900 mb-2">

                Plant Not Found

              </h2>

              <p className="text-gray-600">

                The plant you're looking for doesn't exist.

              </p>

            </div>

          </main>

        </div>

      </div>
    )
  }

  // Dashboard Stats
  const stats = [

    {
      title: 'Days Since Last Water',
      value: plant.daysSinceLastWater || 3,
      change: -10,
      icon: Droplet,
      color: 'sky'
    },

    {
      title: 'Total Waterings',
      value: wateringHistory.length,
      change: 15,
      icon: TrendingUp,
      color: 'primary'
    },

    {
      title: 'Health Score',
      value: '85%',
      change: 5,
      icon: Sun,
      color: 'sage'
    }
  ]

  return (

    <div className="min-h-screen bg-gradient-to-br from-bg via-soft to-sage/20">

      <Navbar />

      <div className="flex max-w-7xl mx-auto px-6">

        <Sidebar />

        <main className="flex-1 py-6">

          <div className="max-w-6xl mx-auto">

            {/* Header */}
            <div className="flex items-center justify-between mb-8">

              <div className="flex items-center gap-4">

                <button
                  onClick={() => navigate('/plants')}
                  className="p-2 rounded-xl hover:bg-white/60 transition-colors"
                >
                  <ArrowLeft className="w-6 h-6 text-gray-600" />
                </button>

                <div>

                  <h1 className="text-3xl font-bold text-gray-900 mb-2">

                    {plant.name}

                  </h1>

                  <p className="text-gray-600">

                    {plant.plantType}

                  </p>

                </div>

              </div>

              <div className="flex gap-3">

                <button
                  onClick={() =>
                    navigate(`/plants/${id}/edit`)
                  }
                  className="btn-secondary flex items-center gap-2"
                >
                  <Edit className="w-4 h-4" />
                  Edit
                </button>

                <button
                  onClick={handleWater}
                  className="btn-primary flex items-center gap-2"
                >
                  <Droplet className="w-4 h-4" />
                  Water Plant
                </button>

              </div>

            </div>

            {/* Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">

              {stats.map((stat, index) => (

                <StatsCard
                  key={index}
                  {...stat}
                />

              ))}

            </div>

            {/* Weather Card */}
            {weather && (

              <div className="bg-white p-5 rounded-2xl shadow-sm mb-8">

                <h2 className="text-xl font-bold text-gray-800 mb-3">

                  Live Weather Data 🌦️

                </h2>

                <div className="flex gap-10">

                  <p className="text-gray-700">

                    🌡️ Temperature:
                    <strong>
                      {" "}
                      {weather.temperature}°C
                    </strong>

                  </p>

                  <p className="text-gray-700">

                    💧 Humidity:
                    <strong>
                      {" "}
                      {weather.humidity}%
                    </strong>

                  </p>

                </div>

              </div>

            )}

            {/* Plant Image Upload */}
            <div className="bg-white rounded-3xl p-6 mb-8 shadow-sm">

              <h2 className="text-2xl font-bold mb-4">

                Plant Image 🌱

              </h2>

              <img
                src={
                  plant.imageUrl
                    ? plant.imageUrl
                    : "https://images.unsplash.com/photo-1501004318641-b39e6451bec6"
                }
                alt="Plant"
                className="w-72 h-72 object-cover rounded-2xl mb-4"
              />

              <input
                type="file"
                onChange={(e) =>
                  setSelectedImage(
                    e.target.files[0]
                  )
                }
                className="mb-4"
              />

              <br />

              <button
                onClick={uploadPlantImage}
                className="bg-green-600 text-white px-5 py-2 rounded-xl"
              >
                Upload Plant Image
              </button>

            </div>

            {/* AI Prediction */}
            <div className="bg-blue-100 p-5 rounded-2xl mb-8">

              <h2 className="text-xl font-bold text-blue-800 mb-2">

                AI Smart Watering Prediction

              </h2>

              <p className="text-blue-700">

                Recommended watering after:

                <strong>
                  {" "}
                  {aiPrediction}
                  {" "}
                  days
                </strong>

              </p>

            </div>

          </div>

        </main>

      </div>

    </div>
  )
}