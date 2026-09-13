import { DoctorItemModel } from "./DoctorItemModel"
import { PCCTimeModel } from "./PCCTimeModel"

@Observed
export class PCCIntroModel {
  phoneNumber: string = ''
  email: string = 'demo@example.invalid'
  location: string = '示例资料，请配置实际服务信息'
  publicAccount:string="示例资料，请配置实际服务信息"
  method:string="示例资料，请配置实际服务信息"
  school: string = '示例资料，请配置实际服务信息'
  description:string='示例资料，请配置实际服务信息'
  province: string = '示例资料，请配置实际服务信息'
  src: string = 'https://assets.example.invalid/placeholder.png'
  doctorList:DoctorItemModel[]=[]
  timeSlots:PCCTimeModel[] = []

  constructor(phoneNumber: string, email: string, location: string,publicAccount:string,method:string, school: string,description:string, src: string, province: string,doctorList:DoctorItemModel[],timeSlots:PCCTimeModel[]) {
    this.phoneNumber = phoneNumber
    this.email = email
    this.location = location
    this.publicAccount = publicAccount
    this.method = method
    this.description = description
    this.school = school
    this.src = src
    this.province = province
    this.doctorList = doctorList
    this.timeSlots = timeSlots
  }
}