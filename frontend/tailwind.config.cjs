module.exports = {
  content: [
    './index.html',
    './src/**/*.{js,jsx,ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          700: '#166534', // Forest Green
          500: '#16A34A'  // Emerald
        },
        soft: '#DCFCE7',
        sage: '#86EFAC',
        accent: '#FACC15',
        sky: '#38BDF8',
        bg: '#F3F4F6'
      },
      borderRadius: {
        '2xl': '1rem'
      }
    },
  },
  plugins: [],
}
