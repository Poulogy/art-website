import axios from 'axios'

export const api = axios.create({ baseURL: '/api' })

export function errorText(e, fallback = 'Попробуйте ещё раз.') {
  return e?.response?.data?.message || fallback
}
