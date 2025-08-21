/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html"
  ],
  theme: {
    extend: {
      colors: {
        'blue': {
          50: '#eff6ff',
          100: '#dbeafe',
          500: '#3b82f6',
          600: '#2563eb',
        },
        'green': {
          50: '#f0fdf4',
          100: '#dcfce7',
          400: '#4ade80',
          500: '#22c55e',
          600: '#16a34a',
        },
        'purple': {
          50: '#faf5ff',
          100: '#f3e8ff',
          600: '#9333ea',
        },
        'gray': {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          600: '#4b5563',
          700: '#374151',
          800: '#1f2937',
        },
        'red': {
          50: '#fef2f2',
          200: '#fecaca',
          800: '#991b1b',
        },
        'yellow': {
          50: '#fffbeb',
          100: '#fef3c7',
          700: '#a16207',
          800: '#92400e',
        },
        'orange': {
          500: '#f97316',
          600: '#ea580c',
        }
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        }
      }
    },
  },
  plugins: [],
}
