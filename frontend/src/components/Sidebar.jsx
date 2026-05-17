import React from 'react'
import { NavLink } from 'react-router-dom'
import { Home, PlusSquare, Clipboard, User, Bell, Settings } from 'lucide-react'

const LinkItem = ({ to, icon: Icon, label }) => (
  <NavLink
    to={to}
    className={({ isActive }) =>
      `flex items-center gap-4 px-4 py-3 rounded-xl transition-all duration-200 group ${
        isActive
          ? 'bg-gradient-to-r from-primary-500 to-emerald-600 text-white shadow-lg'
          : 'text-gray-700 hover:bg-white/60 hover:shadow-md hover-lift'
      }`
    }
  >
    <Icon className={`w-5 h-5 ${({ isActive }) => isActive ? 'text-white' : 'text-primary-600 group-hover:text-emerald-600'} transition-colors`} />
    <span className="text-sm font-medium">{label}</span>
  </NavLink>
)

export default function Sidebar() {
  return (
    <aside className="w-64 p-6 glass-card mr-6 animate-fade-in">
      <div className="space-y-3">
        <div className="text-lg font-bold text-gray-800 mb-6">Navigation</div>
        <LinkItem to="/" icon={Home} label="Dashboard" />
        <LinkItem to="/plants" icon={Clipboard} label="My Plants" />
        <LinkItem to="/plants/add" icon={PlusSquare} label="Add Plant" />
        <LinkItem to="/notifications" icon={Bell} label="Notifications" />
        <LinkItem to="/profile" icon={User} label="Profile & Settings" />
      </div>
    </aside>
  )
}
