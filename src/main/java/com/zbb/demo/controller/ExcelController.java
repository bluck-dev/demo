package com.zbb.demo.controller;


import com.zbb.demo.entity.Student;
import com.zbb.demo.util.ExcelUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("excel")
public class ExcelController {

    public static void main(String[] args) {

        int[] a = {17, 41, 55, 65, 85};
        int[] b = {12, 45, 57, 68, 84};
        int[] c = new int[a.length + b.length];

        String aa ="1";
        int i1 = Integer.parseInt(aa);
        System.out.println("转换后得:"+i1);
//src是源数组
//srcPos是源数组复制的起始位置
//dest是目标数组
//destPos是目标数组接收复制数据的起始位置
//length是复制的长度(源数组中从复制起始位置srcPos开始需要复制的长度)
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        for (int i = 0; i < c.length; i++) {

        }
        ArrayList< Object> arrayList = new ArrayList<Object>();
        Collections.addAll(arrayList, c);
        arrayList.stream().forEach(System.out::println);


        int i2 = 0;
        List<int[]> ints = Arrays.asList(c);
        System.out.println(ints);
        for (int i : c) {
            i2 += i;
            System.out.print(i+"  ");
        }
        System.out.println("新数组:" + i2);



//        Map<String, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("张三","2");
//        objectObjectHashMap.put("李四","6");
//        objectObjectHashMap.put("王五","4");
//        List<Map<String, Object>> mapList = new ArrayList<>();
//        mapList.add(objectObjectHashMap);
//
//        for (Map<String, Object> stringObjectMap : mapList) {
//            System.out.println("遍历list:"+stringObjectMap);
//        }

//        Student student =new Student();
//        student.setName("XXX ");
//        student.setResult("95");
//        student.setClazz("二班");
//        student.setGrade("五年级");
//        student.setNumber("66");
//        List<Student> list =new ArrayList<>();
//        list.add(student);
//        list.add(student);
//        list.add(student);
//        testExcelDemo(list);
    }
    @GetMapping("/exceltest")
    public void testRespose(HttpServletResponse response) throws IOException {

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        File file = new File("D:\\壁纸\\1.jpg");
        BufferedImage bufferImg = ImageIO.read(file);
        ImageIO.write(bufferImg, file.getName().substring(file.getName().lastIndexOf(".") + 1), byteArrayOut);

        Map<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("exu001","2");
        objectObjectHashMap.put("sub002","6");
        objectObjectHashMap.put("sub004","4");
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(objectObjectHashMap);
        mapList.get(0).put("remark",new ExcelUtils.Image(byteArrayOut.toByteArray()));

//        String exportFields = "date|时间,firstHeader|表头1,secondHeader|表头2," +
//                "sub001|编号-0001|子标题1,sub002|编号-0001|子标题2,sub003|编号-0001|子标题3,sub004|编号-0001|子标题4,remark|备注";
        String exportFields = "date|序号,firstHeader|单位名字," +
                "exu001|签署人信息|中心主任,exu002|签署人信息|中心副主任,"+
                "sub001|邀请函联系人|姓名,sub002|邀请函联系人|电话,sub003|邀请函联系人|传真,sub004|邀请函联系人|电子邮件,remark|备案领导人签署样式,danweiz|单位印章";
//        String exportFields = "date|时间,firstHeader|表头1,secondHeader|表头2,sub001|签署人信息|中心主任,sub002|签署人信息|中心副主任,sub003," +
//                "sub001|编号-0001|子标题1,sub002|编号-0001|子标题2,sub003|编号-0001|子标题3,sub004|编号-0001|子标题4,remark|备注";
        ExcelUtils.export(response, "testExcel", mapList, exportFields, "yyyy-MM-dd HH:mm");
    }
    /**
     *
     * @param list 需要写入excel的数据 从数据库或者其他途径读取
     */
    public static void testExcelDemo(List<Student> list) {
        /** 第一步，创建一个Workbook，对应一个Excel文件  */
        XSSFWorkbook wb = new XSSFWorkbook();
        /** 第二步，在Workbook中添加一个sheet,对应Excel文件中的sheet  */
        XSSFSheet sheet = wb.createSheet("excel导出标题");
        /** 第三步，设置样式以及字体样式*/
        XSSFCellStyle titleStyle = createTitleCellStyle(wb);
        XSSFCellStyle headerStyle = createHeadCellStyle(wb);
        XSSFCellStyle contentStyle = createContentCellStyle(wb);
        /** 第四步，创建标题 ,合并标题单元格 */
        // 行号
        int rowNum = 0;
        // 创建第一页的第一行，索引从0开始
        XSSFRow row0 = sheet.createRow(rowNum++);
        row0.setHeight((short) 800);// 设置行高

        String title = "excel导出标题";
        XSSFCell c00 = row0.createCell(0);
        c00.setCellValue(title);
        c00.setCellStyle(titleStyle);
        // 合并单元格，参数依次为起始行，结束行，起始列，结束列 （索引0开始）
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));//标题合并单元格操作，6为总列数
        //第二行
        XSSFRow row2 = sheet.createRow(rowNum++);
        row2.setHeight((short) 700);
        String[] row_third = {"序号", "单位名字", "签署人备信息", "邀请函联系人", "备案领导签署样式","单位印章"};
        for (int i = 0; i < row_third.length; i++) {
            //sheet.setColumnWidth(i, 256*30); //设置列宽度
            XSSFCell tempCell = row2.createCell(i);
            tempCell.setCellValue(row_third[i]);
            tempCell.setCellStyle(headerStyle);
        }
