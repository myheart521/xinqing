@Observed
export class PCCModel {
  id: number = 1
  phoneNumber: string = ''
  email: string = 'demo@example.invalid'
  location: string = '示例资料，请配置实际服务信息'
  school: string = '示例资料，请配置实际服务信息'
  src: string = 'https://assets.example.invalid/placeholder.png'
  province: string = '示例资料，请配置实际服务信息'

  constructor(id: number, phoneNumber: string, email: string, location: string, school: string, src: string, province: string) {
    this.id = id
    this.phoneNumber = phoneNumber
    this.email = email
    this.location = location
    this.school = school
    this.src = src
    this.province = province
  }
}