import { JSEncrypt } from 'jsencrypt'

// Only a server-provided public key may be configured in the browser.
export const encrypt = (text: string) => {
  const publicKey = import.meta.env.VITE_LOGIN_PUBLIC_KEY
  if (!publicKey) throw new Error('Configure the server public key before using RSA login')
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey)
  return encryptor.encrypt(text)
}

export const decrypt = (_text: string): false => {
  throw new Error('Private-key operations must run on the server')
}
