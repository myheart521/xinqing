import { HistoryMessages, HttpHeaders, Message, MiddleData, ParsedData } from '../../model/ai/EmollmResponse';
import { http } from '@kit.NetworkKit';
import { JSONUtil, ObjectUtil, StrUtil } from '@q/harmony-utils';
import { JSON } from '@kit.ArkTS';
import { SpinKit, SpinType } from '@pura/spinkit';
import { KeyboardUtil } from '@pura/harmony-utils/src/main/ets/utils/KeyboardUtil';

// API的URL
const API_URL = "http://localhost:8080/chat/completions";
const headers: HttpHeaders = {
  'Authorization': 'c752945b3f13b762c5b4b0a2501d35411d9222ec',
  'Content-Type': 'application/json'
};


@Component
export struct ChatIndex {
  appendStringGradually = (interval: number) => {
    const intervalId = setInterval(() => {
      if (this.currentIndex < this.joinResult.length) {
        console.log("拼接字符", this.joinResult[this.currentIndex])
        this.result += this.joinResult[this.currentIndex]; // 逐个字符拼接
        this.currentIndex++; // 递增索引
      } else if (!this.isOutput && this.currentIndex >= this.joinResult.length) {
        this.messageHistory.push({
          role: 'assistant',
          content: this.result
        });
        this.currentIndex = 0
        this.scroller.scrollEdge(Edge.Top)
        clearInterval(intervalId); // 当字符拼接完毕时停止定时器
      }
    }, interval); // 每隔 interval 毫秒执行一次
  }
  aiOutput = (userInput: string) => {
    console.log("输入数据：", userInput)

    this.appendStringGradually(60)
    // 将用户消息加入到历史中
    this.messageHistory.push({
      role: 'user',
      content: userInput
    });
    this.scroller.scrollEdge(Edge.Bottom)
    // 创建HTTP请求对象
    let httpRequest = http.createHttp();

    // 订阅HTTP响应头事件
    httpRequest.on('headersReceive', (header: Object) => {
      console.info('header: ' + JSON.stringify(header));
    });
    // 订阅HTTP流式响应数据接收事件
    httpRequest.on('dataReceive', (data: ArrayBuffer) => {
      // 将 ArrayBuffer 转换为字符串
      const jsonStr: string = StrUtil.bufferToStr(data)
      this.json = this.json + jsonStr
      console.log("result数据json", jsonStr)
      //提取对话内容
      const regex = /data:.*?"result":"([\s\S]*?)"/g;
      let match: RegExpMatchArray | null;
      let results: string[] = [];
      while ((match = regex.exec(jsonStr) as RegExpMatchArray | null) !== null) {
        results.push(match[1]);
      }
      // 拼接所有 result 的值
      const combinedResult = results.join('');
      console.log("result数据：", combinedResult);
      this.joinResult += combinedResult
      //提取是否是最后一句话
      // const isEndReg = /"is_end":(true|false)/g;
      // const isEndMatches:RegExpMatchArray | null = jsonStr.match(isEndReg);
      // if (isEndMatches) {
      //   isEndMatches.forEach(match => {
      //     const isEnd = match.split(':')[1].trim();
      //     if(isEnd === "true"){
      //
      //     }
      //   });
      // } else {
      //   console.log('没有发现is_end');
      // }
    });
    // 订阅HTTP流式响应数据接收完毕事件
    httpRequest.on('dataEnd', () => {
      setTimeout(() => {
        //回复结束
        this.isOutput = false
        // console.log("流式输出结束");
      }, 4000)
    });

    // 订阅HTTP流式响应数据接收进度事件
    interface DataProgress {
      receiveSize: number;
      totalSize: number;
    }

    httpRequest.on('dataReceiveProgress', (data: DataProgress) => {
      console.log("dataReceiveProgress receiveSize:" + data.receiveSize + ", totalSize:" + data.totalSize);
    });
    // 准备HTTP请求的相关信息
    let streamInfo: http.HttpRequestOptions = {
      method: http.RequestMethod.POST,
      header: headers, // 传递请求头
      extraData: {
        stream: true, // 启用流式响应
        messages: this.messageHistory // 使用消息历史来实现多轮对话
      },
      expectDataType: http.HttpDataType.STRING, // 指定返回数据的类型为字符串
      usingCache: false,
      connectTimeout: 60000,
      readTimeout: 60000
    };
    // 发起流式网络请求
    httpRequest.requestInStream(API_URL, streamInfo).then((data: number) => {
      console.log("请求成功: " + data);
      // 取消订阅事件
      httpRequest.off('headersReceive');
      httpRequest.off('dataReceive');
      httpRequest.off('dataReceiveProgress');
      httpRequest.off('dataEnd');
      // 销毁请求对象
      httpRequest.destroy();
    }).catch((err: Error) => {
      console.error("请求出错: " + JSON.stringify(err));
    });
  };
  @State messageHistory: Message[] = [];
  // @ObjectLink mess:HistoryMessages
  //展示的输出内容
  @State result: string = ''
  //拼接的输出内容
  @State joinResult: string = ""
  //输入框内容
  @State userInput: string = ""
  @State json: string = ''
  //判断是否在输出：
  @State isOutput: boolean = false
  @State currentIndex: number = 0
  //滚动条控制
  private scroller: Scroller = new Scroller();

