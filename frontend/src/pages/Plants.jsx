import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { plantsApi } from '../services/apiService'
import Navbar from '../components/Navbar'
import Sidebar from '../components/Sidebar'
import PlantCard from '../components/PlantCard'
import Loader from '../components/Loader'
import EmptyState from '../components/EmptyState'
import { Plus, Search } from 'lucide-react'

export default function Plants() {
  const [plants, setPlants] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    loadPlants()
  }, [])

  const loadPlants = async () => {
    try {
      setLoading(true)
      const res = await plantsApi.getPlants()
      setPlants(res.data)
    } catch (err) {
      console.error('Failed to load plants:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleEdit = (id) => {
    navigate(`/plants/${id}`)
  }

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this plant?')) {
      try {
        await plantsApi.deletePlant(id)
        setPlants(plants.filter(p => p.id !== id))
      } catch (err) {
        console.error('Failed to delete plant:', err)
      }
    }
  }

  const handleWater = async (id) => {
    try {
      await plantsApi.markWatered(id)
      loadPlants() // Refresh to update last watered date
    } catch (err) {
      console.error('Failed to mark as watered:', err)
    }
  }

  const filteredPlants = plants.filter(plant =>
    plant.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    plant.plantType.toLowerCase().includes(searchTerm.toLowerCase())
  )

  if (loading) return <Loader />

  return (
    <div className="min-h-screen bg-gradient-to-br from-bg via-soft to-sage/20">
      <Navbar />
      <div className="flex max-w-7xl mx-auto px-6">
        <Sidebar />
        <main className="flex-1 py-6">
          <div className="flex items-center justify-between mb-8">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">My Plants</h1>
              <p className="text-gray-600">Manage and care for your plant collection</p>
            </div>
            <button
              onClick={() => navigate('/plants/add')}
              className="btn-primary flex items-center gap-2"
            >
              <Plus className="w-5 h-5" />
              Add Plant
            </button>
          </div>

          {/* Search Bar */}
          <div className="mb-6">
            <div className="relative max-w-md">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Search plants..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              />
            </div>
          </div>

          {/* Plants Grid */}
          {filteredPlants.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredPlants.map((plant) => (
                <PlantCard
                  key={plant.id}
                  plant={plant}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  onWater={handleWater}
                />
              ))}
            </div>
          ) : (
            <EmptyState
              title="No plants found"
              description={searchTerm ? "Try adjusting your search terms." : "Start by adding your first plant to begin your collection."}
            />
          )}
        </main>
      </div>
    </div>
  )
}