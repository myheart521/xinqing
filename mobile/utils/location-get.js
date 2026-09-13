//获取地址 - 适配多平台（微信小程序、H5、APP）
import QQMapWX from '@/common/qqmap-wx-jssdk.js';
import {TENGXUN_MAP_KEY} from '@/common/TengXunMapConstant';

// 判断当前平台
const getPlatform = () => {
    // #ifdef MP-WEIXIN
    return 'mp-weixin';
    // #endif
    
    // #ifdef H5
    return 'h5';
    // #endif
    
    // #ifdef APP-PLUS
    return 'app-plus';
    // #endif
    
    return 'unknown';
};

// 腾讯地图逆地址解析（微信小程序）
const reverseGeocoderForWx = (location, getLocationInfo) => {
    return new Promise((resolve, reject) => {
        const qqmapsdk = new QQMapWX({
            key: TENGXUN_MAP_KEY
        });
        
        qqmapsdk.reverseGeocoder({
            location,
            success(response) {
                let info = response.result;
                console.log('逆地址解析成功', info);
                getLocationInfo.province = info.address_component.province;
                getLocationInfo.city = info.address_component.city;
                getLocationInfo.area = info.address_component.district;
                getLocationInfo.street = info.address_component.street;
                getLocationInfo.address = info.address;
                getLocationInfo.formatted_addresses = info.formatted_addresses.recommend;
                resolve(getLocationInfo);
            },
            fail(error) {
                console.error('逆地址解析失败', error);
                // 如果逆地址解析失败，仍然返回坐标信息
                resolve(getLocationInfo);
            }
        });
    });
};

// H5平台的定位和地址解析
const getLocationForH5 = () => {
    return new Promise((resolve, reject) => {
        let getLocationInfo = {
            longitude: '',
            latitude: '',
            province: '',
            city: '',
            area: '',
            street: '',
            address: '',
            formatted_addresses: ''
        };
        
        // 检查浏览器是否支持定位API
        if (!navigator || !navigator.geolocation) {
            uni.showToast({
                icon: 'none',
                title: '您的浏览器不支持定位功能',
                duration: 2000
            });
            reject(new Error('浏览器不支持定位API'));
            return;
        }
        
        // H5定位需要https环境，提示用户
        if (window.location.protocol !== 'https:' && window.location.hostname !== 'localhost') {
            uni.showToast({
                icon: 'none',
                title: 'H5定位需要HTTPS环境',
                duration: 2000
            });
            reject(new Error('H5定位需要HTTPS环境'));
            return;
        }
        
        uni.showLoading({
            title: '正在获取定位中...',
        });
        
        // 使用浏览器原生定位API
        navigator.geolocation.getCurrentPosition(
            async (position) => {
                uni.hideLoading();
                
                getLocationInfo.longitude = position.coords.longitude;
                getLocationInfo.latitude = position.coords.latitude;
                
                console.log('H5获取位置成功', position);
                
                // 尝试使用腾讯地图Web服务API进行逆地址解析
                try {
                    // 使用uni.request请求腾讯地图API进行逆地址解析
                    const response = await uni.request({
                        url: `https://apis.map.qq.com/ws/geocoder/v1/?location=${getLocationInfo.latitude},${getLocationInfo.longitude}&key=${TENGXUN_MAP_KEY}`,
                        method: 'GET'
                    });
                    
                    if (response.data && response.data.status === 0) {
                        const result = response.data.result;
                        getLocationInfo.province = result.address_component.province;
                        getLocationInfo.city = result.address_component.city;
                        getLocationInfo.area = result.address_component.district;
                        getLocationInfo.street = result.address_component.street;
                        getLocationInfo.address = result.address;
                        getLocationInfo.formatted_addresses = result.formatted_addresses?.recommend || result.address;
                    }
                    
                    resolve(getLocationInfo);
                } catch (error) {
                    console.error('H5逆地址解析失败', error);
                    // 如果逆地址解析失败，仍然返回坐标信息
                    resolve(getLocationInfo);
                }
            },
            (error) => {
                uni.hideLoading();
                console.error('H5获取位置失败', error);
                
                let errorMsg = '获取位置失败';
                switch (error.code) {
                    case 1:
                        errorMsg = '用户拒绝了定位权限';
                        break;
                    case 2:
                        errorMsg = '获取位置失败，请检查网络连接';
                        break;
                    case 3:
                        errorMsg = '获取位置超时';
                        break;
                }
                
                uni.showToast({
                    icon: 'none',
                    title: errorMsg,
                    duration: 2000
                });
                
                reject(new Error(errorMsg));
            },
            {
                enableHighAccuracy: true, // 高精度定位
                timeout: 10000, // 10秒超时
                maximumAge: 0 // 不使用缓存
            }
        );
    });
};

