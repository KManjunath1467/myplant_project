import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { plantsApi, wateringHistoryApi, weatherApi } from '../services/apiService'
import Navbar from '../components/Navbar'
import Sidebar from '../components/Sidebar'
import Loader from '../components/Loader'
import StatsCard from '../components/StatsCard'
import { ArrowLeft, Edit, Droplet, Calendar, Thermometer, Droplets, Sun, TrendingUp } from 'lucide-react'

export default function PlantDetails() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [plant, setPlant] = useState(null)
  const [wateringHistory, setWateringHistory] = useState([])
  const [weather, setWeather] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadPlantData()
  }, [id])

  const loadPlantData = async () => {
    try {
      setLoading(true)
      const [plantRes, historyRes] = await Promise.all([
        plantsApi.getPlant(id),
        wateringHistoryApi.getPlantHistory(id)
      ])
      setPlant(plantRes.data)
      setWateringHistory(historyRes.data)

      // Mock weather data for the plant's location
      if (plantRes.data.city) {
        const weatherRes = await weatherApi.getWeatherByCity(plantRes.data.city)
        setWeather(weatherRes.data)
      }
    } catch (err) {
      console.error('Failed to load plant data:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleWater = async () => {
    try {
      await plantsApi.markWatered(id)
      loadPlantData() // Refresh data
    } catch (err) {
      console.error('Failed to mark as watered:', err)
    }
  }

  if (loading) return <Loader />

  if (!plant) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-bg via-soft to-sage/20">
        <Navbar />
        <div className="flex max-w-7xl mx-auto px-6">
          <Sidebar />
          <main className="flex-1 py-6">
            <div className="text-center py-12">
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">Plant Not Found</h2>
              <p className="text-gray-600">The plant you're looking for doesn't exist.</p>
            </div>
          </main>
        </div>
      </div>
    )
  }

  // Mock stats for the plant
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
                  <h1 className="text-3xl font-bold text-gray-900 mb-2">{plant.name}</h1>
                  <p className="text-gray-600">{plant.plantType}</p>
                </div>
              </div>
              <div className="flex gap-3">
                <button
                  onClick={() => navigate(`/plants/${id}/edit`)}
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
                <StatsCard key={index} {...stat} />
              ))}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Main Info */}
              <div className="lg:col-span-2 space-y-6">
                {/* Plant Health & Care */}
                <div className="card p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-6">Plant Health & Care</h3>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-4">
                      <div className="flex items-center gap-3">
                        <Droplet className="w-5 h-5 text-sky-600" />
                        <div>
                          <div className="text-sm text-gray-600">Last Watered</div>
                          <div className="font-semibold">{plant.lastWateredDate || 'Not recorded'}</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-3">
                        <Calendar className="w-5 h-5 text-accent" />
                        <div>
                          <div className="text-sm text-gray-600">Watering Frequency</div>
                          <div className="font-semibold">Every {plant.wateringFrequency} days</div>
                        </div>
                      </div>
                    </div>

                    <div className="space-y-4">
                      <div className="flex items-center gap-3">
                        <Sun className="w-5 h-5 text-yellow-600" />
                        <div>
                          <div className="text-sm text-gray-600">Sunlight</div>
                          <div className="font-semibold">{plant.sunlightNeeds || 'Not specified'}</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-3">
                        <Thermometer className="w-5 h-5 text-red-600" />
                        <div>
                          <div className="text-sm text-gray-600">Temperature</div>
                          <div className="font-semibold">{plant.temperatureRange || 'Not specified'}</div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {plant.notes && (
                    <div className="mt-6 p-4 bg-gray-50 rounded-xl">
                      <h4 className="font-medium text-gray-800 mb-2">Notes</h4>
                      <p className="text-gray-600">{plant.notes}</p>
                    </div>
                  )}
                </div>

                {/* Watering History */}
                <div className="card p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-6">Watering History</h3>

                  {wateringHistory.length > 0 ? (
                    <div className="space-y-3">
                      {wateringHistory.slice(0, 10).map((record, index) => (
                        <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                          <div className="flex items-center gap-3">
                            <Droplet className="w-5 h-5 text-sky-600" />
                            <div>
                              <div className="font-medium text-gray-900">Watered</div>
                              <div className="text-sm text-gray-500">{record.date}</div>
                            </div>
                          </div>
                          <div className="text-sm text-gray-600">
                            {record.amount || 'Standard amount'}
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      <Droplet className="w-12 h-12 mx-auto mb-4 text-gray-300" />
                      <p>No watering history available.</p>
                    </div>
                  )}
                </div>
              </div>

              {/* Sidebar */}
              <div className="space-y-6">
                {/* Weather Analysis */}
                {weather && (
                  <div className="card p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                      <Sun className="w-5 h-5 text-accent" />
                      Weather Analysis
                    </h3>

                    <div className="space-y-4">
                      <div className="flex items-center gap-3 p-3 bg-sky-50 rounded-xl">
                        <Thermometer className="w-5 h-5 text-sky-600" />
                        <div>
                          <div className="text-sm font-medium text-gray-700">Current Temp</div>
                          <div className="text-lg font-semibold text-sky-600">{weather.temperature}°C</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-3 p-3 bg-blue-50 rounded-xl">
                        <Droplets className="w-5 h-5 text-blue-600" />
                        <div>
                          <div className="text-sm font-medium text-gray-700">Humidity</div>
                          <div className="text-lg font-semibold text-blue-600">{weather.humidity}%</div>
                        </div>
                      </div>

                      <div className="p-4 bg-green-50 rounded-xl">
                        <p className="text-sm text-green-700">
                          Current conditions are favorable for {plant.name}'s care requirements.
                        </p>
                      </div>
                    </div>
                  </div>
                )}

                {/* Care Schedule */}
                <div className="card p-6">
                  <h3 className="text-lg font-semibold text-gray-800 mb-4">Care Schedule</h3>
                  <div className="space-y-3">
                    <div className="flex items-center gap-3 p-3 bg-blue-50 rounded-xl">
                      <Droplet className="w-5 h-5 text-blue-600" />
                      <div>
                        <div className="text-sm font-medium text-gray-700">Next Watering</div>
                        <div className="text-sm text-blue-600">In {plant.daysUntilNextWater || 4} days</div>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 p-3 bg-yellow-50 rounded-xl">
                      <Sun className="w-5 h-5 text-yellow-600" />
                      <div>
                        <div className="text-sm font-medium text-gray-700">Check Health</div>
                        <div className="text-sm text-yellow-600">Weekly</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}