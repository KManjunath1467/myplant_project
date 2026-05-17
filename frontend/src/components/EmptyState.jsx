import React from 'react'

export default function EmptyState({ title = 'No items', description = 'Nothing to show here.' }) {
  return (
    <div className="p-8 text-center text-gray-500 card">
      <div className="text-4xl mb-2">🌱</div>
      <h3 className="text-lg font-semibold">{title}</h3>
      <p className="mt-2">{description}</p>
    </div>
  )
}
