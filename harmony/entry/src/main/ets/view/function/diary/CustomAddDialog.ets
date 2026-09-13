@Component
export  default struct CustomAddDialog{
  @Link addContent:string
  onClose:() => void =()=>{}
  onAdd:() => void = ()=>{}
  build(){
    Stack(){
      //蒙层
      Rect()
        .fill('rgba(0,0,0,0.4)')
        .width('100%')
        .height('100%')
        .onClick(()=>{
          this.onClose()
        })
      //弹窗
      Column(){
        //文本
        Column(){
          TextArea({
            placeholder:'请输入内容',
            text:this.addContent
          })
            .backgroundColor('#ffffff')
            .height('60%')
            .onChange((value)=>{
              this.addContent = value
            })
        }
        .width('100%')
        .margin(20)
        .padding(20)
        //按钮
        Row(){
          //返回
          Column(){
            Image($r('app.media.back'))
              .width(24)
              .height(24)
          }
          .backgroundColor('#ececf6')
          .width(60)
          .height(60)
          .borderRadius(30)
          .justifyContent(FlexAlign.Center)
          .onClick(()=>{
            this.onClose()
          })
          //保存
          Column(){
            Image($rawfile('functionImage/diary_save.png'))
              .width(24)
              .height(24)
          }
          .backgroundColor('#ececf6')
          .width(60)
          .height(60)
          .borderRadius(30)
          .justifyContent(FlexAlign.Center)
          .onClick(()=>{
            this.onAdd()
          })
        }
        .width('100%')
        .justifyContent(FlexAlign.SpaceAround)
      }
      .width('100%')
      .height('50%')
      .backgroundColor('#f1f5f8')
      .borderRadius({topLeft:16,topRight:16})

    }
    .width('100%')
    .height('100%')
    .alignContent(Alignment.Bottom)
  }
}

@Component
@Preview
struct CustomEditDialogPreview{
  @State addContent:string=''
  build(){
    CustomAddDialog({addContent:$addContent})
  }
}