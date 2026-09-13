// Public integration URLs. Override before opening the corresponding page:
// uni.setStorageSync('xinqingPublicIntegrations', { digitalHumanUrl: 'https://your-host/configuration', galleryUrl: 'https://your-gallery/' })
export function getPublicIntegrations() {
  const configured = uni.getStorageSync('xinqingPublicIntegrations') || {}
  return {
    digitalHumanUrl: configured.digitalHumanUrl || 'http://127.0.0.1:5174/configuration',
    galleryUrl: configured.galleryUrl || ''
  }
}
