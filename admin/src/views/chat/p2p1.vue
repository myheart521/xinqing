<template>
<dev>我的ID是  {{myId}}</dev>
      <!-- 本地视频 -->
      <div class="local-video-wrapper">
        <video ref="localVideo" autoplay muted class="local-video"></video>
        <div class="video-label">我（本地）</div>
      </div>
      <!-- 远程视频 -->
    <video ref="remoteVideo" 
            autoplay
            class="remote-video"></video>

    <input type="text" v-model="targetUserId" placeholder="请输入你朋友的的ID" />
    <button @click="connect">连接</button>

</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'

const localStream = ref(null)        // 本地媒体流对象
const localVideo = ref(null)        // 本地视频标签
const remoteVideo = ref(null)        // 远程视频标签
const remoteStream = ref(null)       // 远程媒体流对象
const pc = ref(null)                // RTCPeerConnection对象
let myId=ref(null)                // 本地id
let targetUserId=ref(null)            // 远程id
const ws=ref(null)                // websocket对象

const connect=()=>{
    initPeerConnection(targetUserId.value)
}

const initWebSocket=()=>{
    ws.value=new WebSocket("ws://localhost:8080/signal")
    ws.value.onopen=()=>{
        console.log("ws连接成功")
    }

    ws.value.onmessage=(event)=>{
        console.log(event.data)
        const msg=JSON.parse(event.data)
        console.log(msg)
        switch(msg.type){
            case "offer":
                handleOffer(msg.data,msg.from)
                break
            case "answer":
                handleAnswer(msg.data)
                break
            case "candidate":
                handleCandidate(msg.data)
                break
            case "login":
                myId.value=msg.data
                break
        }
    }

    ws.value.onclose=()=>{
        console.log("ws连接关闭")
    }
}




const getLocalMedia= async () => {
    localStream.value=await navigator.mediaDevices.getUserMedia({
        video:true,
        audio:true
    })
    localVideo.value.srcObject=localStream.value
}

//
const createPeerConnection=(targetUserId)=>{
    //配置ICE服务器
    pc.value=new RTCPeerConnection({
        iceServers:[{
            urls:[          
          'stun:global.stun.twilio.com:3478',
          'stun:stun.xten.com:3478',
          'stun:stun.l.google.com:19302']
        }]
    })

    //向pc中添加本地的轨道, 准备发给远程的人做准备
    localStream.value.getTracks().forEach(track=>{
        pc.value.addTrack(track,localStream.value)
        console.log('向pc中添加本地的轨道',track)
    })


    //接受到远程轨道之后的处理逻辑
    pc.value.ontrack=async (event)=>{
        if(event.streams!=null&&event.streams.length>0){
            remoteStream.value=event.streams[0]
            remoteVideo.value.srcObject=remoteStream.value
            remoteVideo.value.muted=false
            remoteVideo.value.play()
        }
    }


    pc.value.onicecandidate=(event)=>{
        if(event.candidate){
            sendSingalMsg({
                type: 'candidate',
                data: event.candidate,
                target: targetUserId,
                from:myId.value
            })
        }
    }
}

const initPeerConnection=async(targetUserId)=>{
    createPeerConnection(targetUserId)
    //offer存入本地和发送给远程
    const offer=await pc.value.createOffer()
    await pc.value.setLocalDescription(offer)
    sendSingalMsg({
        type:"offer",
        data:offer,
        target:targetUserId,
        from:myId.value
    })
}

const handleOffer=async(offer,from)=>{
    targetUserId.value=from
    createPeerConnection(targetUserId.value)
    pc.value.setRemoteDescription(new RTCSessionDescription(offer))
    const answer=await pc.value.createAnswer()
    pc.value.setLocalDescription(answer)
    sendSingalMsg({
        type:"answer",
        data:answer,
        target:targetUserId.value,
        from:myId.value
    })
}


const handleAnswer=(answer)=>{
    pc.value.setRemoteDescription(new RTCSessionDescription(answer))
}

const handleCandidate=(candidate)=>{
    pc.value.addIceCandidate(new RTCIceCandidate(candidate))
}


const sendSingalMsg=(msg)=>{
    ws.value.send(JSON.stringify(msg))

}



onMounted(async() => {
    await getLocalMedia()
    initWebSocket()
})

onBeforeUnmount(() => {
  if (localStream.value) {
    localStream.value.getTracks().forEach(track => track.stop())
  }
  if (ws.value) {
    ws.value.close()
  }
})

</script>

<style scoped>
.local-video-wrapper {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 240px;
  height: 180px;
  border: 2px solid #4CAF50;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  z-index: 100;
}

video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scaleX(-1); /* 翻转视频- 保证视频不镜像 */
}

.remote-video-wrapper {
  background: #222;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
}
</style>