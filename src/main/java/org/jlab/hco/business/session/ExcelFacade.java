package org.jlab.hco.business.session;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jlab.hco.persistence.entity.Category;
import org.jlab.hco.persistence.entity.SystemEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ryans
 */
@Stateless
public class ExcelFacade {

    @EJB
    CategoryFacade categoryFacade;

    private Sheet sheet = null;
    private int rownum = 0;

    public void exportCategoriesAndSubsystems(OutputStream out) throws IOException {
        Workbook wb = new XSSFWorkbook();
        sheet = wb.createSheet("Categories and Subsystems");

        Category root = categoryFacade.findRootWithChildren();

        rownum = 0;

        recursivePrintCategory(root, 0);

        wb.write(out);
    }

    private void recursivePrintCategory(Category category, int indent) {
        Row row = sheet.createRow(rownum++);
        int cellnum = 0;
        for (int i = 0; i < indent; i++) {
            row.createCell(cellnum++);
        }
        row.createCell(cellnum).setCellValue(category.getName());
        indent++;
        for (Category child : category.getCategoryList()) {
            recursivePrintCategory(child, indent);
        }
        for (SystemEntity child : category.getSystemList()) {
            row = sheet.createRow(rownum++);
            cellnum = 0;
            for (int i = 0; i < indent; i++) {
                row.createCell(cellnum++);
            }
            row.createCell(cellnum).setCellValue(child.getName());

            // Component count
            row = sheet.createRow(rownum++);
            cellnum = 0;
            for (int i = 0; i < indent + 1; i++) {
                row.createCell(cellnum++);
            }
            row.createCell(cellnum).setCellValue(child.getComponentList().size() + " Components");
        }
    }
}
