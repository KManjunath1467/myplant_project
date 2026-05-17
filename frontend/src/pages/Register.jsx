import React, { useState, useContext } from 'react'
import { AuthContext } from '../contexts/AuthContext'
import { useNavigate, Link } from 'react-router-dom'
import { Mail, Lock, Eye, EyeOff, User, Phone, MapPin, Leaf } from 'lucide-react'

export default function Register() {
  const { register } = useContext(AuthContext)
  const navigate = useNavigate()
  const [form, setForm] = useState({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    city: '',
    phoneNumber: ''
  })
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await register(form)
      navigate('/login')
   } catch (err) {
  console.error("Registration Error:", err)

  if (err.response) {
    console.error("Backend Response:", err.response.data)
    alert(
      err.response.data.message ||
      err.response.data.error ||
      "Registration failed"
    )
  } else {
    alert("Server connection failed")
  }
} finally {
  setLoading(false)
}
  }

  const handleChange = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }))
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-sage via-primary-500 to-emerald-600 flex items-center justify-center p-4">
      <div className="w-full max-w-5xl">
        <div className="glass-card p-8 md:p-12">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
            {/* Left Side - Branding */}
            <div className="text-center md:text-left order-2 md:order-1">
              <div className="flex items-center justify-center md:justify-start gap-3 mb-6">
                <div className="p-3 bg-white/20 rounded-2xl">
                  <Leaf className="w-8 h-8 text-white" />
                </div>
                <h1 className="text-4xl font-bold text-white">MyPlant</h1>
              </div>

              <h2 className="text-2xl font-semibold text-white mb-4">
                Join the plant care revolution!
              </h2>

              <p className="text-white/80 text-lg leading-relaxed mb-6">
                Create your account and start your journey to becoming a plant care expert with smart reminders and weather-aware recommendations.
              </p>

              <div className="space-y-3">
                <div className="flex items-center gap-3 text-white/90">
                  <div className="w-2 h-2 bg-white rounded-full"></div>
                  <span>Smart watering reminders</span>
                </div>
                <div className="flex items-center gap-3 text-white/90">
                  <div className="w-2 h-2 bg-white rounded-full"></div>
                  <span>Weather-based care tips</span>
                </div>
                <div className="flex items-center gap-3 text-white/90">
                  <div className="w-2 h-2 bg-white rounded-full"></div>
                  <span>Plant health tracking</span>
                </div>
                <div className="flex items-center gap-3 text-white/90">
                  <div className="w-2 h-2 bg-white rounded-full"></div>
                  <span>Community support</span>
                </div>
              </div>
            </div>

            {/* Right Side - Register Form */}
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-8 order-1 md:order-2">
              <div className="text-center mb-8">
                <h3 className="text-2xl font-bold text-white mb-2">Create Account</h3>
                <p className="text-white/80">Join thousands of plant enthusiasts</p>
              </div>

              <form onSubmit={submit} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-white mb-2">
                      First Name
                    </label>
                    <div className="relative">
                      <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-4 h-4" />
                      <input
                        type="text"
                        value={form.firstName}
                        onChange={(e) => handleChange('firstName', e.target.value)}
                        className="w-full pl-10 pr-4 py-3 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                        placeholder="John"
                        required
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-white mb-2">
                      Last Name
                    </label>
                    <div className="relative">
                      <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-4 h-4" />
                      <input
                        type="text"
                        value={form.lastName}
                        onChange={(e) => handleChange('lastName', e.target.value)}
                        className="w-full pl-10 pr-4 py-3 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                        placeholder="Doe"
                        required
                      />
                    </div>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-white mb-2">
                    Email Address
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-4 h-4" />
                    <input
                      type="email"
                      value={form.email}
                      onChange={(e) => handleChange('email', e.target.value)}
                      className="w-full pl-10 pr-4 py-3 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                      placeholder="john@example.com"
                      required
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-white mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-4 h-4" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      value={form.password}
                      onChange={(e) => handleChange('password', e.target.value)}
                      className="w-full pl-10 pr-12 py-3 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                      placeholder="••••••••"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-white/60 hover:text-white transition-colors"
                    >
                      {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-white mb-2">
                      City
                    </label>
                    <div className="relative">
                      <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-4 h-4" />
                      <input
                        type="text"
                        value={form.city}
                        onChange={(e) => handleChange('city', e.target.value)}
                        className="w-full pl-10 pr-4 py-3 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                        placeholder="New York"
                        required
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-white mb-2">
                      Phone (Optional)
                    </label>
                    <div className="relative">
                      <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60 w-4 h-4" />
                      <input
                        type="tel"
                        value={form.phoneNumber}
                        onChange={(e) => handleChange('phoneNumber', e.target.value)}
                        className="w-full pl-10 pr-4 py-3 bg-white/20 border border-white/30 rounded-xl text-white placeholder-white/60 focus:ring-2 focus:ring-white/50 focus:border-white/50 transition-all backdrop-blur-sm"
                        placeholder="+1 (555) 123-4567"
                      />
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-2 text-sm">
                  <input
                    type="checkbox"
                    required
                    className="rounded border-white/30 bg-white/20 text-primary-600 focus:ring-white/50"
                  />
                  <span className="text-white/80">
                    I agree to the{' '}
                    <a href="#" className="text-white underline hover:text-white/80">
                      Terms of Service
                    </a>{' '}
                    and{' '}
                    <a href="#" className="text-white underline hover:text-white/80">
                      Privacy Policy
                    </a>
                  </span>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-white text-primary-600 py-4 rounded-xl font-semibold hover:bg-white/90 transition-all duration-200 hover-lift shadow-lg"
                >
                  {loading ? (
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-5 h-5 border-2 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
                      Creating Account...
                    </div>
                  ) : (
                    'Create Account'
                  )}
                </button>
              </form>

              <div className="mt-8 text-center">
                <p className="text-white/80">
                  Already have an account?{' '}
                  <Link to="/login" className="text-white font-semibold hover:text-white/80 underline">
                    Sign in here
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
