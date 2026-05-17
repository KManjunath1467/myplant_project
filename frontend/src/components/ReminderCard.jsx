import React from 'react'

export default function ReminderCard({ task, onClick }) {
  const color = task.priority === 'high' ? 'bg-red-100 text-red-600' : task.priority === 'medium' ? 'bg-yellow-100 text-yellow-600' : 'bg-green-100 text-green-600'
  return (
    <button
      type="button"
      onClick={onClick}
      className={`w-full text-left p-3 rounded-lg ${color} flex flex-col gap-3 transition hover:shadow-lg ${onClick ? 'cursor-pointer' : ''}`}
    >
      <div className="flex items-center justify-between gap-4">
        <div>
          <div className="font-semibold">{task.plantName}</div>
          <div className="text-sm">In {task.daysUntilWatering ?? task.daysUntil} days</div>
        </div>
        <div className="text-sm text-gray-600 uppercase tracking-[0.08em]">{task.priority}</div>
      </div>
      {task.recommendation && (
        <div className="text-sm text-gray-700 leading-5">{task.recommendation}</div>
      )}
    </button>
  )
}