  build() {
    Column() {
      Column() {
        Scroll(this.scroller) {
          Column() {
            if (this.messageHistory.length == 0) {
              Row() {
                Image($rawfile("functionImage/music_picture4.png"))
                  .backgroundColor(Color.White)
                  .width(50)
                  .height(50)
                  .borderRadius(4)
                  .margin({ left: 8, right: 8 })
                Text("你好呀有什么烦心事吗？尽情向我倾诉吧，我会好好倾听的。")
                  .width("65%")
                  .padding(15.5)
                  .backgroundColor(Color.White)
                  .borderRadius(4)
                  .margin({ right: 0 })
              }
              .margin({ top: 12 })
              .width("100%")
              .alignItems(VerticalAlign.Top)
              .justifyContent(FlexAlign.Start)
            }
            ForEach(this.messageHistory, (item: Message, index: number) => {
              if (item.role == "user") {
                Row() {
                  Text(item.content)
                    .textAlign(TextAlign.End)
                    .width("60%")
                    .alignSelf(ItemAlign.End)
                    .padding(15.5)
                    .backgroundColor("#95EC69")
                    .borderRadius(4)
                  Image($r("app.media.account"))
                    .backgroundColor(Color.White)
                    .objectFit(ImageFit.Fill)
                    .width(50)
                    .height(50)
                    .borderRadius(4)
                    .margin({ left: 8, right: 8 })
                }
                .margin({ top: index === 0 ? 0 : 12 })
                .width("100%")
                .alignItems(VerticalAlign.Top)
                .justifyContent(FlexAlign.End)
              } else if (item.role == "assistant") {
                Row() {
                  Image($rawfile("functionImage/music_picture4.png"))
                    .backgroundColor(Color.White)
                    .objectFit(ImageFit.Fill)
                    .width(50)
                    .height(50)
                    .borderRadius(4)
                    .margin({ left: 8, right: 8 })
                  Text(item.content)
                    .width("65%")
                    .padding(15.5)
                    .backgroundColor(Color.White)
                    .borderRadius(4)
                    .margin({ right: 0 })
                }
                .margin({ top: index === 0 ? 0 : 12 })
                .width("100%")
                .alignItems(VerticalAlign.Top)
                .justifyContent(FlexAlign.Start)
              }
            })
            //此处是流式展示响应的结果，提高用户的体验
            if (this.isOutput || this.currentIndex < this.joinResult.length - 1 && this.currentIndex != 0) {
              if (this.result == "") {
                //加载等待
                Row() {
                  Image($rawfile("functionImage/music_picture4.png"))
                    .backgroundColor(Color.White)
                    .objectFit(ImageFit.Fill)
                    .width(50)
                    .height(50)
                    .borderRadius(4)
                    .margin({ left: 8, right: 8 })
                  SpinKit({
                    spinType: SpinType.spinC,
                    spinColor: "#dd5145",
                    spinSize: 40
                  })
                    .backgroundColor(Color.White)
                    .padding(15.5)
                }
                .margin({ top: 12 })
                .width("100%")
                .alignItems(VerticalAlign.Top)
                .justifyContent(FlexAlign.Start)
                .borderRadius(4)
              } else {
                Row() {
                  Image($rawfile("functionImage/music_picture4.png"))
                    .backgroundColor(Color.White)
                    .objectFit(ImageFit.Fill)
                    .width(50)
                    .height(50)
                    .borderRadius(4)
                    .margin({ left: 8, right: 8 })
                  Text(this.result)
                    .width("65%")
                    .padding(15.5)
                    .backgroundColor(Color.White)
                    .borderRadius(4)
                    .margin({ right: 0 })
                }
                .margin({ top: 12 })
                .width("100%")
                .alignItems(VerticalAlign.Top)
                .justifyContent(FlexAlign.Start)
              }
            }
          }
          .padding({ top: 12, bottom: 12 })
        }
      }
      .backgroundColor("#EDEDED")
      .layoutWeight(1)

      Row() {
        TextInput({
          placeholder: "来和艾薇聊天吧", text: $$this.userInput
        })
          .backgroundColor(Color.White)
          .layoutWeight(8)
          .height(60)
          .margin({
            top: 10,
            bottom: 10,
            left: 4,
            right: 4
          })
        Button() {
          Row() {
            if (this.isOutput || this.currentIndex < this.joinResult.length - 1 && this.currentIndex != 0) {
              SpinKit({
                spinType: SpinType.spinC,
                spinColor: "#dd5145",
                spinSize: 40
              })
            } else {
              Text("发送")
            }
          }
        }
        .height(45)
        .backgroundColor(this.isOutput || this.currentIndex < this.joinResult.length - 1 && this.currentIndex != 0 ?
          "#FFF" : "#00d886")
        .margin(4)
        .layoutWeight(2)
        .onClick(() => {
          if (!this.isOutput) {
            KeyboardUtil.hide()
            const sendAi = this.userInput
            //回复数据置为空
            this.result = ""
            this.joinResult = ""
            console.log("sendAi", sendAi)
            //设置为开始回答
            this.isOutput = true
            this.aiOutput(sendAi)
            //用户输入框置为空
            this.userInput = ""
          }
        })
      }
      .backgroundColor("#F7F7F7")
    }
    .width("100%")
    .height("100%")
    .justifyContent(FlexAlign.SpaceBetween)
  }
}