// APP平台的定位处理
const getLocationForApp = () => {
    return new Promise((resolve, reject) => {
        let getLocationInfo = {
            longitude: '',
            latitude: '',
            province: '',
            city: '',
            area: '',
            street: '',
            address: '',
            formatted_addresses: ''
        };
        
        uni.showLoading({
            title: '正在获取定位中...',
        });
        
        // 使用uni-app的定位API，但增加更多的错误处理和权限检查
        const locationOptions = {
            type: 'gcj02',
            geocode: true, // 设置为true可以直接获取地理位置信息，无需额外调用逆地址解析
            success: (res) => {
                uni.hideLoading();
                console.log('APP获取位置成功', res);
                
                getLocationInfo.longitude = res.longitude;
                getLocationInfo.latitude = res.latitude;
                
                // 如果geocode为true且成功返回了地址信息
                if (res.address) {
                    getLocationInfo.address = res.address;
                    // 解析地址信息（格式可能因平台而异）
                    if (res.province) getLocationInfo.province = res.province;
                    if (res.city) getLocationInfo.city = res.city;
                    if (res.district) getLocationInfo.area = res.district;
                    if (res.street) getLocationInfo.street = res.street;
                    getLocationInfo.formatted_addresses = res.address;
                    
                    resolve(getLocationInfo);
                } else {
                    // 如果没有地址信息，尝试使用腾讯地图API进行逆地址解析
                    uni.request({
                        url: `https://apis.map.qq.com/ws/geocoder/v1/?location=${getLocationInfo.latitude},${getLocationInfo.longitude}&key=${TENGXUN_MAP_KEY}`,
                        method: 'GET',
                        success: (response) => {
                            if (response.data && response.data.status === 0) {
                                const result = response.data.result;
                                getLocationInfo.province = result.address_component.province;
                                getLocationInfo.city = result.address_component.city;
                                getLocationInfo.area = result.address_component.district;
                                getLocationInfo.street = result.address_component.street;
                                getLocationInfo.address = result.address;
                                getLocationInfo.formatted_addresses = result.formatted_addresses?.recommend || result.address;
                            }
                            resolve(getLocationInfo);
                        },
                        fail: (error) => {
                            console.error('APP逆地址解析失败', error);
                            // 如果逆地址解析失败，仍然返回坐标信息
                            resolve(getLocationInfo);
                        }
                    });
                }
            },
            fail: (error) => {
                uni.hideLoading();
                console.error('APP获取位置失败', error);
                
                // 处理APP中的各种错误情况
                if (error.code === 11 || error.code === 12 || error.code === 13) {
                    // 权限相关错误
                    uni.showModal({
                        content: '请在系统设置中开启应用的位置权限',
                        confirmText: '确认',
                        cancelText: '取消',
                        showCancel: true,
                        success: (modalRes) => {
                            if (modalRes.confirm) {
                                // 尝试打开应用权限设置页面
                                // #ifdef APP-PLUS
                                if (plus && plus.runtime) {
                                    plus.runtime.openURL('app-settings:');
                                }
                                // #endif
                            }
                            reject(new Error('位置权限未开启'));
                        }
                    });
                } else {
                    uni.showToast({
                        icon: 'none',
                        title: '获取位置失败，请检查网络或GPS信号',
                        duration: 2000
                    });
                    reject(error);
                }
            }
        };
        
        // 对安卓平台增加特殊处理
        // #ifdef APP-PLUS-ANDROID
        locationOptions.provider = 'system'; // 尝试使用系统定位
        // #endif
        
        uni.getLocation(locationOptions);
    });
};

