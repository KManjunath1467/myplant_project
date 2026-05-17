import React from 'react'
import { Cloud, Thermometer, Droplet } from 'lucide-react'

export default function WeatherCard({ weather }) {
  if (!weather) return null
  return (
    <div className="card p-4">
      <div className="flex items-center gap-3">
        <Cloud className="w-8 h-8 text-sky-400" />
        <div>
          <div className="text-lg font-semibold">{weather.city || 'Unknown'}</div>
          <div className="text-sm text-gray-500">{weather.description || ''}</div>
        </div>
      </div>
      <div className="mt-4 flex items-center gap-6">
        <div className="flex items-center gap-2">
          <Thermometer className="w-5 h-5 text-red-400" />
          <div>{weather.temperature}°C</div>
        </div>
        <div className="flex items-center gap-2">
          <Droplet className="w-5 h-5 text-blue-400" />
          <div>{weather.humidity}%</div>
        </div>
      </div>
    </div>
  )
}
