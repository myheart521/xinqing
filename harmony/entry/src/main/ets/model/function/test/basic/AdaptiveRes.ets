// /ets/data/AnsReturn.ets
@Observed
export class AdaptiveRes {
  relationship: number = 0;//人际关系
  study: number = 0;//学习适应
  campus: number = 0;//校园生活适应
  job: number = 0;//择业适应
  emotion: number = 0;//情绪适应
  self: number = 0;//自我适应
  satisfaction: number = 0;//满意度
  total: number = 0;
  good: string[]=[];//比较好的方面
  bad: string[]=[];//需要修改的地方
  judge: string = ''//建议
  constructor(data?:AdaptiveRes) {
    if (data) {
      //this.res = data.res;
      this.relationship = data.relationship;
      this.study = data.study;
      this.campus = data.campus;
      this.job = data.job;
      this.emotion = data.emotion;
      this.self = data.self;
      this.satisfaction = data.satisfaction;
      this.total = data.total;
      this.judge = data.judge
      this.good = data.good
      this.bad = data.bad
    }
  }
}
