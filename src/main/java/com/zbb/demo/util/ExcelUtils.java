package com.zbb.demo.util;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持复合表头导出的Excel工具类
 *
 * @author liukun
 * @version 1.0
 * @date 2020/2/18 20:22
 */
public class ExcelUtils {
    private ExcelUtils() {
        throw new UnsupportedOperationException("initialization is prohibited...");
    }

    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 图片
     */
    public static class Image {
        private byte[] imageData;

        public Image(byte[] imageData) {
            this.imageData = imageData;
        }

        public byte[] getImageData() {
            return imageData;
        }
    }

    /**
     * 导出（目前仅支持小于两行表头的合并操作）
     * @param realName 导出文件名
     * @param data 导出数据
     * @param fields 导出字段,比如 date|时间,firstHeader|表头1,secondHeader|表头2,sub001|编号-0001|子标题1,sub002|编号-0001|子标题2,sub003|编号-0001|子标题3,sub004|编号-0001|子标题4,remark|备注
     */
    public static void export(HttpServletResponse response, String realName, List<Map<String, Object>> data, String fields) {
        String fileName = realName + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        writeFileToResponse(construct(fields, data, fileName), response);
    }

    /**
     * 导出并转换时间格式（目前仅支持小于两行表头的合并操作）
     * @param realName 导出文件名
     * @param data 导出数据
     * @param fields  导出字段,比如 date|时间,firstHeader|表头1,secondHeader|表头2,sub001|编号-0001|子标题1,sub002|编号-0001|子标题2,sub003|编号-0001|子标题3,sub004|编号-0001|子标题4,remark|备注
     * @param formatter 日期格式化字符串
     */
    public static void export(HttpServletResponse response, String realName, List<Map<String, Object>> data, String fields, String formatter) {
        convertDateToStr(data, formatter);
        export(response, realName, data, fields);
    }

