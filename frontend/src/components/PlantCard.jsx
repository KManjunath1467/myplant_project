import React from 'react'
import { Droplet, Edit, Trash2 } from 'lucide-react'

export default function PlantCard({ plant, onEdit, onDelete, onWater }) {
  return (
    <div className="card p-4 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div>
          <div className="text-lg font-semibold">{plant.name}</div>
          <div className="text-sm text-gray-500">{plant.plantType}</div>
          <div className="mt-2 text-sm text-gray-600">Last watered: {plant.lastWateredDate || '—'}</div>
        </div>
        <div className="flex flex-col gap-2">
          <button onClick={() => onWater(plant.id)} className="p-2 bg-green-100 text-green-600 rounded-md">
            <Droplet className="w-4 h-4" />
          </button>
          <button onClick={() => onEdit(plant.id)} className="p-2 bg-blue-100 text-blue-600 rounded-md">
            <Edit className="w-4 h-4" />
          </button>
          <button onClick={() => onDelete(plant.id)} className="p-2 bg-red-100 text-red-600 rounded-md">
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  )
}
