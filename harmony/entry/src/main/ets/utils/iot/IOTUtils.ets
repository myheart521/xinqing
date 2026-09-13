import { navItem } from '../../model/index/navItem'
import { bodyFatScaleModel, useData } from '../../model/iot/BodyFatScale'
import { createEquipment, equipmentType } from '../../model/iot/Equipment'
import { runningModel } from '../../model/iot/Running'
import { wristWatchModel } from '../../model/iot/WristWatch'

//格式化体脂秤顶部展示数据
export function changeBodyFatScale(bodyFatScaleData: bodyFatScaleModel) {
  const data: useData[] = [
    { info: "身高", data: bodyFatScaleData.height, image: $rawfile("iotImage/height.svg") },
    { info: "体重", data: bodyFatScaleData.weight, image: $rawfile("iotImage/weight.svg") },
    { info: "BMI", data: bodyFatScaleData.bmi, image: $rawfile("iotImage/bmi.svg") },
    { info: "体脂率", data: bodyFatScaleData.tiZhiLv, image: $rawfile("iotImage/body_fat_scale.svg") }
  ]
  return data
}

//格式化跑步机顶部展示数据
export function changeRunning(runningData: runningModel) {
  const data: useData[] = [
    { info: "总距离", data: runningData.diatance, image: $rawfile("iotImage/distance.svg") },
    { info: "总运动时长", data: runningData.longTime, image: $rawfile("iotImage/time.svg") },
  ]
  return data
}

//格式化稚嫩恶搞手表顶部数据
export function changeWatch(watchData: wristWatchModel) {
  const data: useData[] = [
    { info: "睡眠时长", data: watchData.sleepTime, image: $rawfile("iotImage/sleep_time.svg") },
    { info: "久坐时长", data: watchData.sedentaryTime, image: $rawfile("iotImage/sedentary_time.svg") },
    { info: "心率", data: watchData.heartRate, image: $rawfile("iotImage/heart_rate.svg") },
    { info: "体温", data: watchData.bodyTemperature, image: $rawfile("iotImage/body_temperature.svg") }
  ]
  return data
}

//格式化传输过来的设备名称
export function changeEquipment(equipment: equipmentType[]) {
  const useEquipment: equipmentType[] = []
  equipment.forEach((item, index) => {
    if (item.label == 'body') {
      useEquipment.push({ label: "智能体脂秤", id: item.id })
    } else if (item.label == "running") {
      useEquipment.push({ label: "跑步机", id: item.id })
    } else if (item.label == "smartWatch") {
      useEquipment.push({ label: "智能手表", id: item.id })
    }
  })
  return useEquipment
}

//格式化sliderBar里面的数据
export function changeSlider(equipment: equipmentType[]) {
  const navData: navItem[] = []
  equipment.forEach((item, index) => {
    if (item.label == 'body') {
      navData.push(new navItem(item.id, $rawfile("iotImage/body_fat_scale.svg"), "智能体脂秤"))
    } else if (item.label == "running") {
      navData.push(new navItem(item.id, $rawfile("iotImage/treadmill.svg"), "跑步机"))
    } else if (item.label == "smartWatch") {
      navData.push(new navItem(item.id, $rawfile("iotImage/wristwatch_normal.png"), "智能手表"))
    }
  })
  return navData
}


// 判断是否存在设备
// 传入设备的名称比如“智能体脂秤”和渲染的navList
export function judgeExistEquipment(name: string, navList: navItem[]): boolean {
  for (let i = 0; i < navList.length; i++) {
    if (name === navList[i].name) {
      return false; // 找到设备，返回 false
    }
  }
  return true; // 如果没有找到设备，返回 true
}

//新增设备判断新增哪种设备
export function changeAddEquipment(name:string){
  if(name=="智能体脂秤"){
    return createEquipment.body
  }else if(name == "跑步机"){
    return createEquipment.running
  }else if(name == "智能手表"){
    return createEquipment.watch
  }else{
    return ""
  }
}



