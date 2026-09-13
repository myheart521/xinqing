 interface WeekInfo {
  week: number;
  day: number;
}


@Component
export default struct CustomEditDate{

  date:string[] = ['日','一','二','三','四','五','六']
  day:string[] = []

  //获取是星期几
  getWeek():WeekInfo{
    const date = new Date()
    const day = date.getDate()
    const week = date.getDay()
    return {week,day}
  }
  getDay(){
    const week =  this.getWeek().week
    const date = new Date()
    const date1 = (new Date()).getTime()
    for(let i=0;i<week;i++){
      const time = (date.getTime()-86400000*(week-i))
      this.day.push(((new Date(time)).getDate()).toString())
    }
    this.day.push((date.getDate()).toString())
    for(let i=week;i<7;i++){
      const time = (date.getTime()+86400000*(i-week+1))
      this.day.push(((new Date(time)).getDate()).toString())
    }
  }
  aboutToAppear(){
    this.getDay()
  }
  build(){
    Column(){
      Grid(){
        ForEach(
          this.date,
          (item:string,index)=>{
            GridItem(){
              if(index === 0|| index === 6){
                Text(item)
                  .fontColor('#ff89b1')
              }else{
                Text(item)
              }
            }
          }
        )
          ForEach(
            this.day,
            (item:string,index)=>{
              GridItem(){
                if(index === this.getWeek().week && index!=0){
                  Text(item)
                    .backgroundColor('#fe88b0')
                    .fontColor(Color.White)
                    .borderRadius(5)
                }else if(index === 0 && this.getWeek().week === 7){
                  Text(item)
                    .backgroundColor('#fe88b0')
                    .fontColor(Color.White)
                    .borderRadius(6)
                }
                else{
                  Text(item)
                }
              }
            }
          )
      }
      .height(70)
      .width('90%')
      .columnsTemplate('1fr 1fr 1fr 1fr 1fr 1fr 1fr')
      .rowsTemplate('1fr 1fr')
      .backgroundColor(Color.White)
    }
    .width('100%')
    .backgroundColor('#f1f5f8')
    .margin({top:10})
  }
}
@Preview
@Component struct PreviewCustomEditDate{
  build(){
    CustomEditDate()
  }
}