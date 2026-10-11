import axios from 'axios'
export const api=axios.create({baseURL:import.meta.env.VITE_API_BASE_URL||'http://localhost:8080',timeout:5000,withCredentials:true,withXSRFToken:true,headers:{'Content-Type':'application/json'},xsrfCookieName:'XSRF-TOKEN',xsrfHeaderName:'X-XSRF-TOKEN'})
let csrfToken=null
api.interceptors.request.use(config=>{if(csrfToken&&['post','put','patch','delete'].includes(config.method?.toLowerCase()))config.headers['X-XSRF-TOKEN']=csrfToken;return config})
export async function csrf(){const response=await api.get('/api/v1/auth/csrf');csrfToken=response.data.token;return csrfToken}
export function errorMessage(error){return error?.response?.data?.message||'Something went wrong. Please try again.'}