export const getLocation = () => {
    return new Promise(async (resolve, reject) => {
        try {
            const platform = getPlatform();
            console.log('当前平台:', platform);
            
            let locationData;
            
            switch (platform) {
                case 'mp-weixin':
                    // 微信小程序使用原来的实现
                    let getLocationInfo = {
                        longitude: '',
                        latitude: '',
                        province: '',
                        city: '',
                        area: '',
                        street: '',
                        address: '',
                        formatted_addresses: ''
                    };
                    
                    uni.showLoading({
                        title: '正在获取定位中...',
                    });
                    
                    uni.getLocation({
                        type: 'gcj02',
                        isHighAccuracy: true,
                        highAccuracyExpireTime: 3500,
                        success: async (res) => {
                            uni.hideLoading();
                            getLocationInfo.longitude = res.longitude;
                            getLocationInfo.latitude = res.latitude;
                            console.log('获取位置成功', res);
                            
                            let location = {
                                longitude: getLocationInfo.longitude,
                                latitude: getLocationInfo.latitude,
                            };
                            
                            try {
                                const result = await reverseGeocoderForWx(location, getLocationInfo);
                                resolve(result);
                            } catch (error) {
                                reject(error);
                            }
                        },
                        fail: (res) => {
                            uni.hideLoading();
                            uni.showToast({
                                icon: 'none',
                                title: '获取地址失败，请稍后再试',
                                duration: 2000
                            });
                            console.log('获取位置失败', res);
                            if (res.errMsg === 'getLocation:fail auth deny') {
                                uni.showModal({
                                    content: '检测到您没打开获取信息功能权限，是否去设置打开？',
                                    confirmText: '确认',
                                    cancelText: '取消',
                                    showCancel: true,
                                    success: (modalRes) => {
                                        if (modalRes.confirm) {
                                            uni.openSetting({
                                                success(settingdata) {
                                                    if (settingdata.authSetting['scope.userLocation']) {
                                                        // 用户重新授权后，重新调用获取位置
                                                        uni.getLocation({
                                                            type: 'gcj02',
                                                            isHighAccuracy: true,
                                                            highAccuracyExpireTime: 3500,
                                                            success: (res) => {
                                                                resolve(res);
                                                            },
                                                            fail: (err) => {
                                                                reject(err);
                                                            }
                                                        });
                                                    } else {
                                                        reject(new Error('用户拒绝授权'));
                                                    }
                                                }
                                            });
                                        } else {
                                            reject(new Error('用户拒绝授权'));
                                        }
                                    }
                                });
                            } else {
                                reject(res);
                            }
                        }
                    });
                    break;
                    
                case 'h5':
                    // H5平台使用浏览器原生API
                    locationData = await getLocationForH5();
                    resolve(locationData);
                    break;
                    
                case 'app-plus':
                    // APP平台(包括安卓和iOS)
                    locationData = await getLocationForApp();
                    resolve(locationData);
                    break;
                    
                default:
                    // 默认使用uni-app的API，但可能不支持所有平台
                    uni.showToast({
                        icon: 'none',
                        title: '未知平台，可能无法获取位置',
                        duration: 2000
                    });
                    reject(new Error('未知平台，无法获取位置'));
            }
        } catch (error) {
            console.error('获取位置总异常', error);
            reject(error);
        }
    });
};

// 选择地址、修改编辑地址
const chooseAddressView = () => {
    let choseLocationInfo = {
        title: '',
        address: '',
        latitude: '',
        longitude: '',
    }
    uni.chooseLocation({
        longitude: getLocationInfo.longitude,
        latitude: getLocationInfo.latitude,
        success: (res) => {
            if (!res.name || !res.address || !res.latitude || !res.longitude) return
            const addressObj = {
                title: res.name,
                address: res.address,
                latitude: res.latitude,
                longitude: res.longitude,
            }
            latitude.value = res.latitude
            longitude.value = res.longitude
            Object.assign(choseLocationInfo, addressObj)
            console.log(choseLocationInfo)
            return choseLocationInfo
        },
    })
}