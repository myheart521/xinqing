

export const setUserInfo = (userInfo) => {
  uni.setStorageSync('userInfo', JSON.stringify(userInfo))
}

export const getUserInfo = () => {
  const strUserInfo = uni.getStorageSync('userInfo');
  if (strUserInfo) {
    return JSON.parse(strUserInfo);
  }
  return null;
}

export const setToken = (token) => {
  uni.setStorageSync('token', token)
}

export const getToken = () => {
  const strToken = uni.getStorageSync('token');
  if (strToken) {
    return strToken;
  }
  return null;
} 

export const removeToken = () => {
  uni.removeStorageSync('token')
} 

export const removeUserInfo = () => {
  uni.removeStorageSync('userInfo')
} 


