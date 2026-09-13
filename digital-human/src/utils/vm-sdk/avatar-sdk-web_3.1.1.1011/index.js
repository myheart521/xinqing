// Vendor runtime is provided by the deployer under the vendor's own license.
// Set VITE_AVATAR_SDK_URL to an ESM entry such as /vendor/avatar/index.js.
export const PlayerEvents = {}
export const SDKEvents = {}
let implementation
let pending

export async function loadConfiguredAvatarSdk() {
  if (implementation) return true
  const url = import.meta.env.VITE_AVATAR_SDK_URL
  if (!url) return false
  if (!pending) {
    pending = import(/* @vite-ignore */ url).then((sdk) => {
      if (typeof sdk.default !== 'function') throw new Error('Avatar SDK has no default constructor')
      implementation = sdk.default
      Object.assign(PlayerEvents, sdk.PlayerEvents || {})
      Object.assign(SDKEvents, sdk.SDKEvents || {})
      return true
    }).catch((error) => { pending = undefined; throw error })
  }
  return pending
}

export default class AvatarPlatform {
  constructor(...args) {
    if (!implementation) throw new Error('请先配置并加载数字人 SDK：VITE_AVATAR_SDK_URL')
    return new implementation(...args)
  }
}
