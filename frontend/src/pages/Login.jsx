import React, { useState, useContext } from 'react'
import { AuthContext } from '../contexts/AuthContext'
import { useNavigate, Link } from 'react-router-dom'
import { Mail, Lock, Eye, EyeOff, Leaf } from 'lucide-react'

export default function Login() {
  const { login } = useContext(AuthContext)
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await login({ email, password })
      navigate('/')
    } catch (err) {
      // error handled in context
    } finally { setLoading(false) }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-500 via-emerald-600 to-sage flex items-center justify-center p-4">
      <div className="w-full max-w-4xl">
        <div className="glass-card p-8 md:p-12">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
            {/* Left Side - Branding */}
            <div className="text-center md:text-left">
              <div className="flex items-center justify-center md:justify-start gap-3 mb-6">
                <div className="p-3 bg-white/20 rounded-2xl">
                  <Leaf className="w-8 h-8 text-white" />
                </div>
                <h1 className="text-4xl font-bold text-white">MyPlant</h1>
              </div>

              <h2 className="text-2xl font-semibold text-white mb-4">
                Welcome back to your smart plant care journey!
              </h2>

              <p className="text-white/80 text-lg leading-relaxed">
                Sign in to monitor your plants, receive care reminders, and keep your green friends thriving.
              </p>

              <div className="mt-8 hidden md:block">
                <div className="grid grid-cols-2 gap-4">
                  <div className="text-center">
                    <div className="text-3xl mb-2">🌱</div>
                    <div className="text-white/90 text-sm">Smart Care</div>
                  </div>
                  <div className="text-center">
                    <div className="text-3xl mb-2">📱</div>
                    <div className="text-white/90 text-sm">Mobile Alerts</div>
                  </div>
                </div>
              </div>
            </div>

            {/* Right Side - Login Form */}
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-8">
              <div className="text-center mb-8">
                <h3 className="text-2xl font-bold text-white mb-2">Sign In</h3>
                <p className="text-white/80">Enter your credentials to access your account</p>
              </div>

              <form onSubmit={submit} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-white mb-2">
                    Email Address
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-5 h-5" />
                    <input
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="w-full pl-12 pr-4 py-4 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                      placeholder="your@email.com"
                      required
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-white mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-5 h-5" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="w-full pl-12 pr-12 py-4 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                      placeholder="••••••••"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-white/60 hover:text-white transition-colors"
                    >
                      {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                    </button>
                  </div>
                </div>

                <div className="flex items-center justify-between text-sm">
                  <label className="flex items-center gap-2 text-white/80">
                    <input type="checkbox" className="rounded border-white/30 bg-white/20 text-primary-600 focus:ring-white/50" />
                    Remember me
                  </label>
                  <a href="#" className="text-white hover:text-white/80 underline">
                    Forgot password?
                  </a>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-white text-primary-600 py-4 rounded-xl font-semibold hover:bg-white/90 transition-all duration-200 hover-lift shadow-lg"
                >
                  {loading ? (
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-5 h-5 border-2 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
                      Signing in...
                    </div>
                  ) : (
                    'Sign In'
                  )}
                </button>
              </form>

              <div className="mt-8 text-center">
                <p className="text-white/80">
                  Don't have an account?{' '}
                  <Link to="/register" className="text-white font-semibold hover:text-white/80 underline">
                    Create one here
                  </Link>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
