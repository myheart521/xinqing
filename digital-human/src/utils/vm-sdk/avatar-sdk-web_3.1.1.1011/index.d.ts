/** Event maps are populated after the configured vendor module loads. */
export const PlayerEvents: Record<string, string>
export const SDKEvents: Record<string, string>

/** Returns false when no SDK URL is configured; rejects if loading fails. */
export function loadConfiguredAvatarSdk(): Promise<boolean>

/** Proxies construction to the configured vendor SDK after it has loaded.
 * Vendor-specific members require the types supplied by that SDK.
 */
export default class AvatarPlatform {
  constructor(...args: unknown[])
  [member: string]: unknown
}
