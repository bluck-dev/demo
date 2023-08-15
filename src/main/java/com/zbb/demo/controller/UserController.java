package com.zbb.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.zbb.demo.entity.User;
import com.zbb.demo.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@RequestMapping("user")
public class UserController {


    @Resource
    private UserService userService;

    @GetMapping("/findAll")
    @ResponseBody
    public List<User> findAll() {
         return userService.findAll();
    }

    @GetMapping("/findOne")
    @ResponseBody
    public User findOne(Integer id) {
        return userService.findOne(id);
    }

    @PostMapping("/add")
    @ResponseBody
    public String add(@RequestBody User user) {
        userService.insertUser(user);
        return "添加成功";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return "修改成功";
    }

    @PostMapping("/outExcelxsl")
    @ResponseBody
    public String outExcelxsl(@RequestBody User user) {
        return "修改成功";
    }

    public static void exportMain(String templatePath){

//        //获取模板
//        File file = new File(templatePath);
//
//        InputStream is = null;
//
//        XSSFWorkbook wb = null;
//
//        XSSFSheet sheet = null;
//
//        InputStream exportInput = null;
//
//        try {
//
//            is = new FileInputStream(file);// 将excel文件转为输入流
//
//            wb = new XSSFWorkbook(is);// 创建个workbook，
//
//            // 获取第一个sheet
//            sheet = wb.getSheetAt(0);
//
//            //例子是第1行开始表头
//            XSSFRow row3 = sheet.getRow(0);
//
//            //获取总列数
//            int cellNum = row3.getLastCellNum();
//
//            //获取第四行的列
//            for (int i=0;i<cellNum;i++){
//                XSSFCell cell = row3.getCell(i);
//                System.out.println("第1行第"+(i+1)+"列："+cell.toString());
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
            //获取模板
            File file = new File(templatePath);

            InputStream is = null;

            XSSFWorkbook wb = null;

            XSSFSheet sheet = null;

            InputStream exportInput = null;

            try {

                is = new FileInputStream(file);// 将excel文件转为输入流

                wb = new XSSFWorkbook(is);// 创建个workbook，

                // 获取第一个sheet
                sheet = wb.getSheetAt(0);
                //获取表头
                Row btRow = sheet.getRow(0);
                //获取总列数
                int totalCellNum = btRow.getLastCellNum();
                //获取单元格合并情况数
                int a =sheet.getNumMergedRegions();
                System.out.println("合并情况数:"+a);

//单元格合并情况
//                CellRangeAddress regiona = sheet.getMergedRegion(a);
//                int q= regiona.getFirstRow();//合并开始行
//                                    System.out.println("合并开始行"+q);
//                int w = regiona.getFirstColumn();//合并开始列
//                                    System.out.println("合并开始列"+w);
//                int e = regiona.getLastColumn();//合并结束列
//                                    System.out.println("合并结束列"+e);
//                int r = regiona.getLastRow();//合并结束行
//                                    System.out.println("合并结束行"+r);


                //存储表头合并部分
                Map<String,String> newBtMap = new HashMap<>();
                //获取合并部分
                for (int i=0;i<sheet.getNumMergedRegions();i++){
                    CellRangeAddress region = sheet.getMergedRegion(i);
                    int firstRow = region.getFirstRow();//合并开始行
//                    System.out.println("合并开始行"+firstRow);
                    int firstColumn = region.getFirstColumn();//合并开始列
//                    System.out.println("合并开始列"+firstColumn);
                    int lastColumn = region.getLastColumn();//合并结束列
//                    System.out.println("合并结束列"+lastColumn);
                    int lastRow = region.getLastRow();//合并结束行
//                    System.out.println("合并结束行"+lastRow);
                    //锁定表头且是列合并的，只是列合并的不管
//                    if (firstRow ==0 || firstRow>1 || (lastColumn-firstColumn==0 && lastRow-firstRow>0)){
//                        continue;
//                    }
                    if (firstRow ==1 || (lastColumn-firstColumn==0 && lastRow-firstRow>0)){
                        continue;
                    }

                    Row row = sheet.getRow(firstRow);
                    Cell cell = row.getCell(firstColumn);
                    String newBtName = cell.toString().trim();
                    System.out.println("开始列："+ firstColumn +"，结束列："+ lastColumn +"，开始行："+firstRow+"，结束行："+ lastRow+"，值："+newBtName);
                    if (StringUtils.isEmpty(newBtName)){
                        continue;
                    }
                    for (int j=firstColumn;j<=lastColumn;j++){
                        String oldBtName = newBtMap.get(String.valueOf(j));
                        String newScBtName = "";
                        if (!StringUtils.isEmpty(oldBtName)){
                            newScBtName = oldBtName.trim() + "-";
                        }
                        newScBtName += newBtName ;
                        newBtMap.put(String.valueOf(j),newScBtName.replaceAll("\\s*|\r|\n|\t",""));
                    }
                }
                System.out.println("合并表头："+ JSONObject.toJSONString(newBtMap));


                //取出无合并情况的表头（4-6行）
                Map<String,String> btMap = new HashMap<>();
                for (int j=1;j<2;j++){
                    Row row = sheet.getRow(j);
                    for (int i=0;i<totalCellNum;i++){
                        Cell cell = row.getCell(i);
                        if (StringUtils.isEmpty(cell.toString())){
                            continue;
                        }
                        String cellValue = cell.toString().trim().replaceAll("\\s*|\r|\n|\t","");
                        System.out.println("第"+(j+1)+"行第"+(i+1)+"列数据："+ cellValue);
                        btMap.put(String.valueOf(i),cellValue);
                    }
                }

                System.out.println("无合并情况表头："+JSONObject.toJSONString(btMap));

                //合并所有表头
                //遍历拼接表头去拼接全部数据
                Set<String> newBtKeys = newBtMap.keySet();
                for (String key : newBtKeys){
                    //最后一行表头
                    String lastBtName = btMap.get(key);
                    //除最后一行外的表头
                    String cLastBtName = newBtMap.get(key);
                    //最全的表头
                    String allBtName = cLastBtName;
                    if (!StringUtils.isEmpty(lastBtName) && !cLastBtName.equals(lastBtName)){
                        allBtName += "-"+lastBtName;
                    }
                    btMap.put(key,allBtName);
                }

                System.out.println("导出模板拼接表头："+JSONObject.toJSONString(btMap));

            }catch (Exception e){
                e.printStackTrace();
            }
//            //例子是第四行开始表头
//            XSSFRow row3 = sheet.getRow(3);
//
//            //获取总列数
//            int cellNum = row3.getLastCellNum();
//
//            //获取第四行的列
//            for (int i=0;i<cellNum;i++){
//                XSSFCell cell = row3.getCell(i);
//                System.out.println("第4行第"+(i+1)+"列："+cell.toString());
//            }

        }


    public static void main(String[] args) {
        exportMain("D:\\1\\Users\\leovo\\Desktop\\工作簿1.xlsx");
    }



}