//第三行
        XSSFRow row3 = sheet.createRow(rowNum++);
        row3.setHeight((short) 700);
        String[] row_third3 = {"中心主任", "中心副主任", "姓名", "电话", "传真","电子邮件"};
        for (int i = 0; i < row_third3.length; i++) {
            //sheet.setColumnWidth(i, 256*30); //设置列宽度
            XSSFCell tempCell3 = row3.createCell(i);
            tempCell3.setCellValue(row_third3[i]);
            tempCell3.setCellStyle(headerStyle);
        }
//        写入数据
//        for (Student student : list) {
//            XSSFRow tempRow = sheet.createRow(rowNum++);
//            tempRow.setHeight((short) 500);
//            // 循环单元格填入数据
//            for (int j = 0; j < 5; j++) {
//                XSSFCell tempCell = tempRow.createCell(j);
//                tempCell.setCellStyle(contentStyle);
//                String tempValue = "";
//                if (j == 0) {
//                    // 学号
//                    tempValue = student.getNumber();
//                } else if (j == 1) {
//                    // 姓名
//                    tempValue = student.getName();
//                } else if (j == 2) {
//                    // 年级
//                    tempValue = student.getGrade();
//                } else if (j == 3) {
//                    // 班级
//                    tempValue = student.getClazz();
//                } else if (j == 4) {
//                    // 成绩
//                    tempValue = student.getResult();
//                }
//                tempCell.setCellValue(tempValue);
//            }
//        }
        String filePath = "D:\\1\\Users\\leovo\\Desktop\\";
        String fileName = "testExcelDemo.xlsx";
        File file = new File(filePath + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // 写入磁盘
            wb.write(fos);
            fos.close();//记得关闭
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 创建标题样式
     * @param wb
     * @return
     */
    private static XSSFCellStyle createTitleCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直对齐
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());//背景颜色

        XSSFFont headerFont1 = (XSSFFont) wb.createFont(); // 创建字体样式
        headerFont1.setBold(true); //字体加粗
        headerFont1.setFontName("黑体"); // 设置字体类型
        headerFont1.setFontHeightInPoints((short) 15); // 设置字体大小
        cellStyle.setFont(headerFont1); // 为标题样式设置字体样式
        return cellStyle;
    }

    /**
     * 创建表头样式
     * @param wb
     * @return
     */
    private static XSSFCellStyle createHeadCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());//背景颜色
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        XSSFFont headerFont = (XSSFFont) wb.createFont(); // 创建字体样式
        headerFont.setBold(true); //字体加粗
        headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 12); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式

        return cellStyle;
    }

    /**
     * 创建内容样式
     * @param wb
     * @return
     */
    private static XSSFCellStyle createContentCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        // 生成12号字体
        XSSFFont font = wb.createFont();
        font.setColor((short)8);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }

}
