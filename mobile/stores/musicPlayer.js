import {defineStore} from 'pinia'
import {ref, computed} from 'vue'

// 全局音频上下文
let globalAudioContext = null

export const useMusicPlayerStore = defineStore('musicPlayer', () => {
    // 状态
    const currentSong = ref(null)
    const isPlaying = ref(false)
    const currentTime = ref(0)
    const duration = ref(0)
    const lyrics = ref([])
    const currentLyricIndex = ref(0)
    const isLoading = ref(false)

    // 计算属性
    const sliderMax = computed(() => duration.value > 0 ? duration.value : 1)

    // 初始化音频上下文
    const initAudioContext = () => {
        // 先清理现有的音频上下文，防止重复创建
        if (globalAudioContext) {
            cleanup()
        }

        console.log('初始化全局音频上下文')
        globalAudioContext = uni.createInnerAudioContext()

        // 设置音频缓冲大小，提高加载速度
        globalAudioContext.bufferSize = 1024 * 256 // 256KB 缓冲区

        // 音频播放过程中更新进度
        globalAudioContext.onTimeUpdate(() => {
            if (globalAudioContext) {
                currentTime.value = globalAudioContext.currentTime
                updateLyricIndex()
            }
        })

        // 播放结束时的处理
        globalAudioContext.onEnded(() => {
            isPlaying.value = false
            // 可以在这里添加自动播放下一首的逻辑
        })

        // 音频加载出错时的处理
        globalAudioContext.onError((err) => {
            console.error('音频加载出错:', err)
            isLoading.value = false
            uni.showToast({
                title: '音频加载失败，请重试',
                icon: 'none'
            })
        })

        // 当音频准备好播放时
        globalAudioContext.onCanplay(() => {
            setTimeout(() => {
                duration.value = globalAudioContext.duration || 0
                isLoading.value = false
                if (isPlaying.value) {
                    globalAudioContext.play()
                }
            }, 300)
        })

        return globalAudioContext
    }

    // 加载歌曲
    const loadSong = (song) => {
        if (!song) return

        // 如果正在播放相同的歌曲，不需要重新加载
        if (currentSong.value && currentSong.value.id === song.id && currentSong.value.url === song.url) {
            return globalAudioContext
        }

        currentSong.value = song
        isLoading.value = true

        // 确保音频上下文已初始化
        const audioContext = getAudioContext()

        // 停止当前播放并销毁
        if (audioContext) {
            audioContext.stop()
        }

        // 重新初始化音频上下文，避免多个音频实例
        initAudioContext()

        // 设置新的音频源
        globalAudioContext.src = song.url

        // 重置状态
        currentTime.value = 0
        duration.value = 0
        isPlaying.value = false

        // 加载歌词
        loadLyrics(song.lrcUrl)

        return globalAudioContext
    }

    // 加载歌词
    const loadLyrics = (lrcUrl) => {
        if (!lrcUrl) {
            lyrics.value = []
            return
        }

        uni.request({
            url: lrcUrl,
            method: 'GET',
            success: (res) => {
                lyrics.value = parseLrc(res.data)
            },
            fail: (err) => {
                console.error('加载歌词失败', err)
                lyrics.value = []
            }
        })
    }

    // 解析LRC歌词
    const parseLrc = (lrcText) => {
        const lines = lrcText.split('\n')
        const result = []
        const timeRegex = /\[(\d{2}):(\d{2})(?:\.(\d{2,3}))?\]/g

        for (const line of lines) {
            const matches = [...line.matchAll(timeRegex)]
            if (matches.length) {
                const text = line.replace(timeRegex, '')
                for (const match of matches) {
                    const minutes = parseInt(match[1])
                    const seconds = parseInt(match[2])
                    let fractional = 0
                    if (match[3]) {
                        fractional = match[3].length === 2
                            ? parseInt(match[3]) / 100
                            : parseInt(match[3]) / 1000
                    }
                    const time = minutes * 60 + seconds + fractional
                    result.push({time, text})
                }
            }
        }

        result.sort((a, b) => a.time - b.time)
        return result
    }

    // 更新当前歌词索引
    const updateLyricIndex = () => {
        if (!lyrics.value.length) return

        let index = 0
        for (let i = 0; i < lyrics.value.length; i++) {
            if (lyrics.value[i].time > currentTime.value) {
                index = i - 1
                break
            }
            if (i === lyrics.value.length - 1) {
                index = i
            }
        }

        currentLyricIndex.value = index < 0 ? 0 : index
    }

    // 播放/暂停切换
    const togglePlay = () => {
        if (!globalAudioContext || !currentSong.value) return

        if (isLoading.value) {
            uni.showToast({
                title: '音频加载中，请稍候',
                icon: 'none'
            })
            return
        }

        if (isPlaying.value) {
            globalAudioContext.pause()
        } else {
            globalAudioContext.play()
        }

        isPlaying.value = !isPlaying.value
    }

    // 播放
    const play = () => {
        if (!globalAudioContext || !currentSong.value) return

        if (isLoading.value) {
            uni.showToast({
                title: '音频加载中，请稍候',
                icon: 'none'
            })
            return
        }

        // 先停止其他可能在播放的音频
        if (globalAudioContext.paused === false) {
            globalAudioContext.stop()
            setTimeout(() => {
                globalAudioContext.play()
                isPlaying.value = true
            }, 50)
        } else {
            globalAudioContext.play()
            isPlaying.value = true
        }
    }

    // 暂停
    const pause = () => {
        if (!globalAudioContext) return

        globalAudioContext.pause()
        isPlaying.value = false
    }

    // 跳转到指定时间
    const seekTo = (time) => {
        if (!globalAudioContext) return

        // 记录当前的播放状态
        const wasPlaying = isPlaying.value

        // 暂停播放，避免跳转时出现音频重叠
        pause()

        // 设置新的播放时间
        globalAudioContext.seek(time)
        currentTime.value = time

        // 如果之前是播放状态，则延迟一小段时间后恢复播放
        if (wasPlaying) {
            setTimeout(() => {
                play()
            }, 100)
        }
    }

    // 获取音频上下文
    const getAudioContext = () => {
        return globalAudioContext || initAudioContext()
    }

    // 清理资源
    const cleanup = () => {
        if (globalAudioContext) {
            try {
                // 先暂停和停止当前播放
                globalAudioContext.pause()
                globalAudioContext.stop()

                // 移除所有事件监听
                globalAudioContext.offTimeUpdate()
                globalAudioContext.offEnded()
                globalAudioContext.offCanplay()
                globalAudioContext.offError()

                // 销毁上下文
                globalAudioContext.destroy()
                globalAudioContext = null
            } catch (e) {
                console.error('清理音频资源失败:', e)
            }
        }
    }

    return {
        // 状态
        currentSong,
        isPlaying,
        currentTime,
        duration,
        lyrics,
        currentLyricIndex,
        isLoading,
        sliderMax,

        // 方法
        initAudioContext,
        loadSong,
        togglePlay,
        play,
        pause,
        seekTo,
        getAudioContext,
        cleanup
    }
}) 