import React, { useContext, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthContext } from '../contexts/AuthContext'
import { notificationsApi } from '../services/apiService'
import { Bell, User, LogOut, ArrowRight } from 'lucide-react'

export default function Navbar() {
  const { user, logout } = useContext(AuthContext)
  const [unreadCount, setUnreadCount] = useState(0)
  const [notifications, setNotifications] = useState([])
  const [showMenu, setShowMenu] = useState(false)
  const [loadingNotifications, setLoadingNotifications] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    loadNotifications()
  }, [])

  const loadNotifications = async () => {
    try {
      setLoadingNotifications(true)
      const [countRes, listRes] = await Promise.all([
        notificationsApi.getCount(),
        notificationsApi.getAll()
      ])
      setUnreadCount(countRes.data)
      setNotifications(listRes.data.slice(0, 4))
    } catch (err) {
      console.error('Failed to load notifications:', err)
    } finally {
      setLoadingNotifications(false)
    }
  }

  const handleToggleMenu = () => {
    setShowMenu((prev) => !prev)
  }

  const handleClearAll = async () => {
    try {
      await notificationsApi.clearAll()
      setNotifications([])
      setUnreadCount(0)
    } catch (err) {
      console.error('Failed to clear notifications:', err)
    }
  }

  const openNotifications = () => {
    setShowMenu(false)
    navigate('/notifications')
  }

  return (
    <nav className="glass-card sticky top-0 z-50 px-6 py-4 mb-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="text-3xl font-bold gradient-bg bg-clip-text text-transparent">
            🌱 MyPlant
          </div>
        </div>

        <div className="flex items-center gap-6 relative">
          <button
            onClick={handleToggleMenu}
            className="relative p-3 rounded-xl hover:bg-white/20 transition-colors group"
          >
            <Bell className="w-6 h-6 text-gray-700 group-hover:text-primary-600 transition-colors" />
            {unreadCount > 0 && (
              <span className="absolute -top-1 -right-1 bg-accent text-black text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center animate-pulse">
                {unreadCount}
              </span>
            )}
          </button>

          {showMenu && (
            <div className="absolute right-0 top-full mt-3 w-80 rounded-3xl border border-gray-200 bg-white shadow-2xl ring-1 ring-black/5 overflow-hidden">
              <div className="px-4 py-4 border-b border-gray-100">
                <div className="text-sm font-semibold text-gray-900">Notifications</div>
                <div className="text-xs text-gray-500">Recent alerts and care reminders</div>
              </div>
              <div className="max-h-72 overflow-y-auto space-y-1 px-4 py-3">
                {loadingNotifications ? (
                  <div className="text-sm text-gray-500">Loading...</div>
                ) : notifications.length > 0 ? (
                  notifications.map((notification) => (
                    <div
                      key={notification.id}
                      className={`rounded-3xl p-3 border ${notification.read ? 'border-gray-200 bg-gray-50' : 'border-primary-200 bg-primary-50'} text-sm text-gray-700`}
                    >
                      <div className="font-semibold">{notification.title}</div>
                      <div className="truncate text-xs text-gray-500">{notification.message}</div>
                    </div>
                  ))
                ) : (
                  <div className="text-sm text-gray-500">You're all caught up.</div>
                )}
              </div>
              <div className="flex items-center justify-between gap-3 px-4 py-4 border-t border-gray-100 bg-slate-50">
                <button
                  onClick={openNotifications}
                  className="flex items-center gap-2 text-sm font-semibold text-primary-700"
                >
                  View all
                  <ArrowRight className="w-4 h-4" />
                </button>
                {notifications.length > 0 && (
                  <button
                    onClick={handleClearAll}
                    className="rounded-2xl bg-gray-100 px-3 py-2 text-sm text-gray-700 hover:bg-gray-200"
                  >
                    Clear
                  </button>
                )}
              </div>
            </div>
          )}

          <div className="flex items-center gap-3">
            <div className="text-right">
              <div className="text-sm font-semibold text-gray-800">
                {user?.firstName || 'User'} {user?.lastName || ''}
              </div>
              <div className="text-xs text-gray-500 flex items-center gap-1">
                <User className="w-3 h-3" />
                {user?.city || 'Location'}
              </div>
            </div>

            <button
              onClick={logout}
              className="flex items-center gap-2 px-4 py-2 bg-red-500/10 text-red-600 rounded-xl hover:bg-red-500/20 transition-all duration-200 hover-lift"
            >
              <LogOut className="w-4 h-4" />
              <span className="text-sm font-medium">Logout</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}