    /**
     * 将文件写入响应流
     * @param file 文件
     * @param response 响应
     * @throws UnsupportedEncodingException
     */
    public static void writeFileToResponse(MultipartFile file, HttpServletResponse response) {
        // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");

        InputStream inputStream = null;
        try {
            String fileName = URLEncoder.encode(((CommonsMultipartFile) file).getFileItem().getName(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));

            inputStream = file.getInputStream();
            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.close();
            out.flush();
        } catch (IOException e) {
            logger.error("文件下载异常：文件名【%s】", file.getName(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 创建生成 excel 文档
     * @param fields 导出字段
     * @param data 导出数据
     * @param fileName 导出文档名称
     * @return 生成的 excel 文档
     */
    private static MultipartFile construct(String fields, List<Map<String, Object>> data, String fileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle cellStyle = createCellStyle(workbook);

        Map<String, Integer> fieldOrder = getFieldOrder(fields);
        // 头部占的行数
        int headerRows = calculateHeaderRows(fields);

        initHeader(sheet, cellStyle, fields, headerRows);
        fillData(workbook, sheet, cellStyle, data, fieldOrder, headerRows);

        DiskFileItem fileItem = (DiskFileItem)(new DiskFileItemFactory()).createItem("file", "text/plain", true, fileName);

        try {
            OutputStream os = fileItem.getOutputStream();
            Throwable var8 = null;

            try {
                workbook.write(os);
            } catch (Throwable var18) {
                var8 = var18;
                throw var18;
            } finally {
                if (os != null) {
                    if (var8 != null) {
                        try {
                            os.close();
                        } catch (Throwable var17) {
                            var8.addSuppressed(var17);
                        }
                    } else {
                        os.close();
                    }
                }

            }
        } catch (Exception var20) {
            throw new IllegalArgumentException("Invalid file: " + var20, var20);
        }

        return new CommonsMultipartFile(fileItem);
    }

    /**
     * 初始化表头
     * @param sheet sheet页
     * @param cellStyle 单元格样式
     * @param fields 导出字段
     * @param headerRows 头部所占的行数
     */
    private static void initHeader(Sheet sheet, CellStyle cellStyle, String fields, int headerRows) {
        // 构造表头
        Row row1 = sheet.createRow(0);

        Row row2 = null;
        if (headerRows > 1) {
            row2 = sheet.createRow(1);
        }

        String[] field = fields.split(",");
        // 上一个合并的表头名称
        String preMergeName = null;
        // 合并的起止索引
        int startIdx = 0,endIdx = -1;

        for (int i = 0; i < field.length; i++) {
            String[] f = field[i].split("\\|");
            Cell c1 = row1.createCell(i);
            c1.setCellStyle(cellStyle);
            c1.setCellValue(f[1]);

            if (f.length > 2) {
                Cell c2 = row2.createCell(i);
                c2.setCellStyle(cellStyle);
                c2.setCellValue(f[f.length - 1]);

                if (preMergeName == null) {
                    preMergeName = f[1];
                    startIdx = i;
                    endIdx = startIdx;
                } else if (preMergeName.equals(f[1])){
                    endIdx++;
                } else {
                    mergeRegion(sheet, 0, 0, startIdx, endIdx);
                    preMergeName = f[1];
                    startIdx = i;
                    endIdx = startIdx;
                }
            } else {
                if (headerRows > 1) {
                    mergeRegion(sheet, 0, 1, i, i);
                }

                if (preMergeName != null) {
                    mergeRegion(sheet, 0, 0, startIdx, endIdx);
                    preMergeName = null;
                }
            }

            // 处理最后一次循环
            if (i == field.length -1) {
                if (preMergeName != null) {
                    mergeRegion(sheet, 0, 0, startIdx, endIdx);
                }
            }
        }
    }

    /**
     * 计算表头需要占用的行数
     * @param fields 导出字段
     * @return 表头需要占用的行数
     */
    private static int calculateHeaderRows(String fields) {
        int rowMax = 0;
        int rowCount = 0;
        char[] chars = fields.toCharArray();
        for (char c : chars) {
            if (c == '|') {
                rowCount ++;
            } else if (c == ',') {
                if (rowCount > rowMax) {
                    rowMax = rowCount;
                }
                rowCount = 0;
            }
        }

        return rowMax;
    }

    /**
     * 填充数据
     * @param workbook workbook文档
     * @param sheet sheet页
     * @param cellStyle 单元格样式
     * @param data 待填充的数据
     * @param fieldOrder 字段填充顺序
     * @param headerRows 头部占的行数
     */
    private static void fillData(Workbook workbook, Sheet sheet, CellStyle cellStyle, List<Map<String, Object>> data, Map<String, Integer> fieldOrder, int headerRows) {
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(headerRows + i);
            data.get(i).forEach((key, value) -> {
                // 为了保证只导出 fields 中的字段，这里必须加这个判断
                if (fieldOrder.get(key) != null) {
                    Cell cell = row.createCell(fieldOrder.get(key));
                    cell.setCellStyle(cellStyle);
                    if (value != null) {
                        if (value instanceof Image) {
                            setCellPicture(workbook, sheet, cell, (Image) value);
                        } else {
                            cell.setCellValue(String.valueOf(value));
                        }
                    } else {
                        cell.setCellValue("");
                    }
                }
            });
        }
    }

    /**
     * 设置单元格图片（图片是悬浮的）
     * @param workbook workbook文档
     * @param sheet sheet页
     * @param cell 单元格
     * @param image 图片
     */
    private static void setCellPicture(Workbook workbook, Sheet sheet, Cell cell, Image image) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        // 绘图对象
        Drawing<?> patriarch = sheet.createDrawingPatriarch();
        // 创建锚点
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, columnIndex, rowIndex, columnIndex + 1, rowIndex + 1);
        patriarch.createPicture(anchor, workbook.addPicture(image.getImageData(), XSSFWorkbook.PICTURE_TYPE_JPEG));
    }

    /**
     * 得到字段的行索引
     * @param fields 字段
     * @return 字段索引map
     */
    private static Map<String, Integer> getFieldOrder(String fields) {
        Map<String, Integer> fieldOrder = new HashMap<>(16);

        String[] field = fields.split(",");
        for (int i = 0; i < field.length; ++i) {
            String[] f = field[i].split("\\|");
            fieldOrder.put(f[0], i);
        }

        return fieldOrder;
    }

    /**
     * 创建自定义单元格样式
     * @param workbook 工作簿
     */
    private static CellStyle createCellStyle(Workbook workbook) {
        // 为单元格设置边框线
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        // 居中显示
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }

    /**
     * 合并区域
     * @param sheet sheet页
     * @param firstRow 起始行
     * @param lastRow 结束行
     * @param firstCol 起始列
     * @param lastCol 结束列
     */
    private static void mergeRegion(Sheet sheet,int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow == lastRow && firstCol == lastCol) {
            return;
        }

        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cra);
        // 为合并后的单元格添加边框线
        RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
    }

    /**
     * 转换时间格式
     * @param data 导出数据
     * @param formatter 转换格式 formatter
     */
    private static void convertDateToStr(List<Map<String, Object>> data, String formatter) {
        for (Map<String, Object> datum : data) {
            for (Map.Entry<String, Object> stringObjectEntry : datum.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                if (value != null) {
                    if (value instanceof Date) {
                        datum.put(key, DateFormatUtils.format((Date) value, formatter));
                    } else if (value instanceof LocalDateTime) {
                        datum.put(key, ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(formatter)));
                    }
                }
            }
        }
    }
}