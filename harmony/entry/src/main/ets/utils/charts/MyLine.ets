import {
  JArrayList, // 工具类：数据集合
  XAxis, // 图表X轴部件
  XAxisPosition, // 图表X轴标签位置枚举类
  YAxis, // 图表Y轴部件
  Description, // 图表描述标签
  Legend, // 图表Legend(图例)部件
  OnChartValueSelectedListener, //
  Highlight, // 图表高亮数据
  EntryOhos, // 图表数据结构基础类
  YAxisLabelPosition, // 图表Y轴标签位置枚举类
  LineDataSet, //线形图数据集合
  ILineDataSet, // 线形图数据集合的操作类
  LineData, //线形图数据包
  Mode, //线形图形状
  LineChart, // 线形图图表类
  LineChartModel, // 线形图配置构建类
  LimitLine, // 图表LimitLine
  LimitLabelPosition, // 图表的LimitLine标签位置枚举类
  ChartColorStop, //颜色类
  LegendForm,
  MarkerView,
  IAxisValueFormatter,
  AxisBase,
  ColorTemplate,
  CustomUiInfo,
  EventType, //图例形状
} from '@ohos/mpchart';

// 自定义Y轴标签格式化器
class YAxisValueFormatter implements IAxisValueFormatter {
  getFormattedValue(value: number, axis: AxisBase): string {
    return `${value.toFixed(1)}`;
  }
}

// 获取 xData 最大值
function getMaxX(xData: string): number {
  const xArray = xData.split(",").map(Number);
  return Math.max(...xArray);
}

// 获取 yData 最大值
function getMaxY(yData: string): number {
  const yArray = yData.split(",").map(Number);
  return Math.max(...yArray);
}


@Component
export struct MyLine {
  dataSet?: LineDataSet;
  normalMarker?: MarkerView;
  xAxis?: XAxis | null;
  leftAxis?: YAxis | null;
  limitLine1?: LimitLine;
  model?: LineChartModel;
  @State customUiInfo: CustomUiInfo = new CustomUiInfo(80, 40);
  //x轴数据
  @Link xData: string
  //y轴数据
  @Link @Watch("changeYData") yData: string
  //x轴发来的数据中最大值
  @State xMax: number = 0
  //y轴最大值
  @State yMax: number = 0

  @Builder
  customUi() {
    // 是否在图表content内
    if (this.customUiInfo.isInbounds && this.customUiInfo.data) {
      Column() {
        Text(`x轴数据:${this.customUiInfo.data?.getX()}`)
          .fontColor(Color.Gray)
          .fontSize(12)
          .fontWeight(FontWeight.Bold);
        Text(`y轴数据:${this.customUiInfo.data?.getY()}`).fontColor(Color.Gray).fontSize(12);
      }
      .padding(4)
      .borderRadius(6)
      .border({ width: 1, color: Color.Orange })
      .backgroundColor(0xf0f0f0)
      .width(this.customUiInfo.width)
      .height(this.customUiInfo.height)
      .margin({ left: this.customUiInfo.x, top: this.customUiInfo.y })
      .alignItems(HorizontalAlign.Start)
      .onClick(ev => {
        this.customUiInfo.showUi = false; // 点击后隐藏MarkerView
      });
    }
  }

  // 构造数据选择监听器
  private valueSelectedListener: OnChartValueSelectedListener = {
    onValueSelected: (e: EntryOhos, h: Highlight) => {
      // ...todoSomething
    },
    onNothingSelected: () => {
      // ...todoSomething
    }
  }

  // 图表数据初始化
  aboutToAppear() {
    this.changeYData()
  }

  private getLineData(): LineData {
    let start: number = 1;
    let values: JArrayList<EntryOhos> = new JArrayList<EntryOhos>();
    let myData = this.yData.split(",").map(Number)
    myData.forEach((item, index) => {
      values.add(new EntryOhos(index + 1, item))
    })

    this.dataSet = new LineDataSet(values, 's');
    this.dataSet.setHighLightColor(Color.Black);
    this.dataSet.setDrawIcons(true);
    this.dataSet.setMode(Mode.CUBIC_BEZIER); //曲线模式
    this.dataSet.setDrawCircles(true); //画圆圈
    this.dataSet.setDrawCircleHole(false); //设置内部孔
    this.dataSet.setColorByColor(Color.Black); //设置折线颜色
    this.dataSet.setCircleColor(ColorTemplate.rgb("#FF4500")); // 数据点为橙色
    this.dataSet.setCircleRadius(6); // 设置数据点半径为6
    //渐变色填充
    let gradientFillColor = new JArrayList<ChartColorStop>();
    gradientFillColor.add(["#0C0099CC", 0.2]);
    gradientFillColor.add(["#7F0099CC", 0.4]);
    gradientFillColor.add(["#0099CC", 1.0]);
    this.dataSet.setGradientFillColor(gradientFillColor);
    this.dataSet.setDrawFilled(true);
    // 设置数据点的半径
    this.dataSet.setCircleHoleRadius(2); //设置内径
    let dataSetList: JArrayList<ILineDataSet> = new JArrayList<ILineDataSet>();
    dataSetList.add(this.dataSet);
    let lineData: LineData = new LineData(dataSetList);
    return lineData
  }

