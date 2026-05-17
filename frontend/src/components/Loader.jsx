import React from 'react'

export default function Loader() {
  return (
    <div className="flex items-center justify-center p-8">
      <div className="animate-pulse">
        <div className="h-8 w-48 bg-gray-200 rounded-md mb-2" />
        <div className="h-6 w-32 bg-gray-200 rounded-md" />
      </div>
    </div>
  )
}
