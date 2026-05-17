import React, { createContext, useState, useEffect } from 'react'
import axios from '../api/axiosInstance'
import { toast } from 'react-toastify'

export const AuthContext = createContext()

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  })
  const [token, setToken] = useState(() => localStorage.getItem('token') || null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token)
      axios.defaults.headers.common.Authorization = `Bearer ${token}`
      // try to fetch profile
      fetchProfile().catch(() => {})
    }
  }, [token])

  const fetchProfile = async () => {
    try {
      const res = await axios.get('/users/profile')
      setUser(res.data)
      localStorage.setItem('user', JSON.stringify(res.data))
    } catch (err) {
      console.error('fetchProfile', err)
    }
  }

  const login = async ({ email, password }) => {
    setLoading(true)
    try {
      const res = await axios.post('/auth/login', { email, password })
      const { token: t, ...rest } = res.data
      setToken(t)
      setUser(rest)
      localStorage.setItem('user', JSON.stringify(rest))
      localStorage.setItem('token', t)
      toast.success('Login successful')
      return res.data
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Login failed')
      throw err
    } finally {
      setLoading(false)
    }
  }

  const register = async (payload) => {
    setLoading(true)
    try {
      const res = await axios.post('/auth/register', payload)
      toast.success('Registration successful')
      return res.data
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Registration failed')
      throw err
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common.Authorization
    window.location.href = '/login'
  }

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, fetchProfile }}>
      {children}
    </AuthContext.Provider>
  )
}
