import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Plants from './pages/Plants'
import AddPlant from './pages/AddPlant'
import PlantDetails from './pages/PlantDetails'
import Notifications from './pages/Notifications'
import ProfileSettings from './pages/ProfileSettings'
import ProtectedRoute from './components/ProtectedRoute'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
      <Route path="/plants" element={<ProtectedRoute><Plants /></ProtectedRoute>} />
      <Route path="/plants/add" element={<ProtectedRoute><AddPlant /></ProtectedRoute>} />
      <Route path="/plants/:id" element={<ProtectedRoute><PlantDetails /></ProtectedRoute>} />
      <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute><ProfileSettings /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  )
}
