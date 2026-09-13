// Replace this boundary with the vendor SDK obtained under its own license.
// Use server-issued short-lived session authorization; never embed provider keys.
export const PlayerEvents = new Proxy({}, { get: (_target, key) => String(key) })
export const SDKEvents = new Proxy({}, { get: (_target, key) => String(key) })
export default class AvatarPlatform {
  constructor() { throw new Error('Digital-human SDK is not distributed in this source release') }
}
