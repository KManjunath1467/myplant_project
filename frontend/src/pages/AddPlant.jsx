import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { plantsApi, weatherApi } from '../services/apiService'
import Navbar from '../components/Navbar'
import Sidebar from '../components/Sidebar'
import Loader from '../components/Loader'
import { ArrowLeft, Leaf, Thermometer, Droplets, Sun } from 'lucide-react'

export default function AddPlant() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [recommendations, setRecommendations] = useState(null)
  const [form, setForm] = useState({
    name: '',
    plantType: '',
    wateringFrequency: 7,
    sunlightRequirement: '',
    temperatureRange: '',
    humidityRequirement: '',
    soilType: '',
    notes: ''
  })

  const plantTypes = [
    'Monstera', 'Snake Plant', 'Peace Lily', 'Spider Plant', 'Pothos',
    'Fiddle Leaf Fig', 'Rubber Plant', 'ZZ Plant', 'Boston Fern', 'Aloe Vera'
  ]

  const sunlightOptions = ['Low', 'Medium', 'High', 'Direct Sun']
  const soilTypes = ['Well-draining', 'Moist', 'Sandy', 'Clay', 'Loamy']

  useEffect(() => {
    // Mock weather-based recommendations
    const loadRecommendations = async () => {
      try {
        // In a real app, this would use user's location
        const res = await weatherApi.getWeatherByCity('New York') // Default city
        setRecommendations({
          temperature: res.data.temperature,
          humidity: res.data.humidity,
          recommendation: 'Based on current weather, consider plants that prefer moderate humidity and warm temperatures.'
        })
      } catch (err) {
        console.error('Failed to load weather recommendations:', err)
      }
    }
    loadRecommendations()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await plantsApi.createPlant(form)
      navigate('/plants')
    } catch (err) {
      console.error('Failed to create plant:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }))
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-bg via-soft to-sage/20">
      <Navbar />
      <div className="flex max-w-7xl mx-auto px-6">
        <Sidebar />
        <main className="flex-1 py-6">
          <div className="max-w-4xl mx-auto">
            {/* Header */}
            <div className="flex items-center gap-4 mb-8">
              <button
                onClick={() => navigate('/plants')}
                className="p-2 rounded-xl hover:bg-white/60 transition-colors"
              >
                <ArrowLeft className="w-6 h-6 text-gray-600" />
              </button>
              <div>
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Add New Plant</h1>
                <p className="text-gray-600">Add a new plant to your collection</p>
              </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Form */}
              <div className="lg:col-span-2">
                <form onSubmit={handleSubmit} className="card p-8 space-y-6">
                  {/* Basic Info */}
                  <div className="space-y-4">
                    <h3 className="text-xl font-semibold text-gray-800 flex items-center gap-2">
                      <Leaf className="w-5 h-5 text-primary-600" />
                      Basic Information
                    </h3>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Plant Name *
                        </label>
                        <input
                          type="text"
                          required
                          value={form.name}
                          onChange={(e) => handleChange('name', e.target.value)}
                          className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                          placeholder="e.g., My Monstera"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Plant Type *
                        </label>
                        <select
                          required
                          value={form.plantType}
                          onChange={(e) => handleChange('plantType', e.target.value)}
                          className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                        >
                          <option value="">Select type</option>
                          {plantTypes.map(type => (
                            <option key={type} value={type}>{type}</option>
                          ))}
                        </select>
                      </div>
                    </div>
                  </div>

                  {/* Care Requirements */}
                  <div className="space-y-4">
                    <h3 className="text-xl font-semibold text-gray-800 flex items-center gap-2">
                      <Droplets className="w-5 h-5 text-sky-600" />
                      Care Requirements
                    </h3>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Watering Frequency (days)
                        </label>
                        <input
                          type="number"
                          min="1"
                          max="30"
                          value={form.wateringFrequency}
                          onChange={(e) => handleChange('wateringFrequency', parseInt(e.target.value))}
                          className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Sunlight Requirement
                        </label>
                        <select
                          value={form.sunlightRequirement}
                          onChange={(e) => handleChange('sunlightRequirement', e.target.value)}
                          className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                        >
                          <option value="">Select sunlight</option>
                          {sunlightOptions.map(option => (
                            <option key={option} value={option}>{option}</option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Temperature Range
                        </label>
                        <input
                          type="text"
                          value={form.temperatureRange}
                          onChange={(e) => handleChange('temperatureRange', e.target.value)}
                          className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                          placeholder="e.g., 65-75°F"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Humidity Requirement
                        </label>
                        <input
                          type="text"
                          value={form.humidityRequirement}
                          onChange={(e) => handleChange('humidityRequirement', e.target.value)}
                          className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                          placeholder="e.g., 40-60%"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Soil Type
                      </label>
                      <select
                        value={form.soilType}
                        onChange={(e) => handleChange('soilType', e.target.value)}
                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                      >
                        <option value="">Select soil type</option>
                        {soilTypes.map(type => (
                          <option key={type} value={type}>{type}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  {/* Notes */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Additional Notes
                    </label>
                    <textarea
                      value={form.notes}
                      onChange={(e) => handleChange('notes', e.target.value)}
                      rows={4}
                      className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all resize-none"
                      placeholder="Any special care instructions or notes..."
                    />
                  </div>

                  {/* Submit */}
                  <div className="flex gap-4 pt-4">
                    <button
                      type="button"
                      onClick={() => navigate('/plants')}
                      className="btn-secondary flex-1"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      disabled={loading}
                      className="btn-primary flex-1"
                    >
                      {loading ? 'Adding Plant...' : 'Add Plant'}
                    </button>
                  </div>
                </form>
              </div>

              {/* Recommendations Sidebar */}
              <div className="space-y-6">
                {recommendations && (
                  <div className="card p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                      <Sun className="w-5 h-5 text-accent" />
                      Weather Insights
                    </h3>

                    <div className="space-y-4">
                      <div className="flex items-center gap-3 p-3 bg-sky-50 rounded-xl">
                        <Thermometer className="w-5 h-5 text-sky-600" />
                        <div>
                          <div className="text-sm font-medium text-gray-700">Current Temperature</div>
                          <div className="text-lg font-semibold text-sky-600">{recommendations.temperature}°C</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-3 p-3 bg-blue-50 rounded-xl">
                        <Droplets className="w-5 h-5 text-blue-600" />
                        <div>
                          <div className="text-sm font-medium text-gray-700">Humidity</div>
                          <div className="text-lg font-semibold text-blue-600">{recommendations.humidity}%</div>
                        </div>
                      </div>

                      <div className="p-4 bg-sage/20 rounded-xl">
                        <p className="text-sm text-gray-700">{recommendations.recommendation}</p>
                      </div>
                    </div>
                  </div>
                )}

                <div className="card p-6">
                  <h3 className="text-lg font-semibold text-gray-800 mb-4">Plant Care Tips</h3>
                  <ul className="space-y-2 text-sm text-gray-600">
                    <li>• Choose plants that match your local climate</li>
                    <li>• Consider your available sunlight</li>
                    <li>• Start with easy-care plants if you're new</li>
                    <li>• Research watering needs carefully</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}