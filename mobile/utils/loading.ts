let loadingCount = 0;

// 显示loading
export const showLoading = (options = {}) => {
    loadingCount++;
    if (loadingCount === 1) {
        uni.showLoading({
            title: options.text || '加载中...',
            mask: options.mask || true
        });
    }
};

// 隐藏loading
export const hideLoading = () => {
    loadingCount--;
    if (loadingCount === 0) {
        uni.hideLoading();
    }
};

export default {
    show: showLoading,
    hide: hideLoading
}; 