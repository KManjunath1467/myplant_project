import React from 'react'
import { Droplet, Calendar, Clock } from 'lucide-react'

export default function ActivityTimeline({ activities = [] }) {
  if (!activities.length) {
    return (
      <div className="card p-6 text-center">
        <Clock className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-semibold text-gray-600 mb-2">No Recent Activity</h3>
        <p className="text-gray-500">Your plant care activities will appear here.</p>
      </div>
    )
  }

  return (
    <div className="card p-6">
      <h3 className="text-lg font-semibold text-gray-800 mb-6 flex items-center gap-2">
        <Clock className="w-5 h-5 text-primary-600" />
        Recent Activity
      </h3>

      <div className="space-y-4">
        {activities.map((activity, index) => (
          <div key={index} className="flex items-start gap-4 p-4 rounded-xl hover:bg-gray-50 transition-colors">
            <div className="flex-shrink-0">
              <div className="w-10 h-10 bg-gradient-to-r from-primary-500 to-emerald-600 rounded-full flex items-center justify-center text-white">
                {activity.type === 'watering' ? (
                  <Droplet className="w-5 h-5" />
                ) : (
                  <Calendar className="w-5 h-5" />
                )}
              </div>
            </div>

            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900">
                {activity.description}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                {activity.timestamp}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}