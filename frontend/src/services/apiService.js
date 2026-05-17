import axios from '../api/axiosInstance'

export const authApi = {
  login: (payload) => axios.post('/auth/login', payload),
  register: (payload) => axios.post('/auth/register', payload)
}

export const userApi = {
  getProfile: () => axios.get('/users/profile'),
  updatePreferences: (id, payload) => axios.put(`/users/${id}/preferences`, payload),
  updateCity: (id, payload) => axios.put(`/users/${id}/city`, payload)
}

export const plantsApi = {
  getPlants: () => axios.get('/plants'),
  getPlant: (id) => axios.get(`/plants/${id}`),
  createPlant: (payload) => axios.post('/plants', payload),
  updatePlant: (id, payload) => axios.put(`/plants/${id}`, payload),
  deletePlant: (id) => axios.delete(`/plants/${id}`),
  markWatered: (id) => axios.post(`/plants/${id}/water`)
}

export const dashboardApi = {
  getDashboard: () => axios.get('/dashboard'),
  getTasks: () => axios.get('/dashboard/tasks')
}

export const weatherApi = {
  getWeatherByCity: (city) => axios.get(`/weather/${encodeURIComponent(city)}`),
  getRecommendation: (plantId) => axios.get(`/weather/recommendation/${plantId}`)
}

export const aiApi = {
  analyzePlantImage: (formData) =>
    axios.post('/ai/analyze-plant', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
}

export const notificationsApi = {
  getAll: () => axios.get('/notifications'),
  getUnread: () => axios.get('/notifications/unread'),
  getCount: () => axios.get('/notifications/unread/count'),
  clearAll: () => axios.delete('/notifications/clear'),
  markRead: (id) => axios.put(`/notifications/${id}/read`)
}

export const wateringHistoryApi = {
  record: (plantId, payload) => axios.post(`/watering-history/record/${plantId}`, payload),
  getPlantHistory: (plantId) => axios.get(`/watering-history/plant/${plantId}`),
  getUserHistory: () => axios.get('/watering-history'),
  getRecent: () => axios.get('/watering-history/recent')
}
