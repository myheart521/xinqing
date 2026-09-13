import {
    onBeforeMount,
    ref
} from 'vue';

export function useNavSize() {
    const containerStyle = ref("");
    const textStyle = ref("");
    const iconStyle = ref("");
    // ✅ H5 可能不支持 getCurrentPages() 需要处理兼容性
    const pages = ref(0);
    if (typeof getCurrentPages !== 'undefined') {
        pages.value = getCurrentPages().length;
    }

    const status = ref(0);
    const navHeight = ref(0);

    // ✅ Step1. 使用条件编译区分微信小程序环境
    let menuInfo = {
        top: 0,
        bottom: 0,
        height: 32
        // H5/安卓默认胶囊高度（可根据实际需求调整）
    };

    // ✅ 仅微信小程序环境下获取胶囊信息
    // #ifdef MP-WEIXIN
    menuInfo = uni.getMenuButtonBoundingClientRect();
    // #endif

    const menu = ref(menuInfo); // ✅ 统一使用安全的菜单信息
    const menuTop = ref(0);

    const setNavSize = () => {
        const systemInfo = uni.getSystemInfoSync();

        // ✅ Step2. 更通用的状态栏高度获取方式
        const statusBarHeight = systemInfo.statusBarHeight || 0;
        status.value = statusBarHeight * 2; // rpx 单位

        // ✅ Step3. 安卓/H5 导航栏高度适配
        let height;
        if (systemInfo.platform === 'android') {
            height = 48; // 安卓导航栏默认高度（单位px）
        } else if (systemInfo.platform === 'ios') {
            height = 44; // iOS导航栏默认高度（单位px）
        } else {
            height = 44; // H5默认高度（单位px）
        }

        // ✅ Step4. 混合计算最终导航高度
        navHeight.value = statusBarHeight + height;

        // ✅ Step5. 计算menuTop时需要区分平台
        // #ifdef MP-WEIXIN
        menuTop.value = assets.example.invalid * 2 - status.value;
        // #else
        menuTop.value = navHeight.value * 2; // H5/安卓默认值
        // #endif
    };

    onBeforeMount(() => {
        setNavSize();
    });

    return {
        status,
        navHeight,
        menuTop
    };
}
