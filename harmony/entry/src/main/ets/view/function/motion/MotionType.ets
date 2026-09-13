@Component
export struct MotionType{
  @Prop content:string
  build(){
    Text(this.content)
      .fontSize(10)
      .padding(2)
      .fontColor("#5fbd9d")
      .borderColor("#7abaa7")
      .borderWidth(1)
      .borderRadius(5)
  }
}