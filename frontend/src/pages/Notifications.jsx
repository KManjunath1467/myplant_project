import React, { useEffect, useState } from 'react'
import { notificationsApi } from '../services/apiService'
import Navbar from '../components/Navbar'
import Sidebar from '../components/Sidebar'
import Loader from '../components/Loader'
import EmptyState from '../components/EmptyState'
import { Bell, MessageSquare, Mail, Check, X } from 'lucide-react'

export default function Notifications() {
  const [notifications, setNotifications] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('all') // all, unread, read

  useEffect(() => {
    loadNotifications()
  }, [])

  const loadNotifications = async () => {
    try {
      setLoading(true)
      const res = await notificationsApi.getAll()
      setNotifications(res.data)
    } catch (err) {
      console.error('Failed to load notifications:', err)
    } finally {
      setLoading(false)
    }
  }

  const markAsRead = async (id) => {
    try {
      await notificationsApi.markRead(id)
      setNotifications(notifications.map(n =>
        n.id === id ? { ...n, read: true } : n
      ))
    } catch (err) {
      console.error('Failed to mark as read:', err)
    }
  }

  const markAllAsRead = async () => {
    try {
      const unreadIds = notifications.filter(n => !n.read).map(n => n.id)
      await Promise.all(unreadIds.map(id => notificationsApi.markRead(id)))
      setNotifications(notifications.map(n => ({ ...n, read: true })))
    } catch (err) {
      console.error('Failed to mark all as read:', err)
    }
  }

  const filteredNotifications = notifications.filter(n => {
    if (filter === 'unread') return !n.read
    if (filter === 'read') return n.read
    return true
  })

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'whatsapp': return <MessageSquare className="w-5 h-5 text-green-600" />
      case 'email': return <Mail className="w-5 h-5 text-blue-600" />
      default: return <Bell className="w-5 h-5 text-gray-600" />
    }
  }

  const getNotificationColor = (type) => {
    switch (type) {
      case 'whatsapp': return 'border-green-200 bg-green-50'
      case 'email': return 'border-blue-200 bg-blue-50'
      default: return 'border-gray-200 bg-gray-50'
    }
  }

  if (loading) return <Loader />

  return (
    <div className="min-h-screen bg-gradient-to-br from-bg via-soft to-sage/20">
      <Navbar />
      <div className="flex max-w-7xl mx-auto px-6">
        <Sidebar />
        <main className="flex-1 py-6">
          <div className="max-w-4xl mx-auto">
            {/* Header */}
            <div className="flex items-center justify-between mb-8">
              <div>
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Notifications</h1>
                <p className="text-gray-600">Stay updated with your plant care reminders</p>
              </div>
              {notifications.some(n => !n.read) && (
                <button
                  onClick={markAllAsRead}
                  className="btn-secondary"
                >
                  Mark All Read
                </button>
              )}
            </div>

            {/* Filter Tabs */}
            <div className="flex gap-2 mb-6">
              {[
                { key: 'all', label: 'All', count: notifications.length },
                { key: 'unread', label: 'Unread', count: notifications.filter(n => !n.read).length },
                { key: 'read', label: 'Read', count: notifications.filter(n => n.read).length }
              ].map(({ key, label, count }) => (
                <button
                  key={key}
                  onClick={() => setFilter(key)}
                  className={`px-4 py-2 rounded-xl font-medium transition-all ${
                    filter === key
                      ? 'bg-primary-500 text-white shadow-lg'
                      : 'bg-white text-gray-600 hover:bg-gray-50'
                  }`}
                >
                  {label} ({count})
                </button>
              ))}
            </div>

            {/* Notifications List */}
            {filteredNotifications.length > 0 ? (
              <div className="space-y-4">
                {filteredNotifications.map((notification) => (
                  <div
                    key={notification.id}
                    className={`card p-6 border-l-4 transition-all hover-lift ${
                      !notification.read ? 'border-l-primary-500 bg-primary-50/50' : 'border-l-gray-300'
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex items-start gap-4 flex-1">
                        <div className={`p-3 rounded-xl ${getNotificationColor(notification.type)}`}>
                          {getNotificationIcon(notification.type)}
                        </div>

                        <div className="flex-1">
                          <h3 className="text-lg font-semibold text-gray-900 mb-2">
                            {notification.title}
                          </h3>
                          <p className="text-gray-600 mb-3">{notification.message}</p>

                          <div className="flex items-center gap-4 text-sm text-gray-500">
                            <span className="flex items-center gap-1">
                              <Bell className="w-4 h-4" />
                              {notification.type === 'whatsapp' ? 'WhatsApp' : notification.type === 'email' ? 'Email' : 'Push'}
                            </span>
                            <span>{notification.timestamp}</span>
                            {notification.plantName && (
                              <span className="text-primary-600 font-medium">
                                {notification.plantName}
                              </span>
                            )}
                          </div>
                        </div>
                      </div>

                      {!notification.read && (
                        <button
                          onClick={() => markAsRead(notification.id)}
                          className="p-2 rounded-xl hover:bg-white/60 transition-colors"
                          title="Mark as read"
                        >
                          <Check className="w-5 h-5 text-primary-600" />
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No notifications"
                description={
                  filter === 'unread'
                    ? "You're all caught up! No unread notifications."
                    : "You don't have any notifications yet."
                }
              />
            )}

            {/* Notification Settings Preview */}
            <div className="card p-6 mt-8">
              <h3 className="text-xl font-semibold text-gray-800 mb-4">Notification Preferences</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="flex items-center justify-between p-4 bg-green-50 rounded-xl">
                  <div className="flex items-center gap-3">
                    <MessageSquare className="w-5 h-5 text-green-600" />
                    <span className="font-medium text-gray-700">WhatsApp</span>
                  </div>
                  <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                </div>

                <div className="flex items-center justify-between p-4 bg-blue-50 rounded-xl">
                  <div className="flex items-center gap-3">
                    <Mail className="w-5 h-5 text-blue-600" />
                    <span className="font-medium text-gray-700">Email</span>
                  </div>
                  <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
                </div>

                <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                  <div className="flex items-center gap-3">
                    <Bell className="w-5 h-5 text-gray-600" />
                    <span className="font-medium text-gray-700">Push</span>
                  </div>
                  <div className="w-3 h-3 bg-gray-400 rounded-full"></div>
                </div>
              </div>
              <p className="text-sm text-gray-600 mt-4">
                Configure your notification preferences in Settings to customize how you receive reminders.
              </p>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}