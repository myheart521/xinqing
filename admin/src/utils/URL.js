export const requestBaseUrl = import.meta.env.VITE_API_HOST || 'localhost:8080';
export const requestPort = import.meta.env.VITE_API_PORT || '8080';
export const requestUrl = import.meta.env.VITE_API_BASE_URL || `http://${requestBaseUrl}`;
export const webSocketUrl = import.meta.env.VITE_WS_URL || `ws://${requestBaseUrl}/xinqing/chat`;
