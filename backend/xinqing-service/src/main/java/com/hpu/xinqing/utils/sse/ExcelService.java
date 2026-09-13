package com.hpu.xinqing.utils.sse;

import com.hpu.xinqing.utils.sse.enums.FileType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Excel文件服务
 * 负责生成带有自定义格式的Excel文件并通过SSE传输到前端
 */
@Service
public class ExcelService {
    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

    private final FileTransferService fileTransferService;

    @Autowired
    public ExcelService(FileTransferService fileTransferService) {
        this.fileTransferService = fileTransferService;
    }

    /**
     * 创建并发送带有自定义格式的Excel文件
     *
     * @param userId       用户ID
     * @param fileName     文件名 (可选)
     * @param sheetName    工作表名称
     * @param headers      表头列表
     * @param data         数据列表 (每行数据是一个Map，key对应headers中的列名)
     * @param styleConfigs 样式配置器 (可选)
     * @return 是否成功启动传输流程
     */
    public boolean transferExcelFile(Long userId, String fileName, String sheetName,
                                     List<String> headers, List<Map<String, Object>> data,
                                     Consumer<Workbook> styleConfigs) {
        try {
            // 生成Excel文件
            byte[] excelBytes = createExcelFile(sheetName, headers, data, styleConfigs);

            // 将二进制数据转换为Base64字符串
            String base64Content = Base64.getEncoder().encodeToString(excelBytes);

            // 使用FileTransferService发送文件
            return transferRawExcelFile(userId, base64Content, fileName);
        } catch (Exception e) {
            log.error("创建Excel文件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 内部方法：发送原始Excel文件数据
     */
    private boolean transferRawExcelFile(Long userId, String base64Content, String fileName) {
        // 使用二进制文件传输方法处理Excel文件
        
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "excel_" + System.currentTimeMillis() + ".xlsx";
        } else if (!fileName.toLowerCase().endsWith(".xlsx")) {
            fileName = fileName + ".xlsx";
        }

        // 使用新增的transferBinaryFile方法，该方法专门处理已经Base64编码的二进制数据
        return fileTransferService.transferBinaryFile(userId, base64Content, FileType.EXCEL, fileName);
    }

    /**
     * 创建Excel文件并应用格式
     *
     * @param sheetName    工作表名称
     * @param headers      表头列表
     * @param data         数据列表
     * @param styleConfigs 样式配置器 (可选)
     * @return Excel文件的字节数组
     */
    public byte[] createExcelFile(String sheetName, List<String> headers,
                                  List<Map<String, Object>> data,
                                  Consumer<Workbook> styleConfigs) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 创建工作表
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet1");

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
                // 自动调整列宽
                sheet.autoSizeColumn(i);
            }

            // 填充数据行
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> rowData = data.get(i);

                for (int j = 0; j < headers.size(); j++) {
                    String header = headers.get(j);
                    Cell cell = row.createCell(j);

                    Object value = rowData.get(header);
                    setCellValue(cell, value);
                    cell.setCellStyle(dataStyle);
                }
            }

            // 应用自定义样式配置（如果有）
            if (styleConfigs != null) {
                styleConfigs.accept(workbook);
            }

            // 将工作簿写入字节数组输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置背景色为浅灰色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        // 设置居中对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 创建数据单元格样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // 设置垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 根据值类型设置单元格值
     */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 自定义Excel单元格样式的工具方法
     *
     * @param workbook    工作簿
     * @param sheet       工作表
     * @param rowIndex    行索引
     * @param columnIndex 列索引
     * @param style       要应用的样式
     */
    public static void applyCellStyle(Workbook workbook, Sheet sheet, int rowIndex, int columnIndex, Consumer<CellStyle> style) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }

        CellStyle cellStyle = workbook.createCellStyle();
        style.accept(cellStyle);
        cell.setCellStyle(cellStyle);
    }

    /**
     * 创建单元格样式构建器
     */
    public static class StyleBuilder {
        private final CellStyle style;
        private final Workbook workbook;

        public StyleBuilder(Workbook workbook) {
            this.workbook = workbook;
            this.style = workbook.createCellStyle();
        }

        public StyleBuilder backgroundColor(IndexedColors color) {
            style.setFillForegroundColor(color.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return this;
        }

        public StyleBuilder border(BorderStyle borderStyle) {
            style.setBorderBottom(borderStyle);
            style.setBorderLeft(borderStyle);
            style.setBorderRight(borderStyle);
            style.setBorderTop(borderStyle);
            return this;
        }

        public StyleBuilder alignment(HorizontalAlignment align) {
            style.setAlignment(align);
            return this;
        }

        public StyleBuilder verticalAlignment(VerticalAlignment align) {
            style.setVerticalAlignment(align);
            return this;
        }

        public StyleBuilder font(Consumer<Font> fontConfig) {
            Font font = workbook.createFont();
            fontConfig.accept(font);
            style.setFont(font);
            return this;
        }

        public StyleBuilder wrapText(boolean wrap) {
            style.setWrapText(wrap);
            return this;
        }

        public CellStyle build() {
            return style;
        }
    }


    /*
      使用方法：
      // 准备表头
              List<String> headers = List.of("姓名", "年龄", "成绩", "备注");

              // 准备数据
              List<Map<String, Object>> data = new ArrayList<>();
              data.add(Map.of("姓名", "张三", "年龄", 20, "成绩", 85.5, "备注", "优秀"));
              data.add(Map.of("姓名", "李四", "年龄", 21, "成绩", 92.0, "备注", "优秀"));
              data.add(Map.of("姓名", "王五", "年龄", 19, "成绩", 78.5, "备注", "良好"));

              // 自定义样式（可选）
              Consumer<Workbook> styleConfigs = workbook -> {
                  Sheet sheet = workbook.getSheetAt(0);

                  // 给特定单元格添加自定义样式
                  ExcelService.applyCellStyle(workbook, sheet, 2, 2, style -> {
                      // 给"王五"的成绩单元格设置为红色背景
                      style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                      style.setBorderBottom(BorderStyle.THIN);
                      style.setBorderLeft(BorderStyle.THIN);
                      style.setBorderRight(BorderStyle.THIN);
                      style.setBorderTop(BorderStyle.THIN);
                  });

                  // 使用StyleBuilder简化样式创建
                  CellStyle highScoreStyle = new ExcelService.StyleBuilder(workbook)
                      .backgroundColor(IndexedColors.LIGHT_GREEN)
                      .border(BorderStyle.THIN)
                      .alignment(HorizontalAlignment.CENTER)
                      .font(font -> {
                          font.setBold(true);
                          font.setColor(IndexedColors.BLUE.getIndex());
                      })
                      .build();

                  // 应用样式到高分单元格
                  Cell cell = sheet.getRow(1).getCell(2); // 李四的成绩
                  cell.setCellStyle(highScoreStyle);
              };

              // 发送Excel文件
              boolean success = excelService.transferExcelFile(
                  userId,
                  "学生成绩表.xlsx",
                  "成绩单",
                  headers,
                  data,
                  styleConfigs
              );
     */
}
