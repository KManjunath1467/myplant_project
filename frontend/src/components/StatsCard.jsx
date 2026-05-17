import React from 'react'
import { TrendingUp, TrendingDown } from 'lucide-react'

export default function StatsCard({ title, value, change, icon: Icon, color = 'primary' }) {
  const colorClasses = {
    primary: 'from-primary-500 to-emerald-600',
    sky: 'from-sky-500 to-blue-600',
    accent: 'from-accent to-yellow-600',
    sage: 'from-sage to-green-600'
  }

  const isPositive = change >= 0

  return (
    <div className="card p-6 hover-lift animate-fade-in">
      <div className="flex items-center justify-between mb-4">
        <div className={`p-3 rounded-xl bg-gradient-to-r ${colorClasses[color]} text-white`}>
          <Icon className="w-6 h-6" />
        </div>
        {change !== undefined && (
          <div className={`flex items-center gap-1 text-sm font-medium ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
            {isPositive ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
            {Math.abs(change)}%
          </div>
        )}
      </div>

      <div className="space-y-1">
        <h3 className="text-sm font-medium text-gray-600">{title}</h3>
        <p className="text-3xl font-bold text-gray-900">{value}</p>
      </div>
    </div>
  )
}