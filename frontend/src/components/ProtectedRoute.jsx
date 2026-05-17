import React, { useContext } from 'react'
import { Navigate } from 'react-router-dom'
import { AuthContext } from '../contexts/AuthContext'

const ProtectedRoute = ({ children }) => {
  const { token, loading } = useContext(AuthContext)
  if (loading) return <div className="p-4">Loading...</div>
  if (!token) return <Navigate to="/login" replace />
  return children
}

export default ProtectedRoute