  build() {
    Column() {
      LineChart({
        model: this.model,
        customUiInfo: this.customUiInfo, // 自定义 ui: 传入 builder
        customUiBuilder: this.customUi,
        // 通过什么事件触发
        customUiTriggerEvent: EventType.SingleTap,
      })
        .width("100%")
        .height(300)
    }

  }

  changeYData() {
    this.xMax = getMaxX(this.xData)
    this.yMax = getMaxY(this.yData)
    // Step1:必须：初始化图表配置构建类
    this.model = new LineChartModel();
    // Step2:配置图表指定样式，各部件间没有先后之分
    // 为图表添加数据选择的监听器
    this.model.setOnChartValueSelectedListener(this.valueSelectedListener);
    // 获取图表描述部件，设置图表描述部件不可用，即图表不进行绘制描述部件
    let description: Description | null = this.model.getDescription()
    if (description) {
      description.setEnabled(false);
    }

    // 获取图表图例部件，设置图表图例形状为线形
    let legend: Legend | null = this.model.getLegend();
    if (legend) {
      legend.setEnabled(true);
      // draw legend entries as lines
      legend.setForm(LegendForm.LINE);
    }
    // 设置图表数据最大的绘制数，如果超过该数值，则不进行绘制图表的数值标签
    this.model.setMaxVisibleValueCount(60);
    this.model.getLegend()?.setEnabled(false); // 关闭图例
    this.model.getDescription()?.setEnabled(false); // 隐藏右下角的描述
    this.model.setHighlightPerTapEnabled(true); // 允许点击高亮

    // 为左Y轴设置LimitLine,可设置限制线的宽度，线段样式，限制标签的位置，标签字体大小等
    this.limitLine1 = new LimitLine(120, 'Upper Limit');
    this.limitLine1.setLineWidth(4);
    //设置虚线样式
    this.limitLine1.enableDashedLine(10, 10, 0);
    //设置标签位置
    this.limitLine1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
    this.limitLine1.setTextSize(10);

    // 设置图表左Y轴信息
    this.leftAxis = this.model.getAxisLeft();
    if (this.leftAxis) {
      //设置标签位置
      this.leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
      //设置距离顶部距离
      this.leftAxis.setSpaceTop(20);
      //设置最小值
      this.leftAxis.setAxisMinimum(0);
      //设置最大值
      // this.leftAxis.setAxisMaximum(200);
      // 使用自定义的格式化器来格式化Y轴的标签
      this.leftAxis.setValueFormatter(new YAxisValueFormatter());
      // 自定义Y轴标签为小时，字体颜色设置为蓝色，大小为16
      this.leftAxis.setTextColor(ColorTemplate.rgb("#0099FF")); // 蓝色字体
      this.leftAxis.setTextSize(13); // 字体大小16
      this.leftAxis.setSpaceMax(0.3)
      this.leftAxis.setAxisMaximum(this.yMax + 3)
    }

    // 设置X轴信息
    this.xAxis = this.model.getXAxis();
    if (this.xAxis) {
      this.xAxis.setPosition(XAxisPosition.BOTTOM);
      this.xAxis.setDrawGridLines(false);
      this.xAxis.setGranularity(1);
      this.xAxis.setLabelCount(7);
      this.xAxis.setLabelCount(this.xData.split(",").length, false);
      // 设置Y轴线的颜色为红色，宽度为2
      this.xAxis.setAxisLineColor(ColorTemplate.rgb("#FF0000")); // 红色轴线
      this.xAxis.setAxisLineWidth(2); // 轴线宽度为2
      this.xAxis.setGridColor(ColorTemplate.rgb("#AAAAAA")); // 网格线为灰色
      this.xAxis.setGridLineWidth(0.5); // 网格线宽度为0.5
      this.xAxis.enableGridDashedLine(10, 5, 0); // 使用虚线网格线
      this.xAxis.setTextColor(ColorTemplate.rgb("#0099FF")); // 蓝色字体
      this.xAxis.setTextSize(13); // 字体大小16
      this.xAxis.setValueFormatter(new XValueFormatter(this.xData));
      this.xAxis.setSpaceMax(0.2); // 增加 X 轴数据距离右侧的距离
    }
    // 为图表设置markerView
    // this.normalMarker = new MarkerView();
    // this.model.setMarker(this.normalMarker);
    // 生成图表数据
    let lineData: LineData = this.getLineData();
    // 将数据与图表配置类绑定
    this.model.setData(lineData);
    // 设置图表最大的X轴显示范围，如不设置，则默认显示全部数据
    this.model.setVisibleXRangeMaximum(20);
    this.model.animateXY(1000, 1000)
    this.model.setDoubleTapToZoomEnabled(false);
    this.model.getAxisRight()?.setEnabled(false);
  }
}

// 设置X轴数据的格式转换器
class XValueFormatter implements IAxisValueFormatter {
  private dates: string[];

  constructor(dates: string) {
    this.dates = dates.split(",")
  }

  // 根据位置获取并格式化日期
  getFormattedValue(value: number): string {
    const index = value - 1
    if (this.dates[index]) {
      if (index >= 0 && index < this.dates.length) {
        const dateParts = this.dates[index].split("-");
        return dateParts[1] + "/" + dateParts[2];
      }
    } else {
      return ""
    }
    return '';
  }
}